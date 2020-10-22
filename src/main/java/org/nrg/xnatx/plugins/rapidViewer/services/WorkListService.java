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

    List<WorkList> findByOwnerXdatUserId(final Integer ownerXdatUserId);
    
    List<WorkList> findByOwnerXdatUserIdAndStatus(final Integer ownerXdatUserId, WorkListStatus status);
}
