package hu.ksh.idgs.worklist.api.controller.converter;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.api.controller.mapper.NoteMapper;
import hu.ksh.idgs.worklist.api.model.AddressApiModel;
import hu.ksh.idgs.worklist.api.model.FormWorklistItemApiModel;
import hu.ksh.idgs.worklist.dto.AddressDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;

@Component
public class WorklistItemUpdateConverter {

	private final NoteMapper noteMapper = Mappers.getMapper(NoteMapper.class);

	public WorklistItemDetailedDto getDto(final FormWorklistItemApiModel worklistItem) {

		final WorklistItemDetailedDto worklistItemDetailedData = new WorklistItemDetailedDto();
		worklistItemDetailedData.setWorklistItemId(IdgsEntityId.fromString(worklistItem.getWorklistId()));
		worklistItemDetailedData.setWorklistNote(this.noteMapper.getNoteDtos(worklistItem.getNote()));
		worklistItemDetailedData.setAddress(getAddressData(worklistItem.getAddress()));
		worklistItemDetailedData.setSuccessfulPreviousVisit(worklistItem.getSuccesfulPreviousVisit());
		return worklistItemDetailedData;
	}

	private AddressDto getAddressData(final AddressApiModel address) {

		if (address == null) {
			return null;
		}

		final AddressDto addressData = new AddressDto();
		addressData.setZipCode(address.getZipCode());
		addressData.setSettlementCode(address.getSettlementCode());
		addressData.setSettlement(address.getSettlement());
		addressData.setPlaceName(address.getPlaceName());
		addressData.setPlaceType(address.getPlaceType());
		addressData.setHouseNumber(address.getHouseNumber());
		addressData.setParcelNumber(address.getParcelNumber());
		addressData.setBuilding(address.getBuilding());
		address.setStairway(address.getStairway());
		address.setFloor(address.getFloor());
		address.setDoor(address.getDoor());

		return addressData;
	}

}
