package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.ksh.idgs.core.model.IdgsEntityId;

public class NoteDto implements Serializable {

	private static final long serialVersionUID = 8474309641116853202L;

	private IdgsEntityId worklistId;
	private String note;

	private OffsetDateTime saveDate;

	public NoteDto() {
		super();

	}

	public NoteDto(final IdgsEntityId worklistId, final String note, final OffsetDateTime saveDate) {
		super();
		this.worklistId = worklistId;
		this.note = note;
		this.saveDate = saveDate;
	}

	public IdgsEntityId getWorklistId() {
		return this.worklistId;
	}

	public void setWorklistId(final IdgsEntityId worklistId) {
		this.worklistId = worklistId;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public OffsetDateTime getSaveDate() {
		return this.saveDate;
	}

	public void setSaveDate(final OffsetDateTime saveDate) {
		this.saveDate = saveDate;
	}

}
