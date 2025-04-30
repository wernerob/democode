package hu.ksh.idgs.worklist.service.proxy.call;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.mapper.form.FormMapper;
import hu.ksh.idgs.form.api.model.FormDataArrivalRequestApiModel;
import hu.ksh.idgs.form.api.model.FormDataArrivalResponseApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class FormServiceProxyCallFormArrivalData {

	@Autowired
	private FormServiceProxy formServiceProxy;

	private final FormMapper formMapper = Mappers.getMapper(FormMapper.class);

	public DataArrivalDto getFormDataArrival(final String authorization, final String worklistId)
			throws ServiceException {

		try {
			final ResponseEntity<FormDataArrivalResponseApiModel> response = this.formServiceProxy
					.getFormDataArrival(authorization, getRequest(worklistId));
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Form service was unavailable");
			}

			final FormDataArrivalResponseApiModel responseBody = response.getBody();

			if (responseBody == null) {
				throw new ServiceException("Service returns empty resposne");
			}

			return convertModelToDto(responseBody);

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private FormDataArrivalRequestApiModel getRequest(final String worklistId) {

		return new FormDataArrivalRequestApiModel().worklistId(worklistId);
	}

	private DataArrivalDto convertModelToDto(final FormDataArrivalResponseApiModel response) {

		return this.formMapper.getFromDataArrivalData(response);
	}

}
