package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import feign.FeignException.FeignClientException;
import hu.ksh.idgs.form.api.model.FormSaveFieldApiModel;
import hu.ksh.idgs.form.api.model.FormSaveFormApiModel;
import hu.ksh.idgs.form.api.model.FormSaveRecordApiModel;
import hu.ksh.idgs.form.api.model.PrefillSaveAddressItemApiModel;
import hu.ksh.idgs.form.api.model.PrefillSaveRequestApiModel;
import hu.ksh.idgs.form.api.model.PrefillSaveResponseApiModel;
import hu.ksh.idgs.form.api.model.ResultStatusApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class FormServiceProxyCallIndependentPrefillSave {

	@Lazy
	@Autowired
	private FormServiceProxy formServiceProxy;

	public void prefillOneAddressMainFields(final String authorization, final String externalId,
			final String houseHoldId, final String osapCode, final Integer year, final String period,
			final Map<String, String> fieldCodeValueMap) throws ServiceException {

		try {

			final PrefillSaveRequestApiModel request = createRequestForMainFields(externalId, houseHoldId, osapCode,
					year, period, fieldCodeValueMap);
			final ResponseEntity<PrefillSaveResponseApiModel> response = this.formServiceProxy
					.independentPrefillSave(authorization, request);
			if (response.getStatusCode() != HttpStatus.OK) {
				throw new ServiceException("Service was unavailable");
			}
			final PrefillSaveResponseApiModel responseBody = response.getBody();

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

	private PrefillSaveRequestApiModel createRequestForMainFields(final String externalId, final String houseHoldId,
			final String osapCode, final Integer year, final String period,
			final Map<String, String> fieldCodeValueMap) {

		final List<FormSaveFieldApiModel> fields = new ArrayList<>();
		fieldCodeValueMap.forEach((code, value) -> fields.add(createField(code, value)));

		final FormSaveRecordApiModel mainFormRecord = createRecord(fields);
		final FormSaveFormApiModel mainForm = createForm("MAIN", List.of(mainFormRecord), List.of(), List.of());

		final PrefillSaveAddressItemApiModel addressItem = createAddressItem(externalId, houseHoldId, mainForm);
		return createRequest(osapCode, year, period, List.of(addressItem));

	}

	private PrefillSaveRequestApiModel createRequest(final String osapCode, final Integer year, final String period,
			final List<PrefillSaveAddressItemApiModel> addresses) {
		return new PrefillSaveRequestApiModel(osapCode, year, period, addresses);
	}

	private PrefillSaveAddressItemApiModel createAddressItem(final String externalId, final String houseHoldId,
			final FormSaveFormApiModel form) {
		return new PrefillSaveAddressItemApiModel(externalId, houseHoldId, form);
	}

	private FormSaveFormApiModel createForm(final String formCode, final List<FormSaveRecordApiModel> records,
			final List<FormSaveFormApiModel> subforms, final List<FormSaveFormApiModel> tables) {
		return new FormSaveFormApiModel(formCode, records, subforms, tables);
	}

	private FormSaveRecordApiModel createRecord(final List<FormSaveFieldApiModel> fields) {
		return new FormSaveRecordApiModel(fields);
	}

	private FormSaveFieldApiModel createField(final String name, final String value) {

		return new FormSaveFieldApiModel(name).value(value);
	}
}
