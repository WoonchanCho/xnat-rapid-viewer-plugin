/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.services.impl;

import java.util.List;

import org.nrg.framework.orm.hibernate.AbstractHibernateEntityService;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem.WorkItemStatus;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.repositories.WorkItemRepository;
import org.nrg.xnatx.plugins.rapidViewer.services.WorkItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages {@link WorkList} data objects in Hibernate.
 */
@Service
@Slf4j
public class HibernateWorkItemService extends AbstractHibernateEntityService<WorkItem, WorkItemRepository>
		implements WorkItemService {
	@Transactional
	@Override
	public WorkItem findById(Long id) {
		log.trace("Requested WorkListItem with ID \"{}\"", id);
		return getDao().findByUniqueProperty("id", id);
	}

	@Transactional
	@Override
	public List<WorkItem> getWorkItems(Long workListId) {
		return _dao.getWorkItems(workListId);
	}

	@Transactional
	@Override
	public List<WorkItem> getWorkItemsByStatus(Long workListId, WorkItemStatus status) {
		return _dao.getWorkItemsByStatus(workListId, status);
	}

	@Autowired
	private WorkItemRepository _dao;
}
