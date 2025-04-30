package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import hu.ksh.idgs.core.dto.GeoLocationDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.model.value.WorklistCreateType;
import hu.ksh.maja.core.dto.VisitType;

public class VisitStatusSendItemDto implements Serializable {

	private static final long serialVersionUID = 7762111036512977973L;
	private String externalAddressId;
	private String houseHoldId;
	private int formIndex;
	private Integer subformIndex;
	private OffsetDateTime closeDate;
	private IdgsEntityId worklistId;
	private IdgsEntityId visitStatusId;
	private String visitStatusCodeTableId;
	private String visitStatusCode;
	private VisitType visitType;
	private Integer spendTime;
	private String saveRole;
	private List<VisitStatusSendFieldDto> fields;
	private String geoLocationId;
	private List<GeoLocationDto> geoLocations;
	private String worklistNote;
	private WorklistCreateType worklistCreateType;
	private String chosenDay;
	private String phone;

	public VisitStatusSendItemDto() {
	}

	public VisitStatusSendItemDto(final IdgsEntityId worklistId, final String externalAddressId,
			final String houseHoldId, final OffsetDateTime closeDate, final String visitStatusCode,
			final IdgsEntityId visitStatusId, final String visitStatusCodeTableId, final VisitType visitType,
			final String geoLocationId, final String worklistNote, final WorklistCreateType worklistCreateType) {
		this.worklistId = worklistId;
		this.externalAddressId = externalAddressId;
		this.houseHoldId = houseHoldId;
		this.closeDate = closeDate;
		this.visitStatusCode = visitStatusCode;
		this.visitStatusId = visitStatusId;
		this.visitStatusCodeTableId = visitStatusCodeTableId;
		this.visitType = visitType;
		this.geoLocationId = geoLocationId;
		this.worklistNote = worklistNote;
		this.worklistCreateType = worklistCreateType;
	}

	public IdgsEntityId getWorklistId() {
		return this.worklistId;
	}

	public void setWorklistId(final IdgsEntityId worklistId) {
		this.worklistId = worklistId;
	}

	public String getExternalAddressId() {
		return this.externalAddressId;
	}

	public void setExternalAddressId(final String externalAddressId) {
		this.externalAddressId = externalAddressId;
	}

	public String getHouseHoldId() {
		return this.houseHoldId;
	}

	public void setHouseHoldId(final String houseHoldId) {
		this.houseHoldId = houseHoldId;
	}

	public int getFormIndex() {
		return this.formIndex;
	}

	public void setFormIndex(final int formIndex) {
		this.formIndex = formIndex;
	}

	public OffsetDateTime getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(final OffsetDateTime closeDate) {
		this.closeDate = closeDate;
	}

	public IdgsEntityId getVisitStatusId() {
		return this.visitStatusId;
	}

	public void setVisitStatusId(final IdgsEntityId visitStatusId) {
		this.visitStatusId = visitStatusId;
	}

	public String getVisitStatusCodeTableId() {
		return this.visitStatusCodeTableId;
	}

	public void setVisitStatusCodeTableId(final String visitStatusCodeTableId) {
		this.visitStatusCodeTableId = visitStatusCodeTableId;
	}

	public String getVisitStatusCode() {
		return this.visitStatusCode;
	}

	public void setVisitStatusCode(final String visitStatusCode) {
		this.visitStatusCode = visitStatusCode;
	}

	public VisitType getVisitType() {
		return this.visitType;
	}

	public void setVisitType(final VisitType visitType) {
		this.visitType = visitType;
	}

	public Integer getSubformIndex() {
		return this.subformIndex;
	}

	public void setSubformIndex(final Integer subformIndex) {
		this.subformIndex = subformIndex;
	}

	public Integer getSpendTime() {
		return this.spendTime;
	}

	public void setSpendTime(final Integer spendTime) {
		this.spendTime = spendTime;
	}

	public String getSaveRole() {
		return this.saveRole;
	}

	public void setSaveRole(final String saveRole) {
		this.saveRole = saveRole;
	}

	public List<VisitStatusSendFieldDto> getFields() {
		return this.fields;
	}

	public void setFields(final List<VisitStatusSendFieldDto> fields) {
		this.fields = fields;
	}

	public String getGeoLocationId() {
		return this.geoLocationId;
	}

	public void setGeoLocationId(final String geoLocationId) {
		this.geoLocationId = geoLocationId;
	}

	public List<GeoLocationDto> getGeoLocations() {
		return this.geoLocations;
	}

	public void setGeoLocations(final List<GeoLocationDto> geoLocations) {
		this.geoLocations = geoLocations;
	}

	public String getWorklistNote() {
		return this.worklistNote;
	}

	public void setWorklistNote(final String worklistNote) {
		this.worklistNote = worklistNote;
	}

	public WorklistCreateType getWorklistCreateType() {
		return this.worklistCreateType;
	}

	public void setWorklistCreateType(final WorklistCreateType worklistCreateType) {
		this.worklistCreateType = worklistCreateType;
	}

	public String getChosenDay() {
		return this.chosenDay;
	}

	public void setChosenDay(final String chosenDay) {
		this.chosenDay = chosenDay;
	}

	public String getPhone() {
		return this.phone;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

}
