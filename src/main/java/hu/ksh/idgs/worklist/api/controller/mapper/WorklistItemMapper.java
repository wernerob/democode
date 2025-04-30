package hu.ksh.idgs.worklist.api.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.AddressApiModel;
import hu.ksh.idgs.worklist.api.model.AdminWorklistItemApiModel;
import hu.ksh.idgs.worklist.api.model.FormInfoApiModel;
import hu.ksh.idgs.worklist.api.model.GetWorkitemStatusDataApiModel;
import hu.ksh.idgs.worklist.api.model.SaveOsapRequestApiModel;
import hu.ksh.idgs.worklist.api.model.SaveOsapResponseApiModel;
import hu.ksh.idgs.worklist.dto.AddressDto;
import hu.ksh.idgs.worklist.dto.SaveOsapDto;
import hu.ksh.idgs.worklist.dto.WorklistAdminItemDto;
import hu.ksh.maja.core.dto.GetWorkitemStatusDto;
import hu.ksh.maja.core.dto.SaveOsapResultDto;

@Mapper
public interface WorklistItemMapper extends CommonMapper {

	List<AdminWorklistItemApiModel> getAdminWorklistItemsModel(final List<WorklistAdminItemDto> worklistDataItems);

	@Mapping(source = "worklistItemId", target = "worklistId")
	@Mapping(source = "worklistAssociationExternalId", target = "externalId")
	@Mapping(source = "address", target = "address")
	@Mapping(source = "houseHoldId", target = "houseHoldId")
	@Mapping(source = "formTemplateInfo", target = "formInfo")
	@Mapping(source = "formStatus", target = "formStatus")
	@Mapping(source = "censusStartDate", target = "censusStartDate")
	@Mapping(source = "censusEndDate", target = "censusEndDate")
	@Mapping(source = "lastVisitStatusCode", target = "lastVisitStatusCode")
	@Mapping(source = "interviewerUserName", target = "interviewerUserName")
	@Mapping(source = "closeDate", target = "closeDate")
	@Mapping(source = "lastNote", target = "note")
	AdminWorklistItemApiModel getAdminWorklistItemModel(WorklistAdminItemDto worklistDataItem);

	// countyCode
	// settlementCode
	// settlementPart
	@Mapping(source = "zipCode", target = "zipCode")
	@Mapping(source = "settlement", target = "settlement")
	@Mapping(source = "placeName", target = "placeName")
	@Mapping(source = "placeType", target = "placeType")
	@Mapping(source = "houseNumber", target = "houseNumber")
	@Mapping(source = "parcelNumber", target = "parcelNumber")
	@Mapping(source = "building", target = "building")
	@Mapping(source = "stairway", target = "stairway")
	@Mapping(source = "floor", target = "floor")
	@Mapping(source = "door", target = "door")
	AddressApiModel getAddressModel(final AddressDto address);

	// censusStartDate
	// censusEndDate
	@Mapping(source = "id", target = "id")
	@Mapping(source = "code", target = "code")
	@Mapping(source = "year", target = "year")
	@Mapping(source = "period", target = "period")
	@Mapping(source = "description", target = "description")
	FormInfoApiModel getFormInfoModel(FormTemplateDetailedInfoDto FormTemplateDetailedInfo);

	@Mapping(source = "saveOsapModel.worklistId", target = "worklistId")
	@Mapping(source = "saveOsapModel.startDate", target = "startDate")
	@Mapping(source = "saveOsapModel.fillDate", target = "fillDate")
	SaveOsapDto getSaveOsapData(SaveOsapRequestApiModel saveOsapModel);

	@Mapping(source = "saveOsapResultData.formStatus", target = "formStatus")
	@Mapping(source = "saveOsapResultData.formTemplateId", target = "formTemplateId")
	SaveOsapResponseApiModel getSaveOsapResponseModel(SaveOsapResultDto saveOsapResultData);

	@Mapping(source = "workitemStatusData.formTemplateId", target = "formTemplateId")
	@Mapping(source = "workitemStatusData.formStatus", target = "formStatus")
	@Mapping(source = "workitemStatusData.startDate", target = "startDate")
	@Mapping(source = "workitemStatusData.saveDate", target = "saveDate")
	@Mapping(source = "workitemStatusData.fillDate", target = "fillDate")
	@Mapping(source = "workitemStatusData.externalId", target = "externalId")
	@Mapping(source = "workitemStatusData.houseHold", target = "houseHold")
	@Mapping(source = "workitemStatusData.worklistId", target = "worklistId")
	GetWorkitemStatusDataApiModel getWorkitemStatusModel(GetWorkitemStatusDto workitemStatusData);

}
