package hu.ksh.idgs.worklist.api.controller.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.GeoLocationDto;
import hu.ksh.idgs.worklist.api.model.GeoLocationApiModel;

@Component
public class GeoLocationConverter {

	public GeoLocationApiModel getGeoLocationFromData(final GeoLocationDto geoLocationData) {

		if (geoLocationData == null) {
			return null;
		}

		return new GeoLocationApiModel()//
				.latitude(geoLocationData.getLatitude())//
				.longitude(geoLocationData.getLongitude())//
				.time(geoLocationData.getTime());
	}

	public GeoLocationDto getGeoLocationData(final GeoLocationApiModel geoLocation) {

		if (geoLocation == null) {
			return null;
		}

		final GeoLocationDto geoLocationData = new GeoLocationDto();
		geoLocationData.setLatitude(geoLocation.getLatitude());
		geoLocationData.setLongitude(geoLocation.getLongitude());
		geoLocationData.setTime(geoLocation.getTime());

		return geoLocationData;
	}

	public List<GeoLocationDto> getGeoLocationDataList(final List<GeoLocationApiModel> geoLocations) {

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
