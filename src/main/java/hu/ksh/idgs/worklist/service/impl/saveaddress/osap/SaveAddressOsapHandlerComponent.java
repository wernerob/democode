package hu.ksh.idgs.worklist.service.impl.saveaddress.osap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.WorklistAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistZoneDto;
import hu.ksh.maja.core.exception.ServiceException;
import jakarta.annotation.PostConstruct;

@Component
public class SaveAddressOsapHandlerComponent {

	@Lazy
	@Autowired
	private SaveAddressOsapHandlerRegister saveAddressOsapHandlerRegister;

	@Lazy
	@Autowired
	private SaveAddressOsapTimeUseHandler saveAddressOsapTimeUseHandler;

	@PostConstruct
	protected void init() {
		registerHandlers();
	}

	public void handle(final String authorization, final HouseHoldDto houseHold, final WorklistAddressDto address,
			final WorklistZoneDto zone) throws ServiceException {

		this.saveAddressOsapHandlerRegister.handle(authorization, houseHold, address, zone);
	}

	private void registerHandlers() {
		this.saveAddressOsapHandlerRegister.addHandler(this.saveAddressOsapTimeUseHandler);
	}

}
