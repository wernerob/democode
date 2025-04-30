package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException.FeignClientException;
import hu.ksh.idgs.auth.api.model.ActiveOsapCodeResponseApiModel;
import hu.ksh.idgs.auth.api.model.ResultStatusApiModel;
import hu.ksh.idgs.worklist.service.proxy.AuthServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class AuthServiceProxyCallOsapCodes {

	@Lazy
	@Autowired
	private AuthServiceProxy authServiceProxy;

	public List<String> getActiveOsapCodes(final String authorization, final String userId) throws ServiceException {

		try {
			final ResponseEntity<ActiveOsapCodeResponseApiModel> response = this.authServiceProxy
					.getActiveOsapCodes(authorization, userId);

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service got an error");
			}

			final ActiveOsapCodeResponseApiModel responseBody = response.getBody();

			if (responseBody == null) {
				throw new ServiceException("Service returns empty resposne");
			}

			if (ResultStatusApiModel.ERROR == responseBody.getStatus()) {
				throw new ServiceException("Service returns error");
			}

			return responseBody.getOsapCodes();

		} catch (final FeignClientException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

}
