package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.core.value.FormTemplateSettingType;
import hu.ksh.idgs.form.api.model.FormSettingResponseApiModel;
import hu.ksh.idgs.form.api.model.ResultStatusApiModel;
import hu.ksh.idgs.form.api.model.SettingTypeApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class FormServiceProxyCallFormTemplateSetting {

	@Autowired
	private FormServiceProxy formServiceProxy;

	public String getSettingValue(final String authorization, final String formTemplateId,
			final FormTemplateSettingType settingType) throws ServiceException {

		try {

			final ResponseEntity<FormSettingResponseApiModel> response = this.formServiceProxy
					.getFormSetting(authorization, UUID.fromString(formTemplateId), getSettingTypeModel(settingType));

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable");
			}
			final FormSettingResponseApiModel responseBody = response.getBody();
			if (responseBody == null) {
				throw new ServiceException("Service returns empty response");
			}
			if (ResultStatusApiModel.OK != responseBody.getStatus()) {
				throw new ServiceException("Servicecall was unavailable");
			}
			return responseBody.getSetting();

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private SettingTypeApiModel getSettingTypeModel(final FormTemplateSettingType settingType) {
		return SettingTypeApiModel.valueOf(settingType.toString());
	}

}
