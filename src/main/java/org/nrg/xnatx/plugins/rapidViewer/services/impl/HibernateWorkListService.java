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
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;
import org.nrg.xnatx.plugins.rapidViewer.repositories.WorkListRepository;
import org.nrg.xnatx.plugins.rapidViewer.services.WorkListService;
import org.springframework.beans.factory.annotation.Autowired;
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

	@Override
	public List<WorkList> getWorkLists(Integer ownerXdatUserId, String orderBy, boolean isAsc, Integer offset,
			Integer limit) {
		return _dao.getWorkLists(ownerXdatUserId, orderBy, isAsc, offset, limit);
	}

	@Override
	public List<WorkList> getWorkListsByStatus(Integer ownerXdatUserId, WorkListStatus status, String orderBy,
			boolean isAsc, int offset, int limit) {
		return _dao.getWorkListsByStatus(ownerXdatUserId, status, orderBy, isAsc, offset, limit);
	}

	@Autowired
	private WorkListRepository _dao;
}
