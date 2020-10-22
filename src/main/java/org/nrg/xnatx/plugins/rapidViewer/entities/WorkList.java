/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.entities;

import java.util.Date;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.nrg.framework.orm.hibernate.AbstractHibernateEntity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;


@Entity
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Accessors(prefix = "_", chain = true)
@Cache(usage = CacheConcurrencyStrategy.NONE, region = "nrg")
@ToString
public class WorkList extends AbstractHibernateEntity {
	private static final long serialVersionUID = 1L;

	public static enum WorkListStatus {
		InProgress, Open, Partial, Complete
	}

	@Converter(autoApply = true)
	public static class WorkListStatusConverter implements AttributeConverter<WorkListStatus, String> {

		@Override
		public String convertToDatabaseColumn(WorkListStatus status) {
			if (status == null) {
				return null;
			}
			return status.toString();
		}

		@Override
		public WorkListStatus convertToEntityAttribute(String code) {
			if (code == null) {
				return null;
			}

			for (WorkListStatus status : WorkListStatus.values()) {
				if (code.equals(status.toString())) {
					return status;
				}
			}

			throw new IllegalArgumentException();
		}
	}

	@NotNull
	private Integer _ownerXdatUserId;

	@NotNull
	private String _name;

	private String _description;

	@Enumerated(EnumType.STRING)
	private WorkListStatus _status;

	@NotNull
	private Date _dueDate;

	@NotNull
	private String _reportId;
	
	private Boolean _formDisabledWhenComplete = true;
	
	private List<WorkItem> _items;

	@JsonManagedReference
	@OneToMany(targetEntity = WorkItem.class, mappedBy = "workList", fetch = FetchType.EAGER)
	public List<WorkItem> getItems() {
		return this._items;
	}
	
	public void preCreate() {
		if (_status == null) {
			_status = WorkListStatus.InProgress;
		}
	}
}
