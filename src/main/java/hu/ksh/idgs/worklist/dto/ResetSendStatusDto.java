package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.util.List;

public class ResetSendStatusDto implements Serializable {

	private static final long serialVersionUID = 4876576493443751101L;

	private List<String> externalIds;
	private String formCode;
	private Integer year;
	private String period;

	public List<String> getExternalIds() {
		return this.externalIds;
	}

	public void setExternalIds(final List<String> externalIds) {
		this.externalIds = externalIds;
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

}
