package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

import hu.ksh.idgs.worklist.service.impl.VisitStatusSendField;

public class VisitStatusSendFieldDto implements Serializable {

	private static final long serialVersionUID = -2527735626176198718L;

	private VisitStatusSendField field;
	private String value;

	public VisitStatusSendFieldDto() {
	}

	public VisitStatusSendFieldDto(final VisitStatusSendField field, final String value) {
		this.field = field;
		this.value = value;
	}

	public VisitStatusSendField getField() {
		return this.field;
	}

	public void setField(final VisitStatusSendField field) {
		this.field = field;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

}
