package hu.ksh.idgs.worklist.api.controller.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.api.controller.mapper.NoteMapper;
import hu.ksh.idgs.worklist.api.model.AddressApiModel;
import hu.ksh.idgs.worklist.api.model.FormInfoApiModel;
import hu.ksh.idgs.worklist.api.model.FormStatusApiModel;
import hu.ksh.idgs.worklist.api.model.FormWorklistItemApiModel;
import hu.ksh.idgs.worklist.api.model.FormWorklistResponseApiModel;
import hu.ksh.idgs.worklist.api.model.ResultStatusApiModel;
import hu.ksh.idgs.worklist.dto.AddressDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;

@Component
public class WorklistItemDetailedConverter {

	private final NoteMapper noteMapper = Mappers.getMapper(NoteMapper.class);

	public FormWorklistResponseApiModel getResponse(final boolean error,
			final List<WorklistItemDetailedDto> worklistItemsData) {

		final List<FormWorklistItemApiModel> worklistItems = getWorklistItemsFromData(worklistItemsData);

		final FormWorklistResponseApiModel response = new FormWorklistResponseApiModel();
		response.setWorklistItems(worklistItems);
		response.setStatus(error ? ResultStatusApiModel.ERROR : ResultStatusApiModel.OK);

		return response;

	}

	private List<FormWorklistItemApiModel> getWorklistItemsFromData(
			final List<WorklistItemDetailedDto> worklistItemsData) {

		if (worklistItemsData == null) {
			return new ArrayList<>();
		}

		final List<FormWorklistItemApiModel> worklistItems = new ArrayList<>();

		for (final WorklistItemDetailedDto worklistItemData : worklistItemsData) {
			worklistItems.add(getWorklistItem(worklistItemData));
		}

		return worklistItems;
	}

	public FormWorklistItemApiModel getWorklistItem(final WorklistItemDetailedDto worklistItemData) {

		if (worklistItemData == null) {
			return null;
		}

		final String addressTypeStripped = StringUtils.stripToNull(worklistItemData.getAddressType());

		final FormWorklistItemApiModel worklistItem = new FormWorklistItemApiModel();
		worklistItem.setWorklistId(entityIdToString(worklistItemData.getWorklistItemId()));
		worklistItem.setExternalId(worklistItemData.getWorklistAssociationExternalId());
		worklistItem.setNote(this.noteMapper.getNoteApiModels(worklistItemData.getWorklistNote()));
		worklistItem.setAddress(getAddress(worklistItemData.getAddress()));
		worklistItem.setHouseHoldId(worklistItemData.getHouseHoldId());
		worklistItem.setFormInfo(getFormTemplateInfo(worklistItemData.getFormTemplateInfo()));
		worklistItem.setFormStatus(getStatusFromStatusData(worklistItemData.getFormStatus()));
		worklistItem.setCensusStartDate(worklistItemData.getCensusStartDate());
		worklistItem.setCensusEndDate(worklistItemData.getCensusEndDate());
		worklistItem.setLastVisitStatusCode(worklistItemData.getLastVisitStatusCode());
		worklistItem.setCloseDate(worklistItemData.getCloseDate());
		worklistItem.setWorklistInfo(worklistItemData.getWorklistInfo());
		worklistItem.setOspec(worklistItemData.getOspec());
		worklistItem.setOsapCode(worklistItemData.getOsapCode());
		worklistItem.setOsapYear(worklistItemData.getOsapYear());
		worklistItem.setOsapPeriod(worklistItemData.getOsapPeriod());
		worklistItem.setAddressType(addressTypeStripped);
		worklistItem.setSuccesfulPreviousVisit(worklistItemData.getSuccessfulPreviousVisit());
		return worklistItem;
	}

	private AddressApiModel getAddress(final AddressDto addressData) {

		if (addressData == null) {
			return null;
		}

		final AddressApiModel address = new AddressApiModel();
		address.setZipCode(addressData.getZipCode());
		address.setSettlementCode(addressData.getSettlementCode());
		address.setSettlement(addressData.getSettlement());
		address.setPlaceName(addressData.getPlaceName());
		address.setPlaceType(addressData.getPlaceType());
		address.setHouseNumber(addressData.getHouseNumber());
		address.setParcelNumber(addressData.getParcelNumber());
		address.setBuilding(addressData.getBuilding());
		address.setStairway(addressData.getStairway());
		address.setFloor(addressData.getFloor());
		address.setDoor(addressData.getDoor());

		return address;
	}

	private String entityIdToString(final IdgsEntityId id) {
		if (id == null) {
			return null;
		}

		return id.toString();
	}

	private FormStatusApiModel getStatusFromStatusData(final FormStatusType statusData) {

		if (statusData == null) {
			return null;
		}

		return FormStatusApiModel.fromValue(statusData.toString());
	}

	private FormInfoApiModel getFormTemplateInfo(final FormTemplateDetailedInfoDto formTemplateInfoData) {

		if (formTemplateInfoData == null) {
			return null;
		}

		final FormInfoApiModel formInfo = new FormInfoApiModel();
		final IdgsEntityId id = formTemplateInfoData.getId();
		formInfo.setId(id == null ? null : id.toString());
		formInfo.setCode(formTemplateInfoData.getCode());
		formInfo.setYear(formTemplateInfoData.getYear());
		formInfo.setPeriod(formTemplateInfoData.getPeriod());
		formInfo.setDescription(formTemplateInfoData.getDescription());

		return formInfo;

	}

}
