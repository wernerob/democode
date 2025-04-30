package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.model.value.SendStatusType;

//TODO interviewer user modification

public class WorklistAdminItemDto implements Serializable {

	private static final long serialVersionUID = 5664039894113640886L;

	private IdgsEntityId worklistItemId;
	private String formTemplateId;
	private String worklistAssociationExternalId;
	private AddressDto address;
	private String houseHoldId;
	private FormTemplateDetailedInfoDto formTemplateInfo;
	private FormStatusType formStatus;
	private OffsetDateTime censusStartDate;
	private OffsetDateTime censusEndDate;
	private String lastVisitStatusCode;
	private String interviewerUserName;
	private OffsetDateTime closeDate;
	private String succesfulPreviousVisit;
	private String lastNote;
	private SendStatusType sendStatus;

	public WorklistAdminItemDto() {
	}

	public WorklistAdminItemDto(final IdgsEntityId worklistItemId, final String formTemplateId,
			final String worklistAssociationExternalId, final FormStatusType formStatus, final String zipCode,
			final String settlementCode, final String settlement, final String placeName, final String placeType,
			final String houseNumber, final String parcelNumber, final String building, final String stairway,
			final String floor, final String door, final String houseHoldId, final String interviewerUserName,
			final OffsetDateTime closeDate, final String lastVisitStatusCode, final String lastNote,
			final String succesfulPreviousVisit, SendStatusType sendStatus) {

		this.worklistItemId = worklistItemId;
		this.formTemplateId = formTemplateId;
		this.worklistAssociationExternalId = worklistAssociationExternalId;
		this.formStatus = formStatus;
		this.address = new AddressDto(zipCode, settlementCode, settlement, placeName, placeType, houseNumber,
				parcelNumber, building, stairway, floor, door);
		this.houseHoldId = houseHoldId;
		this.interviewerUserName = interviewerUserName;
		this.closeDate = closeDate;
		this.lastVisitStatusCode = lastVisitStatusCode;
		this.lastNote = lastNote;
		this.succesfulPreviousVisit = succesfulPreviousVisit;
		this.sendStatus = sendStatus;
	}

	public String getLastNote() {
		return this.lastNote;
	}

	public void setLastNote(final String lastNote) {
		this.lastNote = lastNote;
	}

	public IdgsEntityId getWorklistItemId() {
		return this.worklistItemId;
	}

	public void setWorklistItemId(final IdgsEntityId worklistItemId) {
		this.worklistItemId = worklistItemId;
	}

	public String getFormTemplateId() {
		return this.formTemplateId;
	}

	public void setFormTemplateId(final String formTemplateId) {
		this.formTemplateId = formTemplateId;
	}

	public String getWorklistAssociationExternalId() {
		return this.worklistAssociationExternalId;
	}

	public void setWorklistAssociationExternalId(final String worklistAssociationExternalId) {
		this.worklistAssociationExternalId = worklistAssociationExternalId;
	}

	public AddressDto getAddress() {
		return this.address;
	}

	public void setAddress(final AddressDto address) {
		this.address = address;
	}

	public String getHouseHoldId() {
		return this.houseHoldId;
	}

	public void setHouseHoldId(final String houseHoldId) {
		this.houseHoldId = houseHoldId;
	}

	public FormTemplateDetailedInfoDto getFormTemplateInfo() {
		return this.formTemplateInfo;
	}

	public void setFormTemplateInfo(final FormTemplateDetailedInfoDto formTemplateInfo) {
		this.formTemplateInfo = formTemplateInfo;
	}

	public FormStatusType getFormStatus() {
		return this.formStatus;
	}

	public void setFormStatus(final FormStatusType formStatus) {
		this.formStatus = formStatus;
	}

	public OffsetDateTime getCensusStartDate() {
		return this.censusStartDate;
	}

	public void setCensusStartDate(final OffsetDateTime censusStartDate) {
		this.censusStartDate = censusStartDate;
	}

	public OffsetDateTime getCensusEndDate() {
		return this.censusEndDate;
	}

	public void setCensusEndDate(final OffsetDateTime censusEndDate) {
		this.censusEndDate = censusEndDate;
	}

	public String getLastVisitStatusCode() {
		return this.lastVisitStatusCode;
	}

	public void setLastVisitStatusCode(final String lastVisitStatusCode) {
		this.lastVisitStatusCode = lastVisitStatusCode;
	}

	public String getInterviewerUserName() {
		return this.interviewerUserName;
	}

	public void setInterviewerUserName(final String interviewerUserName) {
		this.interviewerUserName = interviewerUserName;
	}

	public OffsetDateTime getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(final OffsetDateTime closeDate) {
		this.closeDate = closeDate;
	}

	public String getSuccesfulPreviousVisit() {
		return this.succesfulPreviousVisit;
	}

	public void setSuccesfulPreviousVisit(final String succesfulPreviousVisit) {
		this.succesfulPreviousVisit = succesfulPreviousVisit;
	}

	public SendStatusType getSendStatus() {
		return sendStatus;
	}

	public void setSendStatus(SendStatusType sendStatus) {
		this.sendStatus = sendStatus;
	}
}
