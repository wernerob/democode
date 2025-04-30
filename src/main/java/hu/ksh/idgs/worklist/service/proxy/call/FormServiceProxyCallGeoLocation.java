package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.core.dto.GeoLocationDto;
import hu.ksh.idgs.form.api.model.GeoLocationApiModel;
import hu.ksh.idgs.form.api.model.GetGeoLocationsRequestApiModel;
import hu.ksh.idgs.form.api.model.GetGeoLocationsResponseApiModel;
import hu.ksh.idgs.form.api.model.ResultStatusApiModel;
import hu.ksh.idgs.form.api.model.SaveGeoLocationRequestApiModel;
import hu.ksh.idgs.form.api.model.SaveGeoLocationResponseApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class FormServiceProxyCallGeoLocation {

	@Autowired
	private FormServiceProxy formServiceProxy;

	public String saveNewGeolocation(final String authorization, final GeoLocationDto geoLocationData)
			throws ServiceException {

		try {

			if (geoLocationData == null) {
				return null;
			}

			final ResponseEntity<SaveGeoLocationResponseApiModel> response = this.formServiceProxy
					.saveGeolocation(authorization, getSaveGeoLocationRequest(geoLocationData));
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Form service was unavailable while saving geoLocation");
			}

			final SaveGeoLocationResponseApiModel responseBody = response.getBody();
			if (responseBody == null) {
				throw new ServiceException("Form service returns empty resposne while getting saved geoLocation id");
			}

			return responseBody.getId();
		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private SaveGeoLocationRequestApiModel getSaveGeoLocationRequest(final GeoLocationDto geoLocationData) {

		return new SaveGeoLocationRequestApiModel()//
				.latitude(geoLocationData.getLatitude())//
				.longitude(geoLocationData.getLongitude())//
				.time(geoLocationData.getTime());
	}

	public List<GeoLocationDto> getGeoLocations(final String authorization, final List<String> locationIds)
			throws ServiceException {

		if (locationIds == null || locationIds.isEmpty()) {
			return Collections.emptyList();
		}

		try {

			final ResponseEntity<GetGeoLocationsResponseApiModel> response = this.formServiceProxy
					.getGeoLocations(authorization, getGeoLocationsRequest(locationIds));
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Form service was unavailable");
			}

			final GetGeoLocationsResponseApiModel responseBody = response.getBody();
			if (responseBody == null) {
				throw new ServiceException("Form service returns empty resposne");
			}

			if (ResultStatusApiModel.ERROR.equals(responseBody.getStatus())) {
				throw new ServiceException("Form service returns error");
			}

			return getGeoLocationDataList(responseBody.getGeoLocations());

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private GetGeoLocationsRequestApiModel getGeoLocationsRequest(final List<String> locationIds) {

		final GetGeoLocationsRequestApiModel request = new GetGeoLocationsRequestApiModel();
		locationIds.forEach(locationId -> request.addGeoLocationIdsItem(UUID.fromString(locationId)));

		return request;
	}

	private GeoLocationDto getGeoLocationData(final GeoLocationApiModel geoLocation) {

		if (geoLocation == null) {
			return null;
		}

		final GeoLocationDto geoLocationData = new GeoLocationDto();
		geoLocationData.setLatitude(geoLocation.getLatitude());
		geoLocationData.setLongitude(geoLocation.getLongitude());
		geoLocationData.setTime(geoLocation.getTime());

		return geoLocationData;
	}

	private List<GeoLocationDto> getGeoLocationDataList(final List<GeoLocationApiModel> geoLocations) {

		if (geoLocations == null || geoLocations.isEmpty()) {
			return Collections.emptyList();
		}

		final List<GeoLocationDto> geoLocationsDataList = new ArrayList<>();
		for (final GeoLocationApiModel geoLocation : geoLocations) {
			final GeoLocationDto geoLocationData = getGeoLocationData(geoLocation);
			if (geoLocationData != null) {
				geoLocationsDataList.add(geoLocationData);
			}
		}

		return geoLocationsDataList;
	}

}
