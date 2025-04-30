package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;

public class VisitInfoDto implements Serializable {

	private static final long serialVersionUID = 3126104946117972672L;

	private final List<CodeTableItemDto> finalVisitCodeTableItems;
	private final List<CodeTableItemDto> tempVisitCodeTableItems;
	private final List<CodeTableItemDto> otherFinalCodeTableItems;
	private final List<CodeTableItemDto> adminFinalVisitCodeTableItems;
	private final List<VisitItemDto> savedVisitItems;

	public VisitInfoDto() {
		this.finalVisitCodeTableItems = new ArrayList<>();
		this.tempVisitCodeTableItems = new ArrayList<>();
		this.otherFinalCodeTableItems = new ArrayList<>();
		this.savedVisitItems = new ArrayList<>();
		this.adminFinalVisitCodeTableItems = new ArrayList<>();
	}

	public List<CodeTableItemDto> getFinalVisitCodeTableItems() {
		return this.finalVisitCodeTableItems;
	}

	public List<CodeTableItemDto> getTempVisitCodeTableItems() {
		return this.tempVisitCodeTableItems;
	}

	public List<CodeTableItemDto> getOtherFinalCodeTableItems() {
		return this.otherFinalCodeTableItems;
	}

	public List<CodeTableItemDto> getAdminFinalVisitCodeTableItems() {
		return this.adminFinalVisitCodeTableItems;
	}

	public List<VisitItemDto> getSavedVisitItems() {
		return this.savedVisitItems;
	}

}
