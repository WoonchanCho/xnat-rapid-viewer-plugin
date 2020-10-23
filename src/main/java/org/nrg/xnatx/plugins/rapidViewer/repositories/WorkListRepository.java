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
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class WorkListRepository extends AbstractHibernateDAO<WorkList> {
	@SuppressWarnings("unchecked")
	@Transactional
	public List<WorkList> getWorkLists(final Integer ownerXdatUserId, String orderBy, boolean isAsc, Integer offset,
			Integer limit) {
		final Criteria criteria = getSession().createCriteria(getParameterizedType());
		criteria.add(Restrictions.eq("ownerXdatUserId", ownerXdatUserId));
		criteria.addOrder(isAsc ? Order.asc(orderBy) : Order.desc(orderBy));
		criteria.setFirstResult(offset);
		criteria.setMaxResults(limit);
		return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public List<WorkList> getWorkListsByStatus(final Integer ownerXdatUserId, final WorkListStatus status,
			String orderBy, boolean isAsc, Integer offset, Integer limit) {
		final Criteria criteria = getSession().createCriteria(getParameterizedType());
		criteria.add(Restrictions.eq("ownerXdatUserId", ownerXdatUserId));
		criteria.add(Restrictions.eq("status", status));
		criteria.addOrder(isAsc ? Order.asc(orderBy) : Order.desc(orderBy));
		criteria.setFirstResult(offset);
		criteria.setMaxResults(limit);
		return criteria.list();
	}
}
