/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.entities;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "workListId", "experimentId" }))
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(prefix = "_", chain = true)
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "nrg")
@ToString
public class WorkItem extends AbstractHibernateEntity {
	private static final long serialVersionUID = 1L;

	public static enum WorkItemStatus {
		InProgress, Open, Partial, Complete, Cancelled
	}

	@Converter(autoApply = true)
	public static class WorkListItemStatusConverter implements AttributeConverter<WorkItemStatus, String> {

		@Override
		public String convertToDatabaseColumn(WorkItemStatus status) {
			if (status == null) {
				return null;
			}
			return status.toString();
		}

		@Override
		public WorkItemStatus convertToEntityAttribute(String code) {
			if (code == null) {
				return null;
			}

			for (WorkItemStatus status : WorkItemStatus.values()) {
				if (code.equals(status.toString())) {
					return status;
				}
			}

			throw new IllegalArgumentException();
		}
	}

	private WorkList _workList;

	@NotNull
	private String _projectId;

	@NotNull
	private String _subjectId;

	@NotNull
	private String _experimentId;

	@NotNull
	private String _experimentLabel;

	@NotNull
	private WorkItemStatus _status;

	@JsonBackReference
	@ManyToOne
	@JoinColumn(name = "workListId", nullable = false)
	public WorkList getWorkList() {
		return _workList;
	}
}
