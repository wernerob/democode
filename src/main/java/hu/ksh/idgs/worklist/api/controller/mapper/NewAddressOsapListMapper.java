package hu.ksh.idgs.worklist.api.controller.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.NewAddressOsapApiModel;

@Mapper
public interface NewAddressOsapListMapper extends CommonMapper {

	List<NewAddressOsapApiModel> getResponse(List<FormTemplateDetailedInfoDto> formTemplateDataList);

}
