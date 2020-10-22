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
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem.WorkItemStatus;
import org.nrg.xnatx.plugins.rapidViewer.repositories.WorkItemRepository;
import org.nrg.xnatx.plugins.rapidViewer.services.WorkItemService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages {@link WorkList} data objects in Hibernate.
 */
@Service
@Slf4j
public class HibernateWorkItemService extends AbstractHibernateEntityService<WorkItem, WorkItemRepository> implements WorkItemService {
	@Transactional
	@Override
	public WorkItem findById(Long id) {
        log.trace("Requested WorkListItem with ID \"{}\"", id);
        return getDao().findByUniqueProperty("id", id);
	}

	@Transactional
	@Override
	public List<WorkItem> findByWorkListId(Long workListId) {
		log.trace("Requested WorkListItem with workListId \"{}\"", workListId);
        List<WorkItem> items = getDao().findByProperty("workList.id", workListId);
		if (items == null) {
			items = new ArrayList<>();
        }
        return items;
	}

	@Transactional
	@Override
	public List<WorkItem> findByWorkListIdAndStatus(Long workListId, WorkItemStatus status) {
		log.trace("Requested WorkList with ownerXdatUserId \"{}\" and status \"{}\"", workListId, status);
        Map<String, Object> properties = new HashMap<>();
        properties.put("workList.id", workListId);
        properties.put("status", status);
    	List<WorkItem> items = getDao().findByProperties(properties);
		if (items == null) {
			items = new ArrayList<>();
        }
        return items;
	}
}
