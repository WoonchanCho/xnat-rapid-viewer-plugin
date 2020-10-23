/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.repositories;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem.WorkItemStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WorkItemRepository extends AbstractHibernateDAO<WorkItem> {
	@SuppressWarnings("unchecked")
	@Transactional
	public List<WorkItem> getWorkItems(Long workListId) {
		final Criteria criteria = getSession().createCriteria(getParameterizedType());
		criteria.add(Restrictions.eq("workList.id", workListId));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
	
	@SuppressWarnings("unchecked")
	@Transactional
	public List<WorkItem> getWorkItemsByStatus(Long workListId, WorkItemStatus status) {
		final Criteria criteria = getSession().createCriteria(getParameterizedType());
	    criteria.add(Restrictions.eq("workList.id", workListId));
	    criteria.add(Restrictions.eq("status", status));
		criteria.addOrder(Order.asc("id"));
		return criteria.list();
	}
}
