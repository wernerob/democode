package hu.ksh.idgs.worklist.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.dto.form.FormSaveDto;
import hu.ksh.idgs.core.dto.form.FormSaveFieldDto;
import hu.ksh.idgs.core.dto.form.FormSaveFormDto;
import hu.ksh.idgs.core.dto.form.FormSaveRecordDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.service.VisitStatusSendItems;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormLoad;
import hu.ksh.maja.core.exception.ServiceException;

@Component("visitStatusSendMefItems")
public class VisitStatusSendMefItems extends VisitStatusSendCommonItems implements VisitStatusSendItems {

	private static final Integer MAIN_FORM_IDX = 1;
	private static final Integer SUB_FORM_IDX = 2;
	private static final String PAGE_B_COUNT_SUBFORM = "SZEMELY";
	private static final String PAGE_B_COUNTABLE_SUBFORM_FIELD = "MKVEGZ21";

	@Autowired
	private FormServiceProxyCallFormLoad formServiceProxyCallFormLoad;

	@Override
	public List<VisitStatusSendItemDto> getItems(final String authorization, final List<VisitStatusSendItemDto> items)
			throws ServiceException {

		final List<VisitStatusSendItemDto> allItems = new ArrayList<>();
		for (final VisitStatusSendItemDto itemData : items) {
			allItems.addAll(getItemsWithChilds(authorization, itemData));
		}

		return allItems;
	}

	private List<VisitStatusSendItemDto> getItemsWithChilds(final String authorization,
			final VisitStatusSendItemDto sendItemData) throws ServiceException {

		final List<VisitStatusSendItemDto> itemWithChilds = new ArrayList<>();
		final IdgsEntityId worklistId = sendItemData.getWorklistId();

		final DataArrivalDto dataArrival = getDataArrival(authorization, worklistId);
		final String saveRole = dataArrival == null ? null : dataArrival.getSaveRole();
		final VisitStatusSendItemDto itemA = createSendableItem(authorization, sendItemData,
				VisitStatusSendMefItems.MAIN_FORM_IDX, null, saveRole, dataArrival);
		itemWithChilds.add(itemA);

		itemA.setSpendTime(getSpendTime(dataArrival));
		itemA.setSaveRole(dataArrival.getSaveRole());

		extendsPageB(authorization, worklistId, itemWithChilds, sendItemData, dataArrival, saveRole);

		return itemWithChilds;
	}

	private List<VisitStatusSendItemDto> extendsPageB(final String authorization, final IdgsEntityId worklistId,
			final List<VisitStatusSendItemDto> itemWithChilds, final VisitStatusSendItemDto sendItemData,
			final DataArrivalDto dataArrival, final String saveRole) throws ServiceException {

		final FormSaveDto formSaveData = this.formServiceProxyCallFormLoad.getForm(authorization, worklistId);
		if (formSaveData == null) {
			return itemWithChilds;
		}
		final FormSaveFormDto formData = formSaveData.getForm();
		if (formData == null) {
			return itemWithChilds;
		}
		final List<FormSaveFormDto> subForms = formData.getSubforms();
		if (subForms == null) {
			return itemWithChilds;
		}
		final FormSaveFormDto subform = findSubform(subForms, VisitStatusSendMefItems.PAGE_B_COUNT_SUBFORM);
		if (subform == null) {
			return itemWithChilds;
		}
		final List<FormSaveRecordDto> records = subform.getRecords();
		if (records == null) {
			return itemWithChilds;
		}

		for (int recordIdx = 0; recordIdx < records.size(); recordIdx++) {
			final FormSaveRecordDto record = records.get(recordIdx);
			final FormSaveFieldDto field = findFieldInRecord(record,
					VisitStatusSendMefItems.PAGE_B_COUNTABLE_SUBFORM_FIELD);
			if (field == null || field.getValue() == null) {
				continue;
			}
			final VisitStatusSendItemDto itemB = createSendableItem(authorization, sendItemData,
					VisitStatusSendMefItems.SUB_FORM_IDX, recordIdx + 1, saveRole, dataArrival);
			itemWithChilds.add(itemB);

		}

		return itemWithChilds;
	}

	private FormSaveFormDto findSubform(final List<FormSaveFormDto> subForms, final String subformId) {

		for (final FormSaveFormDto subform : subForms) {
			if (subformId.equals(subform.getId())) {
				return subform;
			}
		}

		return null;
	}

	private FormSaveFieldDto findFieldInRecord(final FormSaveRecordDto record, final String fieldId) {

		if (record == null) {
			return null;
		}

		final List<FormSaveFieldDto> fields = record.getFields();
		if (fields == null) {
			return null;
		}
		for (final FormSaveFieldDto field : fields) {
			if (fieldId.equals(field.getName())) {
				return field;
			}
		}

		return null;
	}

}
