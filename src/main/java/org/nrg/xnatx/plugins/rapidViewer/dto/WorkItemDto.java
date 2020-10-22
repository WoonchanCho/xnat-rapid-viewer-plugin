/*
 * XNAT http://www.xnat.org
 * Copyright (c) 2020, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 */

package org.nrg.xnatx.plugins.rapidViewer.dto;

import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem.WorkItemStatus;
import org.nrg.xnatx.plugins.rapidViewer.entities.WorkList;

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
public class WorkItemDto {

	private String projectId;

	private String subjectId;

	private String experimentId;

	private String experimentLabel;

	private WorkItemStatus status;

	public WorkItem toWorkListItem(Long workListId) {
		WorkItem item = new WorkItem();
		item.setProjectId(this.getProjectId()).setSubjectId(this.getSubjectId()).setExperimentId(this.getExperimentId())
				.setExperimentLabel(this.getExperimentLabel()).setStatus(this.getStatus());
		
		WorkList workList = new WorkList();
		workList.setId(workListId);
		item.setWorkList(workList);
		return item;
	}
}
