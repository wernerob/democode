package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.ksh.idgs.core.dto.GeoLocationDto;
import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.maja.core.dto.VisitType;

public class VisitItemDto implements Serializable {

	private static final long serialVersionUID = 1666697297761705241L;

	private String visitCodeTableItemId;
	private String visitCode;
	private String note;
	private OffsetDateTime date;
	private CodeTableItemDto codeTableItem;
	private GeoLocationDto geoLocation;
	private VisitType visitType;
	private String visitForm;

	public VisitItemDto() {
	}

	public VisitItemDto(final String visitCodeTableItemId, final String visitCode, final String note,
			final OffsetDateTime date, final String visitForm, final VisitType visitType) {
		this.visitCodeTableItemId = visitCodeTableItemId;
		this.visitCode = visitCode;
		this.note = note;
		this.date = date;
		this.visitForm = visitForm;
		this.visitType = visitType;
	}

	public String getVisitCodeTableItemId() {
		return this.visitCodeTableItemId;
	}

	public void setVisitCodeTableItemId(final String visitCodeTableItemId) {
		this.visitCodeTableItemId = visitCodeTableItemId;
	}

	public String getVisitCode() {
		return this.visitCode;
	}

	public void setVisitCode(final String visitCode) {
		this.visitCode = visitCode;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public OffsetDateTime getDate() {
		return this.date;
	}

	public void setDate(final OffsetDateTime date) {
		this.date = date;
	}

	public CodeTableItemDto getCodeTableItem() {
		return this.codeTableItem;
	}

	public void setCodeTableItem(final CodeTableItemDto codeTableItem) {
		this.codeTableItem = codeTableItem;
	}

	public GeoLocationDto getGeoLocation() {
		return this.geoLocation;
	}

	public void setGeoLocation(final GeoLocationDto geoLocation) {
		this.geoLocation = geoLocation;
	}

	public VisitType getVisitType() {
		return this.visitType;
	}

	public void setVisitType(final VisitType visitType) {
		this.visitType = visitType;
	}

	public String getVisitForm() {
		return this.visitForm;
	}

	public void setVisitForm(final String visitForm) {
		this.visitForm = visitForm;
	}

}
