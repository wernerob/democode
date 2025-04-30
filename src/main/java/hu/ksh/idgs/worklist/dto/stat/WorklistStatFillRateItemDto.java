package hu.ksh.idgs.worklist.dto.stat;

import java.io.Serializable;

import hu.ksh.idgs.core.value.FormStatusType;

public class WorklistStatFillRateItemDto implements Serializable {

	private static final long serialVersionUID = 5959321640306660999L;

	private FormStatusType formStatus;
	private Long number;

	public WorklistStatFillRateItemDto() {
	}

	public WorklistStatFillRateItemDto(final FormStatusType formStatus, final Long number) {
		this.formStatus = formStatus;
		this.number = number;
	}

	public FormStatusType getFormStatus() {
		return this.formStatus;
	}

	public void setFormStatus(final FormStatusType formStatus) {
		this.formStatus = formStatus;
	}

	public Long getNumber() {
		return this.number;
	}

	public void setNumber(final Long number) {
		this.number = number;
	}

}
