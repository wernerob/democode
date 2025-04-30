package hu.ksh.idgs.worklist.dto.stat;

import java.io.Serializable;
import java.util.List;

public class WorklistStatDto implements Serializable {

	private static final long serialVersionUID = -3091583689663426236L;

	private List<WorklistStatFillRateItemDto> fillRate;

	public List<WorklistStatFillRateItemDto> getFillRate() {
		return this.fillRate;
	}

	public void setFillRate(final List<WorklistStatFillRateItemDto> fillRate) {
		this.fillRate = fillRate;
	}

}
