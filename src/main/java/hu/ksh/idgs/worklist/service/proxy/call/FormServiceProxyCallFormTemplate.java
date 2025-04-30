package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.form.api.model.FormTemplateIdRequestApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateIdResponseApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateInfoApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateInfoRequestApiModel;
import hu.ksh.idgs.form.api.model.FormTemplatesResponseApiModel;
import hu.ksh.idgs.form.api.model.LanguageApiModel;
import hu.ksh.idgs.form.api.model.ResultStatusApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;

@Component
public class FormServiceProxyCallFormTemplate {

	@Autowired
	private FormServiceProxy formServiceProxy;

	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	public FormTemplateDetailedInfoDto getFormTemplateInfo(final String authorization, final Language language,
			final String formTemplateId) throws ServiceException {

		final String role = this.majaAuthenticationService.getRoleName();
		return getFormTemplateInfo(authorization, language, formTemplateId, role);

	}

	public FormTemplateDetailedInfoDto getFormTemplateInfo(final String authorization, final Language language,
			final String formTemplateId, final String role) throws ServiceException {

		try {
			final ResponseEntity<FormTemplateInfoApiModel> response = this.formServiceProxy
					.getFormTemplateInfo(authorization, getTemplateInfoRequest(language, formTemplateId, role));

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable while getting form template info");
			}

			return convertFormTemplateInfoModelToDto(response.getBody());

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private FormTemplateInfoRequestApiModel getTemplateInfoRequest(final Language language, final String formTemplateId,
			final String role) {

		final FormTemplateInfoRequestApiModel request = new FormTemplateInfoRequestApiModel();
		request.setFormTemplateId(formTemplateId);
		request.setLang(LanguageApiModel.fromValue(language.toString()));
		if (role != null) {
			request.setRole(role);
		}

		return request;

	}

	private FormTemplateDetailedInfoDto convertFormTemplateInfoModelToDto(final FormTemplateInfoApiModel model) {

		if (model == null) {
			return null;
		}

		final FormTemplateDetailedInfoDto formTemplateInfo = new FormTemplateDetailedInfoDto();
		formTemplateInfo.setId(IdgsEntityId.fromString(model.getId()));
		formTemplateInfo.setCode(model.getCode());
		formTemplateInfo.setYear(model.getYear());
		formTemplateInfo.setPeriod(model.getPeriod());
		formTemplateInfo.setDescription(model.getDescription());
		formTemplateInfo.setCensusStartDate(model.getCensusStartDate());
		formTemplateInfo.setCensusEndDate(model.getCensusEndDate());
		formTemplateInfo.setActive(model.getActive());

		return formTemplateInfo;
	}

	public String getFormTemplateId(final String formCode, final Integer year, final String period,
			final String authorization) throws ServiceException {

		try {

			final FormTemplateIdRequestApiModel formTemplateIdRequest = new FormTemplateIdRequestApiModel();
			formTemplateIdRequest.setFormTemplateCode(formCode);
			formTemplateIdRequest.setYear(year);
			formTemplateIdRequest.setPeriod(period);

			final ResponseEntity<FormTemplateIdResponseApiModel> response = this.formServiceProxy
					.getFormTemplateId(authorization, formTemplateIdRequest);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Form service was unavailable while getting formId");
			}

			final FormTemplateIdResponseApiModel responseBody = response.getBody();
			if (responseBody == null) {
				throw new ServiceException("Form service returns empty resposne while getting formId");
			}

			return responseBody.getId();
		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	public List<FormTemplateDetailedInfoDto> getFormTemplates(final String authorization, final Language language,
			final Boolean active, final Boolean valid) throws ServiceException {

		final ResponseEntity<FormTemplatesResponseApiModel> response = this.formServiceProxy
				.getFormTemplates(authorization, LanguageApiModel.fromValue(language.toString()), active, valid);
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new ServiceException("Form service was unavailable");
		}

		final FormTemplatesResponseApiModel responseBody = response.getBody();
		if (responseBody == null) {
			throw new ServiceException("Form service returns empty resposne");
		}

		if (!ResultStatusApiModel.OK.equals(responseBody.getStatus())) {
			throw new ServiceException("Form service returns error");
		}

		final List<FormTemplateDetailedInfoDto> formTemplateDataList = new ArrayList<>();
		final List<FormTemplateInfoApiModel> formTemplates = responseBody.getFormTemplates();
		for (final FormTemplateInfoApiModel formTemplate : formTemplates) {
			formTemplateDataList.add(convertFormTemplateInfoModelToDto(formTemplate));
		}

		return formTemplateDataList;

	}

	public FormTemplateDetailedInfoDto getFormTemplatePeriodInfo(final String authorization, final String osap,
			final Integer year, final String period, final Language language, final String role)
			throws ServiceException {

		try {
			final ResponseEntity<FormTemplateInfoApiModel> response = this.formServiceProxy.templatePeriodInfo(
					authorization, osap, year, period, LanguageApiModel.fromValue(language.toString()), role);

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable while getting form template info");
			}

			return convertFormTemplateInfoModelToDto(response.getBody());

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

}
