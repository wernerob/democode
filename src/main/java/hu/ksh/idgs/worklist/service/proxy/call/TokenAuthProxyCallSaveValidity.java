package hu.ksh.idgs.worklist.service.proxy.call;

import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException.FeignClientException;
import hu.ksh.idgs.worklist.service.proxy.TokenAuthServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.tokenauth.api.model.CensusItemApiModel;
import hu.ksh.maja.tokenauth.api.model.CommonResponseApiModel;
import hu.ksh.maja.tokenauth.api.model.ResultStatusApiModel;
import hu.ksh.maja.tokenauth.api.model.SaveValidityRequestApiModel;

@Component
public class TokenAuthProxyCallSaveValidity {

	@Lazy
	@Autowired
	private TokenAuthServiceProxy tokenAuthServiceProxy;

	public boolean saveValidity(final String authorization, final String osapCode, final Integer year,
			final String period, final String kshAddressId, final String houseHoldId, final OffsetDateTime validFrom,
			final OffsetDateTime validTo) throws ServiceException {

		try {
			final CensusItemApiModel censusItem = getCensusItemModel(osapCode, year, period, kshAddressId, houseHoldId);
			final ResponseEntity<CommonResponseApiModel> response = this.tokenAuthServiceProxy
					.saveValidity(authorization, getRequest(censusItem, validFrom, validTo));

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable");
			}
			final CommonResponseApiModel responseBody = response.getBody();

			if (responseBody == null) {
				throw new ServiceException("Service returns empty resposne");
			}
			if (ResultStatusApiModel.ERROR.equals(responseBody.getStatus())) {
				throw new ServiceException("Service returns error");
			}

			return ResultStatusApiModel.OK.equals(responseBody.getStatus());

		} catch (final FeignClientException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private SaveValidityRequestApiModel getRequest(final CensusItemApiModel censusItem, final OffsetDateTime validFrom,
			final OffsetDateTime validTo) {

		return new SaveValidityRequestApiModel(censusItem, validFrom, validTo);
	}

	private CensusItemApiModel getCensusItemModel(final String osapCode, final Integer year, final String period,
			final String kshAddressId, final String houseHoldId) {

		return new CensusItemApiModel(osapCode, year, period, kshAddressId, houseHoldId);

	}

}
