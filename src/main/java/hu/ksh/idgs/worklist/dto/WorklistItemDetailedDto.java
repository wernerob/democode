package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;

public class WorklistItemDetailedDto implements Serializable {

	private static final long serialVersionUID = -4511369386592429620L;

	private IdgsEntityId worklistItemId;
	private String formTemplateId;
	private String worklistAssociationExternalId;
	private List<NoteDto> worklistNote;
	private AddressDto address;
	private String houseHoldId;
	private FormTemplateDetailedInfoDto formTemplateInfo;
	private FormStatusType formStatus;
	private OffsetDateTime censusStartDate;
	private OffsetDateTime censusEndDate;
	private String lastVisitStatusCode;
	private OffsetDateTime closeDate;
	private String worklistInfo;
	private String ospec;
	private String osapCode;
	private Integer osapYear;
	private String osapPeriod;
	private String successfulPreviousVisit;
	private String addressType;

	public WorklistItemDetailedDto() {
	}

	/**
	 *
	 * Constructor for interviewer
	 *
	 * @param worklistItemId
	 * @param formTemplateId
	 * @param worklistAssociationExternalId
	 * @param formStatus
	 * @param worklistNote
	 * @param zipCode
	 * @param settlementCode
	 * @param settlement
	 * @param placeName
	 * @param placeType
	 * @param houseNumber
	 * @param parcelNumber
	 * @param building
	 * @param stairway
	 * @param floor
	 * @param door
	 * @param houseHoldId
	 * @param closeDate
	 * @param lastVisitStatusCode
	 * @param worklistInfo
	 * @param ospec
	 * @param censusStartDate
	 * @param censusEndDate
	 * @param addressType
	 */
	public WorklistItemDetailedDto(final IdgsEntityId worklistItemId, final String formTemplateId,
			final String worklistAssociationExternalId, final FormStatusType formStatus, final String zipCode,
			final String settlementCode, final String settlement, final String placeName, final String placeType,
			final String houseNumber, final String parcelNumber, final String building, final String stairway,
			final String floor, final String door, final String houseHoldId, final OffsetDateTime closeDate,
			final String lastVisitStatusCode, final String worklistInfo, final String ospec,
			final OffsetDateTime censusStartDate, final OffsetDateTime censusEndDate, final String addressType,
			final String succesfulPreviousVisit) {

		this.worklistItemId = worklistItemId;
		this.formTemplateId = formTemplateId;
		this.worklistAssociationExternalId = worklistAssociationExternalId;
		this.formStatus = formStatus;

		this.address = new AddressDto(zipCode, settlementCode, settlement, placeName, placeType, houseNumber,
				parcelNumber, building, stairway, floor, door);
		this.houseHoldId = houseHoldId;
		this.closeDate = closeDate;
		this.lastVisitStatusCode = lastVisitStatusCode;
		this.worklistInfo = worklistInfo;
		this.ospec = ospec;
		this.censusStartDate = censusStartDate;
		this.censusEndDate = censusEndDate;
		this.addressType = addressType;
		this.successfulPreviousVisit = succesfulPreviousVisit;
	}

	/**
	 * Constructor for self loader query
	 *
	 * @param worklistItemId
	 * @param formTemplateId
	 */

	public WorklistItemDetailedDto(final IdgsEntityId worklistItemId, final String formTemplateId,
			final String worklistAssociationExternalId, final String zipCode, final String settlementCode,
			final String settlement, final String placeName, final String placeType, final String houseNumber,
			final String parcelNumber, final String building, final String stairway, final String floor,
			final String door, final String addressType, final String succesfulPreviousVisit) {

		this.worklistItemId = worklistItemId;
		this.formTemplateId = formTemplateId;
		this.worklistAssociationExternalId = worklistAssociationExternalId;
		this.address = new AddressDto(zipCode, settlementCode, settlement, placeName, placeType, houseNumber,
				parcelNumber, building, stairway, floor, door);
		this.addressType = addressType;
		this.successfulPreviousVisit = succesfulPreviousVisit;
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

	public List<NoteDto> getWorklistNote() {
		return this.worklistNote;
	}

	public void setWorklistNote(final List<NoteDto> worklistNote) {
		this.worklistNote = worklistNote;
	}

	public AddressDto getAddress() {
		return this.address;
	}

	public void setAddress(final AddressDto address) {
		this.address = address;
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

	public String getHouseHoldId() {
		return this.houseHoldId;
	}

	public void setHouseHoldId(final String houseHoldId) {
		this.houseHoldId = houseHoldId;
	}

	public String getLastVisitStatusCode() {
		return this.lastVisitStatusCode;
	}

	public void setLastVisitStatusCode(final String lastVisitStatusCode) {
		this.lastVisitStatusCode = lastVisitStatusCode;
	}

	public OffsetDateTime getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(final OffsetDateTime closeDate) {
		this.closeDate = closeDate;
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

	public String getOsapCode() {
		return this.osapCode;
	}

	public void setOsapCode(final String osapCode) {
		this.osapCode = osapCode;
	}

	public Integer getOsapYear() {
		return this.osapYear;
	}

	public void setOsapYear(final Integer osapYear) {
		this.osapYear = osapYear;
	}

	public String getOsapPeriod() {
		return this.osapPeriod;
	}

	public void setOsapPeriod(final String osapPeriod) {
		this.osapPeriod = osapPeriod;
	}

	public String getAddressType() {
		return this.addressType;
	}

	public void setAddressType(final String addressType) {
		this.addressType = addressType;
	}

	public String getSuccessfulPreviousVisit() {
		return this.successfulPreviousVisit;
	}

	public void setSuccessfulPreviousVisit(final String successfulPreviousVisit) {
		this.successfulPreviousVisit = successfulPreviousVisit;
	}

	@Override
	public String toString() {
		return "WorklistItemDetailedDto [worklistItemId=" + this.worklistItemId + ", formTemplateId="
				+ this.formTemplateId + ", worklistAssociationExternalId=" + this.worklistAssociationExternalId
				+ ", worklistNote=" + this.worklistNote + ", address=" + this.address + "]";
	}

}
