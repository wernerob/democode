package hu.ksh.idgs.worklist.api.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.ResetSendStatusRequestApiModel;
import hu.ksh.idgs.worklist.api.model.WorklistItemDetailedExportApiModel;
import hu.ksh.idgs.worklist.api.model.WorklistSendItemApiModel;
import hu.ksh.idgs.worklist.dto.ResetSendStatusDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedExportDto;
import hu.ksh.idgs.worklist.dto.WorklistSendItemDto;

@Mapper
public interface WorklistSendMapper extends CommonMapper {

	List<WorklistSendItemApiModel> getResponseItems(List<WorklistSendItemDto> itemDataList);

	@Mapping(source = "itemData.worklistId", target = "worklistId")
	@Mapping(source = "itemData.formStatus", target = "formStatus")
	@Mapping(source = "itemData.lastVisitStatusCode", target = "lastVisitStatusCode")
	WorklistSendItemApiModel getResponseItem(WorklistSendItemDto itemData);

	@Mapping(source = "request.externalIds", target = "externalIds")
	@Mapping(source = "request.formCode", target = "formCode")
	@Mapping(source = "request.year", target = "year")
	@Mapping(source = "request.period", target = "period")
	ResetSendStatusDto getResetSendStatusData(ResetSendStatusRequestApiModel request);

	@Mapping(source = "exportItem.worklistItemId", target = "worklistId")
	@Mapping(source = "exportItem.formTemplateId", target = "formTemplateId")
	@Mapping(source = "exportItem.externalId", target = "externalId")
	@Mapping(source = "exportItem.houseHoldId", target = "houseHoldId")
	@Mapping(source = "exportItem.address", target = "address")
	@Mapping(source = "exportItem.formStatus", target = "formStatus")
	@Mapping(source = "exportItem.lastVisitStatusCode", target = "lastVisitStatusCode")
	@Mapping(source = "exportItem.interviewerUserName", target = "interviewerUserName")
	@Mapping(source = "exportItem.startDate", target = "startDate")
	@Mapping(source = "exportItem.fillDate", target = "fillDate")
	@Mapping(source = "exportItem.saveDate", target = "saveDate")
	@Mapping(source = "exportItem.closeDate", target = "closeDate")
	@Mapping(source = "exportItem.worklistInfo", target = "worklistInfo")
	@Mapping(source = "exportItem.ospec", target = "ospec")
	@Mapping(source = "exportItem.worklistNotes", target = "notes")
	WorklistItemDetailedExportApiModel getExportItem(WorklistItemDetailedExportDto exportItem);

	List<WorklistItemDetailedExportApiModel> getExportItems(List<WorklistItemDetailedExportDto> exportItems);
}
