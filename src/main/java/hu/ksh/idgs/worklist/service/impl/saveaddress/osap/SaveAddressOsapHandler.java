package hu.ksh.idgs.worklist.service.impl.saveaddress.osap;

import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.WorklistAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistZoneDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface SaveAddressOsapHandler {

	void handle(String authorization, HouseHoldDto houseHold, WorklistAddressDto address, WorklistZoneDto zone)
			throws ServiceException;

	boolean isApplicable(String osapCode);

}
