package hu.ksh.idgs.worklist.service.impl.saveaddress.osap;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.WorklistAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistZoneDto;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallIndependentPrefillSave;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.OsapCodes;

@Component
public class SaveAddressOsapTimeUseHandler implements SaveAddressOsapHandler {

	private static final Logger logger = LoggerFactory.getLogger(SaveAddressOsapTimeUseHandler.class);

	@Lazy
	@Autowired
	private FormServiceProxyCallIndependentPrefillSave formServiceProxyCallIndependentPrefillSave;

	@Override
	public void handle(final String authorization, final HouseHoldDto houseHold, final WorklistAddressDto address,
			final WorklistZoneDto zone) throws ServiceException {

		final String osapCode = zone.getFormCode();
		final Integer year = zone.getYear();
		final String period = zone.getPeriod();

		final String externalId = address.getExternalAddressId();
		final String houseHoldId = houseHold.getId();

		final String targetDate = houseHold.getDate();

		if (StringUtils.isBlank(targetDate)) {
			SaveAddressOsapTimeUseHandler.logger.warn("Missing date for address: {}/{}", externalId, houseHoldId);
			return;
		}

		final Map<String, String> fieldCodeValueMap = Map.of("TEREDETI", targetDate.trim());

		this.formServiceProxyCallIndependentPrefillSave.prefillOneAddressMainFields(authorization, externalId,
				houseHoldId, osapCode, year, period, fieldCodeValueMap);

	}

	@Override
	public boolean isApplicable(final String osapCode) {
		return List.of(OsapCodes.TIMEUSE_REGISTRATION.getOsapCode(), OsapCodes.TIMEUSE_DIARY.getOsapCode())
				.contains(osapCode);
	}

}
