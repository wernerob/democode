package hu.ksh.idgs.worklist.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.service.VisitStatusSendItems;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;

@Component("visitStatusSendPnszItems")
public class VisitStatusSendPnszItems extends VisitStatusSendCommonItems implements VisitStatusSendItems {

	private static final Integer MAIN_FORM_IDX = 1;

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

		final VisitType visitType = sendItemData.getVisitType();

		final DataArrivalDto dataArrival = getDataArrival(authorization, worklistId);
		final String saveRole = dataArrival == null ? null : dataArrival.getSaveRole();
		final VisitStatusSendItemDto itemA = createSendableItem(authorization, sendItemData,
				VisitStatusSendPnszItems.MAIN_FORM_IDX, null, saveRole, dataArrival);
		itemWithChilds.add(itemA);

		if (VisitType.SUCCESS.equals(visitType)) {

			itemA.setSpendTime(getSpendTime(dataArrival));
			itemA.setSaveRole(dataArrival.getSaveRole());

		}

		return itemWithChilds;
	}

}
