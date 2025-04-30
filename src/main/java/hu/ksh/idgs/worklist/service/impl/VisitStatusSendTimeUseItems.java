package hu.ksh.idgs.worklist.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.dto.form.FormSaveDto;
import hu.ksh.idgs.core.dto.form.FormSaveFieldDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.service.VisitStatusSendItems;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormLoad;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.service.FormSaveDataGetterService;

@Component("visitStatusSendTimeUseItems")
public class VisitStatusSendTimeUseItems extends VisitStatusSendCommonItems implements VisitStatusSendItems {

	private static final Integer MAIN_FORM_IDX = 1;
	private static final String OSAP_FIELD_CHOSEN_DAY = "DIARYDAY";
	private static final String OSAP_FIELD_PHONE = "TEL";

	@Lazy
	@Autowired
	private FormServiceProxyCallFormLoad formServiceProxyCallFormLoad;

	@Lazy
	@Autowired
	private FormSaveDataGetterService formSaveDataGetterService;

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
				VisitStatusSendTimeUseItems.MAIN_FORM_IDX, null, saveRole, dataArrival);

		itemA.setSpendTime(getSpendTime(dataArrival));
		itemA.setSaveRole(dataArrival.getSaveRole());

		final List<FormSaveFieldDto> mainFields = getMainFields(authorization, worklistId);
		itemA.setChosenDay(getChosenDay(mainFields));
		itemA.setPhone(getPhone(mainFields));

		itemWithChilds.add(itemA);

		return itemWithChilds;
	}

	private String getChosenDay(final List<FormSaveFieldDto> fields) throws ServiceException {
		return this.formSaveDataGetterService.getFieldValue(fields, VisitStatusSendTimeUseItems.OSAP_FIELD_CHOSEN_DAY);
	}

	private String getPhone(final List<FormSaveFieldDto> fields) throws ServiceException {
		return this.formSaveDataGetterService.getFieldValue(fields, VisitStatusSendTimeUseItems.OSAP_FIELD_PHONE);
	}

	private List<FormSaveFieldDto> getMainFields(final String authorization, final IdgsEntityId worklistId)
			throws ServiceException {

		final FormSaveDto formSaveData = this.formServiceProxyCallFormLoad.getForm(authorization, worklistId);
		return this.formSaveDataGetterService.getMainFields(formSaveData);

	}

}
