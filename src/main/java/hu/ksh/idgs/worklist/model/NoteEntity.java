package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.ksh.idgs.core.model.IdgsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "note")
public class NoteEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = 4056384009740657246L;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worklist_item_id", nullable = false)
	private WorklistItemEntity worklistItem;

	@Size(max = 300)
	@Column(name = "note", length = 300)
	private String note;

	@Column(name = "save_date")
	private OffsetDateTime saveDate;

	public NoteEntity() {
		super();
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

	public WorklistItemEntity getWorklistItem() {
		return this.worklistItem;
	}

	public void setWorklistItem(final WorklistItemEntity worklistItem) {
		this.worklistItem = worklistItem;
	}

}
