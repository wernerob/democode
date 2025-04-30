package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class WorklistAddressDto implements Serializable {

	private static final long serialVersionUID = 4117751791274448931L;

	private String externalAddressId;
	private AddressDto address;
	private WorklistUserDto selfLoaderUser;

	private Integer order;
	private OffsetDateTime selfLoaderCensusValidFrom;
	private OffsetDateTime selfLoaderCensusValidTo;
	private OffsetDateTime interviewerCensusValidFrom;
	private OffsetDateTime interviewerCensusValidTo;

	public String getExternalAddressId() {
		return this.externalAddressId;
	}

	public void setExternalAddressId(final String externalAddressId) {
		this.externalAddressId = externalAddressId;
	}

	public AddressDto getAddress() {
		return this.address;
	}

	public void setAddress(final AddressDto address) {
		this.address = address;
	}

	public WorklistUserDto getSelfLoaderUser() {
		return this.selfLoaderUser;
	}

	public void setSelfLoaderUser(final WorklistUserDto selfLoaderUser) {
		this.selfLoaderUser = selfLoaderUser;
	}

	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(final Integer order) {
		this.order = order;
	}

	public OffsetDateTime getSelfLoaderCensusValidFrom() {
		return this.selfLoaderCensusValidFrom;
	}

	public void setSelfLoaderCensusValidFrom(final OffsetDateTime selfLoaderCensusValidFrom) {
		this.selfLoaderCensusValidFrom = selfLoaderCensusValidFrom;
	}

	public OffsetDateTime getSelfLoaderCensusValidTo() {
		return this.selfLoaderCensusValidTo;
	}

	public void setSelfLoaderCensusValidTo(final OffsetDateTime selfLoaderCensusValidTo) {
		this.selfLoaderCensusValidTo = selfLoaderCensusValidTo;
	}

	public OffsetDateTime getInterviewerCensusValidFrom() {
		return this.interviewerCensusValidFrom;
	}

	public void setInterviewerCensusValidFrom(final OffsetDateTime interviewerCensusValidFrom) {
		this.interviewerCensusValidFrom = interviewerCensusValidFrom;
	}

	public OffsetDateTime getInterviewerCensusValidTo() {
		return this.interviewerCensusValidTo;
	}

	public void setInterviewerCensusValidTo(final OffsetDateTime interviewerCensusValidTo) {
		this.interviewerCensusValidTo = interviewerCensusValidTo;
	}

}
