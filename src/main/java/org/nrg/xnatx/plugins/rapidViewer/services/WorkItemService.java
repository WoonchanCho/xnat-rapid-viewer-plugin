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
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem.WorkItemStatus;

public interface WorkItemService extends BaseHibernateService<WorkItem> {
    WorkItem findById(final Long id);

    List<WorkItem> findByWorkListId(final Long workListId);
    
    List<WorkItem> findByWorkListIdAndStatus(final Long workListId, WorkItemStatus status);
}
