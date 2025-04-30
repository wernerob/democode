package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;

public class WorklistItemDetailedExportDto implements Serializable {

	private static final long serialVersionUID = 3637032858987877154L;

	private IdgsEntityId worklistItemId;
	private String formTemplateId;
	private String externalId;
	private String houseHoldId;
	private AddressDto address;
	private FormStatusType formStatus;
	private String lastVisitStatusCode;
	private String interviewerUserName;
	private OffsetDateTime startDate;
	private OffsetDateTime fillDate;
	private OffsetDateTime saveDate;
	private OffsetDateTime closeDate;
	private String worklistInfo;
	private String ospec;
	private List<NoteDto> worklistNotes;

	public WorklistItemDetailedExportDto() {
		super();
	}

	public WorklistItemDetailedExportDto(final IdgsEntityId worklistItemId, final String formTemplateId,
			final String externalId, final String houseHoldId, final String zipCode, final String settlementCode,
			final String settlement, final String placeName, final String placeType, final String houseNumber,
			final String parcelNumber, final String building, final String stairway, final String floor,
			final String door, final FormStatusType formStatus, final String lastVisitStatusCode,
			final String interviewerUserName, final OffsetDateTime startDate, final OffsetDateTime fillDate,
			final OffsetDateTime saveDate, final OffsetDateTime closeDate, final String worklistInfo,
			final String ospec) {
		this.worklistItemId = worklistItemId;
		this.formTemplateId = formTemplateId;
		this.externalId = externalId;
		this.houseHoldId = houseHoldId;
		this.address = new AddressDto(zipCode, settlementCode, settlement, placeName, placeType, houseNumber,
				parcelNumber, building, stairway, floor, door);
		this.formStatus = formStatus;
		this.lastVisitStatusCode = lastVisitStatusCode;
		this.interviewerUserName = interviewerUserName;
		this.startDate = startDate;
		this.fillDate = fillDate;
		this.saveDate = saveDate;
		this.closeDate = closeDate;
		this.worklistInfo = worklistInfo;
		this.ospec = ospec;
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

	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(final String externalId) {
		this.externalId = externalId;
	}

	public String getHouseHoldId() {
		return this.houseHoldId;
	}

	public void setHouseHoldId(final String houseHoldId) {
		this.houseHoldId = houseHoldId;
	}

	public AddressDto getAddress() {
		return this.address;
	}

	public void setAddress(final AddressDto address) {
		this.address = address;
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

	public String getInterviewerUserName() {
		return this.interviewerUserName;
	}

	public void setInterviewerUserName(final String interviewerUserName) {
		this.interviewerUserName = interviewerUserName;
	}

	public OffsetDateTime getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final OffsetDateTime startDate) {
		this.startDate = startDate;
	}

	public OffsetDateTime getFillDate() {
		return this.fillDate;
	}

	public void setFillDate(final OffsetDateTime fillDate) {
		this.fillDate = fillDate;
	}

	public OffsetDateTime getSaveDate() {
		return this.saveDate;
	}

	public void setSaveDate(final OffsetDateTime saveDate) {
		this.saveDate = saveDate;
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

	public List<NoteDto> getWorklistNotes() {
		return this.worklistNotes;
	}

	public void setWorklistNotes(final List<NoteDto> worklistNotes) {
		this.worklistNotes = worklistNotes;
	}

}
