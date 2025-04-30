package hu.ksh.idgs.worklist.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.service.VisitStatusSendItems;
import hu.ksh.maja.core.exception.ServiceException;

@Component("visitStatusSendLuszItems")
public class VisitStatusSendLuszItems extends VisitStatusSendCommonItems implements VisitStatusSendItems {

	private static final Integer MAIN_FORM_IDX = 1;
	private static final Integer SUB_FORM_IDX = 2;

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

		// final VisitType visitType = sendItemData.getVisitType();

		final DataArrivalDto dataArrival = getDataArrival(authorization, worklistId);
		final String saveRole = dataArrival == null ? null : dataArrival.getSaveRole();
		final VisitStatusSendItemDto itemA = createSendableItem(authorization, sendItemData,
				VisitStatusSendLuszItems.MAIN_FORM_IDX, null, saveRole, dataArrival);
		itemWithChilds.add(itemA);

		// if (VisitType.SUCCESS.equals(visitType)) {

		itemA.setSpendTime(getSpendTime(dataArrival));
		itemA.setSaveRole(dataArrival.getSaveRole());

		final String pageBValue = dataArrival.getPageB() == null ? "0" : dataArrival.getPageB();

		Integer pageBValueInt;
		try {
			pageBValueInt = Integer.valueOf(pageBValue);

		} catch (final NumberFormatException e) {
			throw new ServiceException(String.format("Page b not a number: %s", pageBValue));
		}

		for (int cnt = 1; cnt <= pageBValueInt; cnt++) {
			final VisitStatusSendItemDto itemB = createSendableItem(authorization, sendItemData,
					VisitStatusSendLuszItems.SUB_FORM_IDX, cnt, saveRole, dataArrival);
			itemWithChilds.add(itemB);
//			}

//			IntStream.range(1, pageBValueInt + 1).forEach(cnt -> {
//				final VisitStatusSendItemDto itemB = createSendableItem(authorization, sendItemData,
//						VisitStatusSendLuszItems.SUB_FORM_IDX, cnt, saveRole, dataArrival);
//				itemWithChilds.add(itemB);
//			});

		}

		return itemWithChilds;
	}

}
