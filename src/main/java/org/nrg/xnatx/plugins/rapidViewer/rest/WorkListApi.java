/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.rest;

import static org.nrg.xdat.security.helpers.AccessLevel.Admin;
import static org.nrg.xdat.security.helpers.AccessLevel.Null;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;
import org.nrg.framework.annotations.XapiRestController;
import org.nrg.xapi.exceptions.NotFoundException;
import org.nrg.xapi.rest.AbstractXapiRestController;
import org.nrg.xapi.rest.XapiRequestMapping;
import org.nrg.xdat.security.helpers.Roles;
import org.nrg.xdat.security.services.RoleHolder;
import org.nrg.xdat.security.services.UserManagementServiceI;
import org.nrg.xdat.security.user.exceptions.UserInitException;
import org.nrg.xdat.security.user.exceptions.UserNotFoundException;
import org.nrg.xft.security.UserI;
import org.nrg.xnatx.plugins.rapidViewer.dto.WorkItemDto;
import org.nrg.xnatx.plugins.rapidViewer.dto.WorkItemValidationDto;
import org.nrg.xnatx.plugins.rapidViewer.dto.WorkListDto;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem.WorkItemStatus;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;
import org.nrg.xnatx.plugins.rapidViewer.etc.RapidViewerUserType;
import org.nrg.xnatx.plugins.rapidViewer.exceptions.AlreadyExistsException;
import org.nrg.xnatx.plugins.rapidViewer.services.WorkItemService;
import org.nrg.xnatx.plugins.rapidViewer.services.WorkListService;
import org.nrg.xnatx.plugins.rapidViewer.services.impl.PermissionService;
import org.nrg.xnatx.plugins.rapidViewer.utils.PluginException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

@Api
@XapiRestController
@RequestMapping(value = "/workLists")
@Slf4j
public class WorkListApi extends AbstractXapiRestController {
	@Autowired
	protected WorkListApi(final UserManagementServiceI userManagementService, final RoleHolder roleHolder,
			final WorkListService workListService, final WorkItemService workItemService,
			final PermissionService permissionService) {
		super(userManagementService, roleHolder);
		_userManagementService = userManagementService;
		_workListService = workListService;
		_workItemService = workItemService;
		_permissionService = permissionService;
	}

	private UserI readUser(String username) throws NotFoundException {
		UserI user = null;
		try {
			user = _userManagementService.getUser(username);
		} catch (UserNotFoundException | UserInitException e) {
			new NotFoundException("Cannot find user information for " + username);
		}
		return user;
	}

	private UserI readUser(Integer xdatUserId) throws NotFoundException {
		UserI user = null;
		try {
			user = _userManagementService.getUser(xdatUserId);
		} catch (UserNotFoundException | UserInitException e) {
			new NotFoundException("Cannot find user information for xdat-user-id " + xdatUserId);
		}
		return user;
	}

	@ApiOperation(value = "Always returns true when this pluging is installed on the XNAT")
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkLists successfully retrieved."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "check", produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public void checkifPluginExists()
			throws NotFoundException {
		log.trace("/workLists/check [GET] called");
	}
	
	@ApiOperation(value = "Returns a list of workLists of a user.", response = WorkList.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkLists successfully retrieved."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(produces = { MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public List<WorkList> getEntities(Principal principal,
			@ApiParam(value = "Status", required = false) @RequestParam(required = false) final WorkListStatus status)
//			@ApiParam(value = "Offset", required = false) @RequestParam(required = false) final Long offset,
//			@ApiParam(value = "Limit", required = false) @RequestParam(required = false) final Long limit)
			throws NotFoundException {
		log.trace("/workLists [GET] called [status={}]", status);
		UserI user = readUser(principal.getName());
		Integer ownerXdatUserId = user.getID();

		if (status == null) {
			return _workListService.findByOwnerXdatUserId(ownerXdatUserId);
		} else {

			return _workListService.findByOwnerXdatUserIdAndStatus(ownerXdatUserId, status);
		}
	}

	@ApiOperation(value = "Returns a list of all workLists.", response = WorkList.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkLists successfully retrieved."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "/all", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET, restrictTo = Admin)
	public List<WorkList> getAll(Principal principal,
			@ApiParam(value = "Username", required = false) @RequestParam(required = false) final String username,
			@ApiParam(value = "Status", required = false) @RequestParam(required = false) final WorkListStatus status)
			throws NotFoundException {
		log.trace("/workLists/all [GET] called [status={}]", status);
		return _workListService.getAll();
	}

	@ApiOperation(value = "Retrieves the indicated workList.", notes = "Based on the workList ID, not the primary key ID.", response = WorkList.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully retrieved."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public WorkList getEntity(@PathVariable final Long workListId) throws NotFoundException {
		log.trace("/workLists/{} [GET] called", workListId);
		if (!_workListService.exists("id", workListId)) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}
		WorkList workList = _workListService.findById(workListId);
		return workList;
	}

	@ApiOperation(value = "Creates a new workList.", response = WorkList.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully created."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, restrictTo = Admin)
	public WorkList createWorkList(Principal principal, @RequestBody WorkListDto dto)
			throws PluginException, NotFoundException {
		log.trace("/workLists [POST] called: {}", dto.toString());

		UserI ownerUser = readUser(dto.getOwnerUsername());
		WorkList workList = dto.toWorkList(ownerUser);
		workList.preCreate();
		return _workListService.create(workList);
	}

	@ApiOperation(value = "Updates the indicated workList.", notes = "Based on primary key ID, not subject or record ID.", response = Long.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully updated."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.PUT, restrictTo = Admin)
	public WorkList updateEntity(@PathVariable final Long workListId, @RequestBody final WorkListDto dto)
			throws NotFoundException {
		log.trace("/workLists/{} [PUT] called: {}", workListId, dto.toString());
		if (!_workListService.exists("id", workListId)) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}
		final WorkList existing = _workListService.findById(workListId);
		existing.setName(dto.getName());
		existing.setDescription(dto.getDescription());
		existing.setStatus(dto.getStatus());
		existing.setDueDate(dto.getDueDate());
		existing.setReportId(dto.getReportId());
		existing.preCreate();
		_workListService.update(existing);
		return existing;
	}

	@ApiOperation(value = "Deletes the indicated workList.", notes = "Based on primary key ID, not subject or record ID.", response = Long.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully deleted."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.DELETE, restrictTo = Admin)
	public long deleteEntity(@PathVariable final Long workListId) throws NotFoundException {
		log.trace("/workLists/{} [DELETE] called", workListId);
		final WorkList existing = _workListService.findById(workListId);
		if (existing == null) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}
		_workListService.delete(existing);
		return workListId;
	}

	@ApiOperation(value = "Returns a list of workItems of a workList.", response = WorkList.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkLists successfully retrieved."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}/items", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public List<WorkItem> getWorkListItems(Principal principal, @PathVariable final Long workListId,
			@ApiParam(value = "Status", required = false) @RequestParam(required = false) final WorkItemStatus status)
			throws NotFoundException {
		log.trace("/workLists/{}/items [GET] called [status={}]", workListId, status);
		UserI user = readUser(principal.getName());
		Integer ownerXdatUserId = user.getID();

		final WorkList workList = _workListService.findById(workListId);
		if (workList == null) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}

		if (!Roles.isSiteAdmin(user) && ownerXdatUserId != workList.getOwnerXdatUserId()) {
			throw new AccessDeniedException("Access to the ID \"" + workListId + "\" is forbidden");
		}

		if (status == null) {
			return _workItemService.findByWorkListId(workListId);
		} else {
			return _workItemService.findByWorkListIdAndStatus(workListId, status);
		}
	}

	@ApiOperation(value = "Returns a list of workItems of a workList.", response = WorkList.class, responseContainer = "List")
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkLists successfully retrieved."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}/items/validate", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.GET)
	public List<WorkItemValidationDto> validateWorkListItems(Principal principal, @PathVariable final Long workListId)
			throws NotFoundException {
		log.trace("/workLists/{}/items/validate [GET] called", workListId);
		String username = principal.getName();
		UserI user = readUser(username);
		Integer xdatUserId = user.getID();

		final WorkList workList = _workListService.findById(workListId);
		if (workList == null) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}

		if (!Roles.isSiteAdmin(user) && xdatUserId != workList.getOwnerXdatUserId()) {
			throw new AccessDeniedException("Access to the ID \"" + workListId + "\" is forbidden");
		}

		UserI ownerUser = readUser(workList.getOwnerXdatUserId());
		List<WorkItem> items = workList.getItems();
		List<WorkItemValidationDto> itemValidations = new ArrayList<WorkItemValidationDto>();

		for (WorkItem item : items) {
			String projectId = item.getProjectId();
			String subjectId = item.getSubjectId();
			String experimentId = item.getExperimentId();
			String experimentLabel = item.getExperimentLabel();

			boolean validated = true;
			String message = null;
			try {
				_permissionService.checkPermission(RapidViewerUserType.WorkListOwner, ownerUser, projectId, subjectId,
						experimentLabel, experimentId);
			} catch (Exception e) {
				validated = false;
				message = e.getMessage();
			}
			itemValidations.add(WorkItemValidationDto.createFromWorkItem(item, validated, message));
		}

		return itemValidations;
	}

	@ApiOperation(value = "Creates a new workItem.", response = WorkItem.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully created."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}/items", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.POST, restrictTo = Admin)
	public WorkItem createWorkListItem(Principal principal, @PathVariable Long workListId, @RequestBody WorkItemDto dto)
			throws PluginException, NotFoundException {
		log.trace("/{}/items [POST] called: {}", workListId, dto.toString());

		WorkList workList = _workListService.findById(workListId);
		if (workList == null) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}

		Integer ownerXdatUserId = workList.getOwnerXdatUserId();
		UserI ownerUser = readUser(ownerXdatUserId);

		String projectId = dto.getProjectId();
		String subjectId = dto.getSubjectId();
		String experimentId = dto.getExperimentId();
		String experimentLabel = dto.getExperimentLabel();

		log.debug(
				"Checking if the session [projectId: {}, subjectId: {}, experimentId: {}, experimentLabel: {}] can be added to the work list {} ",
				projectId, subjectId, experimentId, experimentLabel, workListId);
		_permissionService.checkPermission(RapidViewerUserType.WorkListOwner, ownerUser, projectId, subjectId,
				experimentId, experimentLabel);

		WorkItem item;
		try {
			item = _workItemService.create(dto.toWorkListItem(workListId));
		} catch (ConstraintViolationException e) {
			throw new AlreadyExistsException("session already exists");
		}
		return item;
	}

	@ApiOperation(value = "Updates the indicated workItem", notes = "Based on primary key ID, not subject or record ID.", response = Long.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully updated."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}/items/{workItemId}", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.PUT, restrictTo = Admin)
	public long updateWorkListItem(@PathVariable final Long workListId, @PathVariable final Long workItemId,
			@RequestBody final WorkItemDto dto) throws NotFoundException {
		log.trace("/{}/items/{} [PUT] called: {}", workListId, workItemId, dto.toString());
		if (!_workListService.exists("id", workListId)) {
			throw new NotFoundException("No workList with the ID \"" + workListId + "\" was found.");
		}

		final WorkItem existing = _workItemService.findById(workItemId);
		if (existing == null) {
			throw new NotFoundException("No workItem with the ID \"" + workItemId + "\" was found.");
		}

		existing.setProjectId(dto.getProjectId());
		existing.setSubjectId(dto.getSubjectId());
		existing.setExperimentId(dto.getExperimentId());
		existing.setExperimentLabel(dto.getExperimentLabel());
		_workItemService.update(existing);
		return workItemId;
	}

	@ApiOperation(value = "Deletes the indicated workList.", notes = "Based on primary key ID, not subject or record ID.", response = Long.class)
	@ApiResponses({ @ApiResponse(code = 200, message = "WorkList successfully deleted."),
			@ApiResponse(code = 401, message = "Must be authenticated to access the XNAT REST API."),
			@ApiResponse(code = 500, message = "Unexpected error") })
	@XapiRequestMapping(value = "{workListId}/items/{workItemId}", produces = {
			MediaType.APPLICATION_JSON_VALUE }, method = RequestMethod.DELETE, restrictTo = Admin)
	public long deleteWorkListItem(@PathVariable final Long workListId, @PathVariable final Long workItemId)
			throws NotFoundException {
		log.trace("/{}/items/{} [DELETE] called", workListId, workItemId);
		final WorkItem existing = _workItemService.findById(workItemId);
		if (existing == null) {
			throw new NotFoundException("No workItem with the ID \"" + workItemId + "\" was found.");
		}
		_workItemService.delete(existing);
		return workItemId;
	}

	private final UserManagementServiceI _userManagementService;
	private final WorkListService _workListService;
	private final WorkItemService _workItemService;
	private final PermissionService _permissionService;
}
