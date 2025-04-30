package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class WorklistSendFilterDto implements Serializable {

	private static final long serialVersionUID = 3330558650939348997L;

	private String formCode;
	private Integer year;
	private String period;
	private Integer maxResult;
	private String authorization;

	public WorklistSendFilterDto() {
	}

	public WorklistSendFilterDto(final String formCode, final Integer year, final String period,
			final Integer maxResult, final String authorization) {
		this.formCode = formCode;
		this.year = year;
		this.period = period;
		this.maxResult = maxResult;
		this.authorization = authorization;
	}

	public String getFormCode() {
		return this.formCode;
	}

	public void setFormCode(final String formCode) {
		this.formCode = formCode;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(final Integer year) {
		this.year = year;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(final String period) {
		this.period = period;
	}

	public Integer getMaxResult() {
		return this.maxResult;
	}

	public void setMaxResult(final Integer maxResult) {
		this.maxResult = maxResult;
	}

	public String getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(final String authorization) {
		this.authorization = authorization;
	}

}
