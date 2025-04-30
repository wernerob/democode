package hu.ksh.idgs.worklist.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import hu.ksh.idgs.core.dto.GeoLocationDto;
import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormArrivalData;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallGeoLocation;
import hu.ksh.maja.core.exception.ServiceException;

public abstract class VisitStatusSendCommonItems {

	@Autowired
	private FormServiceProxyCallFormArrivalData formServiceProxyCallFormArrivalData;

	@Autowired
	private FormServiceProxyCallGeoLocation formServiceProxyCallGeoLocation;

	protected final VisitStatusSendItemDto createSendableItem(final String authorization,
			final VisitStatusSendItemDto rawItemData, final int formIndex, final Integer subformIndex,
			final String saveRole, final DataArrivalDto dataArrival) throws ServiceException {

		final VisitStatusSendItemDto sendableItem = new VisitStatusSendItemDto();
		sendableItem.setExternalAddressId(rawItemData.getExternalAddressId());
		sendableItem.setHouseHoldId(rawItemData.getHouseHoldId());
		sendableItem.setFormIndex(formIndex);
		sendableItem.setSubformIndex(subformIndex);
		sendableItem.setCloseDate(rawItemData.getCloseDate());
		sendableItem.setVisitStatusCode(rawItemData.getVisitStatusCode());
		sendableItem.setSaveRole(saveRole);
		sendableItem.setWorklistNote(rawItemData.getWorklistNote());
		sendableItem.setGeoLocations(getGeoLocations(authorization, dataArrival, rawItemData.getGeoLocationId()));

		return sendableItem;
	}

	protected DataArrivalDto getDataArrival(final String authorization, final IdgsEntityId worklistId)
			throws ServiceException {

		if (worklistId == null) {
			return null;
		}

		final String worklistIdStr = worklistId.toString();
		return this.formServiceProxyCallFormArrivalData.getFormDataArrival(authorization, worklistIdStr);

	}

	protected final Integer getSpendTime(final DataArrivalDto dataArrival) {

		if (dataArrival == null) {
			return null;
		}

		return dataArrival.getSpendTime();

	}

	private List<GeoLocationDto> getGeoLocations(final String authorization, final DataArrivalDto dataArrival,
			final String visitGeoLocationId) throws ServiceException {

		if (dataArrival == null) {
			return Collections.emptyList();
		}

		final List<GeoLocationDto> geoLocations = new ArrayList<>();

		final List<GeoLocationDto> formGeoLocations = dataArrival.getGeoLocations();
		if (formGeoLocations != null && !formGeoLocations.isEmpty()) {
			geoLocations.addAll(formGeoLocations);
		}
		if (visitGeoLocationId != null) {
			final List<GeoLocationDto> visitGeoLocations = this.formServiceProxyCallGeoLocation
					.getGeoLocations(authorization, List.of(visitGeoLocationId));
			if (visitGeoLocations != null && !visitGeoLocations.isEmpty()) {
				geoLocations.addAll(visitGeoLocations);
			}
		}

		if (geoLocations.isEmpty()) {
			return Collections.emptyList();
		}

		Collections.sort(geoLocations);

		return List.of(//
				geoLocations.get(0), //
				geoLocations.get(geoLocations.size() - 1));

	}

}
