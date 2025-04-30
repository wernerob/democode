package hu.ksh.idgs.worklist.api.controller.converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.worklist.api.model.CodeTableItemApiModel;
import hu.ksh.idgs.worklist.api.model.VisitInfoResponseApiModel;
import hu.ksh.idgs.worklist.dto.VisitInfoDto;

@Component
public class VisitInfoConverter {

	@Autowired
	private VisitItemConverter visitItemConverter;

	@Autowired
	private CodeTableConverter codeTableConverter;

	public VisitInfoResponseApiModel getResponse(final VisitInfoDto visitInfoData, final boolean error) {

		final VisitInfoResponseApiModel response = new VisitInfoResponseApiModel();

		response.setFinalStatuses(getStatuses(visitInfoData.getFinalVisitCodeTableItems()));
		response.setTempStatuses(getStatuses(visitInfoData.getTempVisitCodeTableItems()));
		response.setOtherFinalStatuses(getStatuses(visitInfoData.getOtherFinalCodeTableItems()));
		response.setAdminFinalStatuses(getStatuses(visitInfoData.getAdminFinalVisitCodeTableItems()));

		response.setSavedItems(this.visitItemConverter.getVisitItemsFromDataList(visitInfoData.getSavedVisitItems()));
		return response;
	}

	private List<CodeTableItemApiModel> getStatuses(final List<CodeTableItemDto> statusDataList) {
		return this.codeTableConverter.getCodeTableItems(statusDataList);
	}

}
