package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;

public class WorklistSendItemDto implements Serializable {

	private static final long serialVersionUID = -6326991092495405822L;

	private IdgsEntityId worklistId;
	private FormStatusType formStatus;
	private String lastVisitStatusCode;

	public WorklistSendItemDto() {
	}

	public WorklistSendItemDto(final IdgsEntityId worklistId, final FormStatusType formStatus,
			final String lastVisitStatusCode) {
		this.worklistId = worklistId;
		this.formStatus = formStatus;
		this.lastVisitStatusCode = lastVisitStatusCode;
	}

	public IdgsEntityId getWorklistId() {
		return this.worklistId;
	}

	public void setWorklistId(final IdgsEntityId worklistId) {
		this.worklistId = worklistId;
	}

	public FormStatusType getFormStatus() {
		return this.formStatus;
	}

	public void setFormStatus(final FormStatusType formStatus) {
		this.formStatus = formStatus;
	}

	public String getLastVisitStatusCode() {
		return this.lastVisitStatusCode;
	}

	public void setLastVisitStatusCode(final String lastVisitStatusCode) {
		this.lastVisitStatusCode = lastVisitStatusCode;
	}

}
