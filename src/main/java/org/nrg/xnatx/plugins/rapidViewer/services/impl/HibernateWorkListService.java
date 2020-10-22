/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;
import org.nrg.xnatx.plugins.rapidViewer.repositories.WorkListRepository;
import org.nrg.xnatx.plugins.rapidViewer.services.WorkListService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages {@link WorkList} data objects in Hibernate.
 */
@Service
@Slf4j
public class HibernateWorkListService extends AbstractHibernateEntityService<WorkList, WorkListRepository>
		implements WorkListService {
	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public WorkList findById(final Long id) {
		log.trace("Requested WorkList with ID \"{}\"", id);
		return getDao().findByUniqueProperty("id", id);
	}

	@Transactional
	@Override
	public List<WorkList> findByOwnerXdatUserId(final Integer ownerXdatUserId) {
		log.trace("Requested WorkList with ownerXdatUserId \"{}\"", ownerXdatUserId);
		List<WorkList> workLists = getDao().findByProperty("ownerXdatUserId", ownerXdatUserId);
		if (workLists == null) {
			workLists = new ArrayList<>();
		}
		return workLists;
	}

	@Transactional
	@Override
	public List<WorkList> findByOwnerXdatUserIdAndStatus(Integer ownerXdatUserId, WorkListStatus status) {
		log.trace("Requested WorkList with ownerXdatUserId \"{}\" and status \"{}\"", ownerXdatUserId, status);
		Map<String, Object> properties = new HashMap<>();
		properties.put("ownerXdatUserId", ownerXdatUserId);
		properties.put("status", status);
		List<WorkList> workLists = getDao().findByProperties(properties);
		if (workLists == null) {
			workLists = new ArrayList<>();
		}
		return workLists;
	}

//	private void setItems(List<WorkList> workLists) {
//		for (WorkList workList : workLists) {
//			List<WorkListItem> items = _itemService.findByWorkListId(workList.getId());
//			workList.setItems(items);
//
//		}
//	}
//
//	@Autowired
//	private WorkListItemService _itemService;
}
