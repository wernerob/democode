package hu.ksh.idgs.worklist.api.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.WorklistStatFillRateItemApiModel;
import hu.ksh.idgs.worklist.api.model.WorklistStatResponseApiModel;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatDto;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatFillRateItemDto;

@Mapper
public interface WorklistStatMapper extends CommonMapper {

	@Mapping(source = "statData.fillRate", target = "fillRate")
	WorklistStatResponseApiModel getStatResponse(WorklistStatDto statData);

	@Mapping(source = "rateItemData.formStatus", target = "formStatus")
	@Mapping(source = "rateItemData.number", target = "number")
	WorklistStatFillRateItemApiModel getStat(WorklistStatFillRateItemDto rateItemData);

}
