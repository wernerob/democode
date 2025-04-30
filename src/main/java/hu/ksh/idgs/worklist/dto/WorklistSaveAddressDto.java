package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class WorklistSaveAddressDto implements Serializable {

	private static final long serialVersionUID = 4979431082930635806L;

	private String packageId;
	private WorklistUserDto interviewerUser;
	private WorklistZoneDto zone;
	private String authorization;

	public String getPackageId() {
		return this.packageId;
	}

	public void setPackageId(final String packageId) {
		this.packageId = packageId;
	}

	public WorklistUserDto getInterviewerUser() {
		return this.interviewerUser;
	}

	public void setInterviewerUser(final WorklistUserDto interviewerUser) {
		this.interviewerUser = interviewerUser;
	}

	public WorklistZoneDto getZone() {
		return this.zone;
	}

	public void setZone(final WorklistZoneDto zone) {
		this.zone = zone;
	}

	public String getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(final String authorization) {
		this.authorization = authorization;
	}

}
