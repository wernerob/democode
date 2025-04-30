package hu.ksh.idgs.worklist.api.controller.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import hu.ksh.idgs.core.mapper.CommonMapper;
import hu.ksh.idgs.worklist.api.model.FormItemValueApiModel;
import hu.ksh.idgs.worklist.api.model.VisitStatusSendItemApiModel;
import hu.ksh.idgs.worklist.api.model.VisitStatusSendResponseApiModel;
import hu.ksh.idgs.worklist.dto.VisitStatusSendDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendFieldDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.service.impl.VisitStatusSendField;

@Mapper(componentModel = "spring")
public interface VisitStatusSendMapper extends CommonMapper {

	@Mapping(source = "visitStatusSendData.formTemplateInfo.code", target = "formCode")
	@Mapping(source = "visitStatusSendData.formTemplateInfo.year", target = "year")
	@Mapping(source = "visitStatusSendData.formTemplateInfo.period", target = "period")
	@Mapping(source = "visitStatusSendData.interviewerUserName", target = "interviewerUserName")
	@Mapping(source = "visitStatusSendData.items", target = "items")
	VisitStatusSendResponseApiModel getResponse(VisitStatusSendDto visitStatusSendData);

	@Mapping(source = "itemData.externalAddressId", target = "externalAddressId")
	@Mapping(source = "itemData.houseHoldId", target = "houseHoldId")
	@Mapping(source = "itemData.formIndex", target = "formIndex")
	@Mapping(source = "itemData.subformIndex", target = "subformIndex")
	@Mapping(source = "itemData.visitStatusCode", target = "visitStatusCode")
	@Mapping(source = "itemData.closeDate", target = "closeDate")
	@Mapping(source = "itemData.spendTime", target = "spendTime")
	@Mapping(source = "itemData.saveRole", target = "saveRole")
	@Mapping(source = "itemData.fields", target = "fields")
	@Mapping(source = "itemData.geoLocations", target = "geoLocations")
	@Mapping(source = "itemData.worklistNote", target = "worklistNote")
	@Mapping(source = "itemData.chosenDay", target = "chosenDay")
	@Mapping(source = "itemData.phone", target = "phone")
	VisitStatusSendItemApiModel getResponseItem(VisitStatusSendItemDto itemData);

	default FormItemValueApiModel getFormItemValue(final VisitStatusSendFieldDto sendField) {

		if (sendField == null) {
			return null;
		}

		final VisitStatusSendField field = sendField.getField();
		final String fieldName = field == null ? null : field.getFieldId();

		final FormItemValueApiModel formItemValueModel = new FormItemValueApiModel();
		formItemValueModel.setName(fieldName);
		formItemValueModel.setValue(sendField.getValue());

		return formItemValueModel;
	}

}
