package hu.ksh.idgs.worklist.service.impl.saveaddress.osap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.WorklistAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistZoneDto;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class SaveAddressOsapHandlerRegister implements SaveAddressOsapHandler {

	private final List<SaveAddressOsapHandler> saveAddressOsapHandlers;

	public SaveAddressOsapHandlerRegister() {
		this.saveAddressOsapHandlers = new ArrayList<>();
	}

	@Override
	public void handle(final String authorization, final HouseHoldDto houseHold, final WorklistAddressDto address,
			final WorklistZoneDto zone) throws ServiceException {

		if (houseHold == null || address == null || zone == null) {
			return;
		}

		for (final SaveAddressOsapHandler saveAddressOsapHandler : this.saveAddressOsapHandlers) {
			if (saveAddressOsapHandler.isApplicable(zone.getFormCode())) {
				saveAddressOsapHandler.handle(authorization, houseHold, address, zone);
			}

		}

	}

	@Override
	public boolean isApplicable(final String osapCode) {
		return true;
	}

	public void addHandler(final SaveAddressOsapHandler saveAddressOsapHandler) {
		this.saveAddressOsapHandlers.add(saveAddressOsapHandler);
	}

}
