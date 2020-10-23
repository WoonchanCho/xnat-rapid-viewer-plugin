/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.services;

import java.util.List;

import org.nrg.framework.orm.hibernate.BaseHibernateService;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;

public interface WorkListService extends BaseHibernateService<WorkList> {
	WorkList findById(final Long id);

	public List<WorkList> getWorkLists(final Integer ownerXdatUserId, String orderBy, boolean isAsc, Integer offset,
			Integer limit);

	public List<WorkList> getWorkListsByStatus(final Integer ownerXdatUserId, final WorkListStatus status,
			String orderBy, boolean isAsc, int offset, int limit);
}
