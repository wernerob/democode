package hu.ksh.idgs.worklist.service;

import java.util.List;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.api.model.NoteApiModel;
import hu.ksh.idgs.worklist.dto.ResetSendStatusDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedExportDto;
import hu.ksh.idgs.worklist.dto.WorklistSendFilterDto;
import hu.ksh.idgs.worklist.dto.WorklistSendItemDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface WorklistSendService {

	List<WorklistSendItemDto> getItems(WorklistSendFilterDto worklistSendFilter) throws ServiceException;

	void resetSendStatus(final String authorization, final ResetSendStatusDto resetSendStatusData)
			throws ServiceException;

	void resetSendStatusesByWorklistId(final String authorization, final List<String> worklistIds)
			throws ServiceException;

	List<WorklistItemDetailedExportDto> getWorklistItemsForExport(List<IdgsEntityId> worklistIds)
			throws ServiceException;

	void setStatusesByTransmitterProcessResult(final String authorization, final List<NoteApiModel> worklistIdsSucces, final List<NoteApiModel> worklistIdsFailed)
			throws ServiceException;
}
