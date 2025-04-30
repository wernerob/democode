package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.ksh.idgs.core.model.IdgsEntityId;

public class SaveOsapDto implements Serializable {

	private static final long serialVersionUID = -4825793197932599701L;

	private IdgsEntityId worklistId;
	private OffsetDateTime startDate;
	private OffsetDateTime fillDate;

	public IdgsEntityId getWorklistId() {
		return this.worklistId;
	}

	public void setWorklistId(final IdgsEntityId worklistId) {
		this.worklistId = worklistId;
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

}
