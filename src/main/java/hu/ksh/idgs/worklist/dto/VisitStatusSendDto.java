package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.util.List;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;

public class VisitStatusSendDto implements Serializable {

	private static final long serialVersionUID = -1459612403288839163L;

	private String formTemplateId;
	private String interviewerUserId;
	private String interviewerUserName;
	private FormTemplateDetailedInfoDto formTemplateInfo;
	private List<VisitStatusSendItemDto> items;

	public VisitStatusSendDto() {
	}

	public VisitStatusSendDto(final String formTemplateId, final String interviewerUserId,
			final String interviewerUserName) {

		this.formTemplateId = formTemplateId;
		this.interviewerUserId = interviewerUserId;
		this.interviewerUserName = interviewerUserName;
	}

	public String getFormTemplateId() {
		return this.formTemplateId;
	}

	public void setFormTemplateId(final String formTemplateId) {
		this.formTemplateId = formTemplateId;
	}

	public String getInterviewerUserId() {
		return this.interviewerUserId;
	}

	public void setInterviewerUserId(final String interviewerUserId) {
		this.interviewerUserId = interviewerUserId;
	}

	public String getInterviewerUserName() {
		return this.interviewerUserName;
	}

	public void setInterviewerUserName(final String interviewerUserName) {
		this.interviewerUserName = interviewerUserName;
	}

	public FormTemplateDetailedInfoDto getFormTemplateInfo() {
		return this.formTemplateInfo;
	}

	public void setFormTemplateInfo(final FormTemplateDetailedInfoDto formTemplateInfo) {
		this.formTemplateInfo = formTemplateInfo;
	}

	public List<VisitStatusSendItemDto> getItems() {
		return this.items;
	}

	public void setItems(final List<VisitStatusSendItemDto> items) {
		this.items = items;
	}

}
