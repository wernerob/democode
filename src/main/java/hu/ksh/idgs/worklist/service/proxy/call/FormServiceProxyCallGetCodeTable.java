package hu.ksh.idgs.worklist.service.proxy.call;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException;
import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.form.api.model.CodeTableItemApiModel;
import hu.ksh.idgs.form.api.model.GetCodeTableItemRequestApiModel;
import hu.ksh.idgs.form.api.model.LanguageApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.Language;

@Component
public class FormServiceProxyCallGetCodeTable {

	@Autowired
	private FormServiceProxy formServiceProxy;

	public CodeTableItemDto getCodeTableItem(final String authorization, final String codeTableItemId,
			final Language language) throws ServiceException {

		try {
			final ResponseEntity<CodeTableItemApiModel> response = this.formServiceProxy.getCodeTableItem(authorization,
					getRequest(codeTableItemId, language));

			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable while getting code table item");
			}

			final CodeTableItemApiModel responseBody = response.getBody();

			return getDataFromModel(responseBody);

		} catch (final FeignException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private GetCodeTableItemRequestApiModel getRequest(final String codeTableItemId, final Language language) {

		return new GetCodeTableItemRequestApiModel()//
				.codeTableItemId(codeTableItemId)//
				.lang(LanguageApiModel.fromValue(language.toString()));
	}

	private CodeTableItemDto getDataFromModel(final CodeTableItemApiModel codeTableItem) {

		if (codeTableItem == null) {
			return null;
		}

		return new CodeTableItemDto(IdgsEntityId.fromString(codeTableItem.getId()), codeTableItem.getCode(),
				codeTableItem.getDescription(), codeTableItem.getValid());
	}

}
