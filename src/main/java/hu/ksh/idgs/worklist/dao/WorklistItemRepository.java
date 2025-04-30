package hu.ksh.idgs.worklist.dao;

import java.util.List;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.core.dto.TupleIdValueDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.dto.VisitItemDto;
import hu.ksh.idgs.worklist.dto.WorklistAdminItemDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedExportDto;
import hu.ksh.idgs.worklist.dto.WorklistSendItemDto;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatFillRateItemDto;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.maja.core.dto.GetWorkitemStatusDto;

public interface WorklistItemRepository extends IdgsRepository<WorklistItemEntity> {

	WorklistItemEntity getWorklistItemForHouseHold(IdgsEntityId houseHoldId, String formTemplateId);

	List<WorklistItemDetailedDto> getWorklistItems(final String userId, List<String> formTempalteIds);

	WorklistItemDetailedDto getWorklistItemForSelfLoader(final String kshAddressId, String houseHoldId,
			final String formTemplateId);

	List<TupleIdValueDto> getNumberOfVisitStatus(List<IdgsEntityId> worklistId);

	String getFormTemplateId(final IdgsEntityId worklistItemId);

	List<VisitItemDto> getVisitItemsByWorklist(final IdgsEntityId worklistItemId);

	FormStatusType getFormStatus(final IdgsEntityId worklistItemId);

	IdgsEntityId getWorklistId(final String externalAddressId, final String houseHoldId, final String formTemplateId);

	List<WorklistSendItemDto> getWorklistSendItems(String formTemplateId, Integer maxResult);

	List<WorklistAdminItemDto> getAdminWorklistItems(String formTemplateId, final String externalAddressId);

	List<WorklistAdminItemDto> getWorklistInfo(final List<IdgsEntityId> worklistIds);

	List<WorklistStatFillRateItemDto> getFillRateStat(String formTemplateId);

	List<WorklistItemEntity> getWorklistItemsByExternalId(final List<String> externalAddressIds,
			final String formTemplateId);

	@Deprecated
	void resend(final String formTemplateId);

	GetWorkitemStatusDto getWorkitemStatusData(IdgsEntityId worklistId);

	List<WorklistItemDetailedExportDto> getWorklistItemsForExport(List<IdgsEntityId> worklistIds);

}
