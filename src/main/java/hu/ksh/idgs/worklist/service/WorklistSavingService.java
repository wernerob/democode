package hu.ksh.idgs.worklist.service;

import java.util.UUID;

import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.dto.WorklistSaveAddressDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface WorklistSavingService {

	void saveAddresses(final WorklistSaveAddressDto worklistSaveAddress, final String fixedFormTemplateId)
			throws ServiceException;

	void updateWorklistItem(final WorklistItemDetailedDto worklistItemDetailedData) throws ServiceException;

	WorklistItemDetailedDto addNewHouseHold(final String authorization, final String externalAddressId,
			final String formTemplateId, String newHouseHold, UUID newWorklistId, final String addressType)
			throws ServiceException;

}
