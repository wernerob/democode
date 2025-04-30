package hu.ksh.idgs.worklist.service.proxy.call;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.FormSaveDto;
import hu.ksh.idgs.core.dto.form.FormSaveFieldDto;
import hu.ksh.idgs.core.dto.form.FormSaveFormDto;
import hu.ksh.idgs.core.dto.form.FormSaveRecordDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormType;
import hu.ksh.idgs.form.api.model.FormLoadRequestApiModel;
import hu.ksh.idgs.form.api.model.FormLoadResponseApiModel;
import hu.ksh.idgs.form.api.model.FormSaveFieldApiModel;
import hu.ksh.idgs.form.api.model.FormSaveFormApiModel;
import hu.ksh.idgs.form.api.model.FormSaveRecordApiModel;
import hu.ksh.idgs.worklist.service.proxy.FormServiceProxy;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class FormServiceProxyCallFormLoad {

	@Autowired
	private FormServiceProxy formServiceProxy;

	public FormSaveDto getForm(final String authorization, final IdgsEntityId worklistId) throws ServiceException {

		if (worklistId == null) {
			throw new ServiceException("Missing worklist id");
		}

		final ResponseEntity<FormLoadResponseApiModel> response = this.formServiceProxy.loadForm(authorization,
				getRequest(worklistId.toString()));
		if (response.getStatusCode() != HttpStatus.OK) {
			throw new ServiceException("Form service was unavailable");
		}

		final FormLoadResponseApiModel responseBody = response.getBody();

		if (responseBody == null) {
			throw new ServiceException("Service returns empty resposne");
		}

		return convertApiToData(responseBody);
	}

	private FormLoadRequestApiModel getRequest(final String worklistId) {

		return new FormLoadRequestApiModel().worklistId(worklistId);
	}

	private FormSaveDto convertApiToData(final FormLoadResponseApiModel responseBody) {

		final FormSaveDto formSaveData = new FormSaveDto();
		formSaveData.setSpendTime(responseBody.getSpendTime());
		formSaveData.setForm(convertFormApiToData(responseBody.getForm(), FormType.MAIN));

		return formSaveData;
	}

	private List<FormSaveFormDto> convertFormsApiToData(final List<FormSaveFormApiModel> formsModel,
			final FormType formType) {

		final List<FormSaveFormDto> formsData = new ArrayList<>();
		formsModel.stream().forEach(formModel -> formsData.add(convertFormApiToData(formModel, formType)));
		return formsData;
	}

	private FormSaveFormDto convertFormApiToData(final FormSaveFormApiModel formModel, final FormType formType) {

		final FormSaveFormDto formData = new FormSaveFormDto();
		formData.setId(formModel.getId());
		formData.setFormType(formType);
		formData.getRecords().addAll(convertRecordsApiToData(formModel.getRecords()));
		formData.getSubforms().addAll(convertFormsApiToData(formModel.getSubforms(), FormType.SUB));
		formData.getTables().addAll(convertFormsApiToData(formModel.getTables(), FormType.TABLE));

		return formData;
	}

	private List<FormSaveRecordDto> convertRecordsApiToData(final List<FormSaveRecordApiModel> recordsModel) {

		final List<FormSaveRecordDto> recordsData = new ArrayList<>();
		recordsModel.stream().forEach(recordModel -> recordsData.add(convertRecordApiToData(recordModel)));

		return recordsData;
	}

	private FormSaveRecordDto convertRecordApiToData(final FormSaveRecordApiModel recordModel) {

		final FormSaveRecordDto record = new FormSaveRecordDto();
		record.getFields().addAll(convertFieldsApiToData(recordModel.getFields()));
		return record;
	}

	private List<FormSaveFieldDto> convertFieldsApiToData(final List<FormSaveFieldApiModel> fieldsModel) {

		final List<FormSaveFieldDto> fieldsData = new ArrayList<>();
		fieldsModel.stream().forEach(fieldModel -> fieldsData.add(convertFieldApiToData(fieldModel)));

		return fieldsData;
	}

	private FormSaveFieldDto convertFieldApiToData(final FormSaveFieldApiModel fieldModel) {

		final FormSaveFieldDto fieldData = new FormSaveFieldDto();
		fieldData.setName(fieldModel.getName());
		fieldData.setValue(fieldModel.getValue());

		return fieldData;
	}

}
