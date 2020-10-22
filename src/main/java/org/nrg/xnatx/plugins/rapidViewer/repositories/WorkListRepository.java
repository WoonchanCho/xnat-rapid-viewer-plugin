/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.repositories;

import org.nrg.framework.orm.hibernate.AbstractHibernateDAO;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.springframework.stereotype.Repository;

@Repository
public class WorkListRepository extends AbstractHibernateDAO<WorkList> {
}
