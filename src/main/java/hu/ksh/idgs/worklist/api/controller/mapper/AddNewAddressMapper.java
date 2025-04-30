package hu.ksh.idgs.worklist.api.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.AddNewAddressRequestApiModel;
import hu.ksh.idgs.worklist.api.model.AddNewSelfLoaderAddressRequestApiModel;
import hu.ksh.idgs.worklist.dto.AddressAddDto;

@Mapper
public interface AddNewAddressMapper extends CommonMapper {

	@Mapping(source = "address", target = "address")
	@Mapping(source = "formTemplateId", target = "formTemplateId")
	AddressAddDto newAddress(AddNewAddressRequestApiModel request);

	@Mapping(source = "address", target = "address")
	@Mapping(source = "formTemplateId", target = "formTemplateId")
	@Mapping(source = "email", target = "email")
	@Mapping(source = "userName", target = "userName")
	AddressAddDto newAddress(AddNewSelfLoaderAddressRequestApiModel request);
}
