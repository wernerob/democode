package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class HouseHoldDto implements Serializable {

	private static final long serialVersionUID = -8094978266893564572L;

	private String id;
	private String worklistInfo;
	private String ospec;
	private String succesfulPreviousVisit;
	private String initVisitCode;
	private String addressType;
	private String date;

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getWorklistInfo() {
		return this.worklistInfo;
	}

	public void setWorklistInfo(final String worklistInfo) {
		this.worklistInfo = worklistInfo;
	}

	public String getOspec() {
		return this.ospec;
	}

	public void setOspec(final String ospec) {
		this.ospec = ospec;
	}

	public String getInitVisitCode() {
		return this.initVisitCode;
	}

	public void setInitVisitCode(final String initVisitCode) {
		this.initVisitCode = initVisitCode;
	}

	public String getAddressType() {
		return this.addressType;
	}

	public void setAddressType(final String addressType) {
		this.addressType = addressType;
	}

	public String getDate() {
		return this.date;
	}

	public void setDate(final String date) {
		this.date = date;
	}

	public String getSuccesfulPreviousVisit() {
		return this.succesfulPreviousVisit;
	}

	public void setSuccesfulPreviousVisit(final String succesfulPreviousVisit) {
		this.succesfulPreviousVisit = succesfulPreviousVisit;
	}

}
