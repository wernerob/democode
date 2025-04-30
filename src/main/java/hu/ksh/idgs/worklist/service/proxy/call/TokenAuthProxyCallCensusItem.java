package hu.ksh.idgs.worklist.service.proxy.call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException.FeignClientException;
import hu.ksh.idgs.worklist.service.proxy.TokenAuthServiceProxy;
import hu.ksh.maja.core.dto.CensusItemDto;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.mapper.TokenAuthCoreMapper;
import hu.ksh.maja.tokenauth.api.model.GetCensusItemResponseApiModel;
import hu.ksh.maja.tokenauth.api.model.ResultStatusApiModel;

@Component
public class TokenAuthProxyCallCensusItem {

	@Lazy
	@Autowired
	private TokenAuthServiceProxy tokenAuthServiceProxy;

	@Lazy
	@Autowired
	private TokenAuthCoreMapper tokenAuthCoreMapper;

	public CensusItemDto getCensusItem(final String authorization) throws ServiceException {

		try {
			final ResponseEntity<GetCensusItemResponseApiModel> response = this.tokenAuthServiceProxy
					.getCensusItem(authorization);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable");
			}
			final GetCensusItemResponseApiModel responseBody = response.getBody();

			if (responseBody == null) {
				throw new ServiceException("Service returns empty resposne");
			}
			if (ResultStatusApiModel.ERROR == responseBody.getStatus()) {
				throw new ServiceException("Service returns error");
			}

			return this.tokenAuthCoreMapper.getCensusInfoData(responseBody.getCensusItem());

		} catch (final FeignClientException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

}
