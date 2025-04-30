package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.core.dto.FormTemplateVisitItemsDto;
import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.form.api.model.LanguageApiModel;
import hu.ksh.idgs.form.api.model.VisitCodeItemsByFormTemlateRequestApiModel;
import hu.ksh.idgs.form.api.model.VisitCodeItemsByFormTemlateResponseApiModel;
import hu.ksh.idgs.worklist.api.controller.converter.CodeTableConverter;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.Language;

@Component
public class FormServiceProxyCallVisitFormTemplate {

	@Autowired
	private FormServiceProxy formServiceProxy;

	@Autowired
	private CodeTableConverter codeTableConverter;

	public FormTemplateVisitItemsDto getVisitCodeItemsByFormTemlate(final String authorization,
			final String formTemplateId, final Language language) throws ServiceException {

		try {

			final ResponseEntity<VisitCodeItemsByFormTemlateResponseApiModel> response = this.formServiceProxy
					.getVisitCodeTableItemsByFormTemplate(authorization,
							getVisitCodeItemsByFormTemlateRequest(formTemplateId, language));

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable while getting form visit code list");
			}

			return getDataFromModel(response.getBody());

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private VisitCodeItemsByFormTemlateRequestApiModel getVisitCodeItemsByFormTemlateRequest(
			final String formTemplateId, final Language language) {

		return new VisitCodeItemsByFormTemlateRequestApiModel().formTemplateId(formTemplateId)
				.lang(LanguageApiModel.valueOf(language.toString()));
	}

	private FormTemplateVisitItemsDto getDataFromModel(final VisitCodeItemsByFormTemlateResponseApiModel responseBody) {

		final FormTemplateVisitItemsDto result = new FormTemplateVisitItemsDto();

		if (responseBody == null) {
			result.setFinalCodeTableItems(Collections.emptyList());
			result.setTempCodeTableItems(Collections.emptyList());
			result.setOtherFinalCodeTableItems(Collections.emptyList());
			result.setAdminFinalCodeTableItems(Collections.emptyList());
			return result;
		}

		final List<CodeTableItemDto> finalCodeTableItems = this.codeTableConverter
				.getCodeTableItemDataList_Form(responseBody.getFinalCodeTableItems());
		final List<CodeTableItemDto> tempCodeTableItems = this.codeTableConverter
				.getCodeTableItemDataList_Form(responseBody.getTempCodeTableItems());
		final List<CodeTableItemDto> otherFinalCodeTableItems = this.codeTableConverter
				.getCodeTableItemDataList_Form(responseBody.getOtherFinalCodeTableItems());
		final List<CodeTableItemDto> successCodeTableItems = this.codeTableConverter
				.getCodeTableItemDataList_Form(responseBody.getSuccessCodeTableItems());
		final List<CodeTableItemDto> adminFinalCodeTableItems = this.codeTableConverter
				.getCodeTableItemDataList_Form(responseBody.getAdminFinalCodeTableItems());

		result.setFinalCodeTableItems(finalCodeTableItems);
		result.setTempCodeTableItems(tempCodeTableItems);
		result.setOtherFinalCodeTableItems(otherFinalCodeTableItems);
		result.setSuccessCodeTableItems(successCodeTableItems);
		result.setAdminFinalCodeTableItems(adminFinalCodeTableItems);

		return result;
	}

}
