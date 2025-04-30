package hu.ksh.idgs.worklist.service.proxy.call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException.FeignClientException;
import hu.ksh.idgs.worklist.service.proxy.TokenAuthServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.tokenauth.api.model.CensusItemApiModel;
import hu.ksh.maja.tokenauth.api.model.ResultStatusApiModel;
import hu.ksh.maja.tokenauth.api.model.TokenReactivateResponseApiModel;

@Component
public class TokenAuthProxyCallReactivateUserToken {

	@Autowired
	private TokenAuthServiceProxy tokenAuthServiceProxy;

	public void reactivate(final String authorization, final String osapCode, final Integer osapYear,
			final String osapPeriod, final String kshAddressId, final String houseHoldId) throws ServiceException {

		try {
			final ResponseEntity<TokenReactivateResponseApiModel> response = this.tokenAuthServiceProxy.tokenReactivate(
					authorization, createRequest(osapCode, osapYear, osapPeriod, kshAddressId, houseHoldId));

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable");
			}
			final TokenReactivateResponseApiModel responseBody = response.getBody();

			if (responseBody == null) {
				throw new ServiceException("Service returns empty resposne");
			}
			if (ResultStatusApiModel.ERROR == responseBody.getStatus()) {
				throw new ServiceException("Service returns error");
			}

		} catch (final FeignClientException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private CensusItemApiModel createRequest(final String osapCode, final Integer osapYear, final String osapPeriod,
			final String kshAddressId, final String houseHoldId) {

		return new CensusItemApiModel()//
				.osapCode(osapCode)//
				.osapYear(osapYear)//
				.osapPeriod(osapPeriod)//
				.kshAddressId(kshAddressId)//
				.houseHoldId(houseHoldId);
	}

}
