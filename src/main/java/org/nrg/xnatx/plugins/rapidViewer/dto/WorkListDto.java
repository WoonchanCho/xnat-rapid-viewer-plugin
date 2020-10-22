/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.dto;

import java.util.Date;

import org.nrg.xft.security.UserI;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList.WorkListStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class WorkListDto {

	private Long id;

	private String ownerUsername;

	private String name;

	private String description;

	private WorkListStatus status;

	private Date dueDate;

	private String reportId;
	
	@Builder.Default
	private Boolean formDisabledWhenComplete = true;

	public WorkList toWorkList(UserI owner) {
		WorkList workList = new WorkList();
		workList.setOwnerXdatUserId(owner.getID()).setName(name).setDescription(description).setStatus(status)
				.setDueDate(dueDate).setReportId(reportId).setFormDisabledWhenComplete(formDisabledWhenComplete);
		if (id != null) {
			workList.setId(id);
		}

		return workList;
	}
}
