package hu.ksh.idgs.worklist.api.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.HouseHoldApiModel;
import hu.ksh.idgs.worklist.api.model.WorklistSaveAddressListRequestApiModel;
import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.WorklistSaveAddressDto;

@Mapper(componentModel = "spring")
public interface WorklistSaveAddressMapper extends CommonMapper {

	WorklistSaveAddressDto getDtoFromRequest(WorklistSaveAddressListRequestApiModel request, String authorization);

	@Mapping(source = "id", target = "id")
	@Mapping(source = "info", target = "worklistInfo")
	@Mapping(source = "ospec", target = "ospec")
	@Mapping(source = "succesfulPreviousVisit", target = "succesfulPreviousVisit")
	@Mapping(source = "initVisitCode", target = "initVisitCode")
	@Mapping(source = "addressType", target = "addressType")
	@Mapping(source = "date", target = "date")
	HouseHoldDto getHouseHold(HouseHoldApiModel houseHoldModel);

}
