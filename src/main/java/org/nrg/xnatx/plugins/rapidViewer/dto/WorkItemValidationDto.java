package org.nrg.xnatx.plugins.rapidViewer.dto;

import org.nrg.xnatx.plugins.rapidViewer.entities.WorkItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class WorkItemValidationDto extends WorkItemDto {
	private boolean validated;
	
	private String message;
	
	public static WorkItemValidationDto createFromWorkItem(WorkItem item, boolean validated, String message) {
		WorkItemValidationDto itemValidation = new WorkItemValidationDto();
		itemValidation.setProjectId(item.getProjectId());
		itemValidation.setSubjectId(item.getSubjectId());
		itemValidation.setExperimentId(item.getExperimentId());
		itemValidation.setExperimentLabel(item.getExperimentLabel());
		itemValidation.setStatus(item.getStatus());
		
		itemValidation.setValidated(validated);
		itemValidation.setMessage(message);
		
		return itemValidation;
	}
}
