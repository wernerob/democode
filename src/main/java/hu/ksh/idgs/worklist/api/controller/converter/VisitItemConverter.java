package hu.ksh.idgs.worklist.api.controller.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.worklist.api.model.VisitItemApiModel;
import hu.ksh.idgs.worklist.api.model.VisitStatusApiModel;
import hu.ksh.idgs.worklist.dto.VisitItemDto;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;

@Component
public class VisitItemConverter {

	@Autowired
	private CodeTableConverter codeTableConverter;

	@Autowired
	private GeoLocationConverter geoLocationConverter;

	public VisitItemApiModel getVisitItemFromData(final VisitItemDto visitItemData) {

		if (visitItemData == null) {
			return null;
		}

		return new VisitItemApiModel()//
				.note(visitItemData.getNote())//
				.date(visitItemData.getDate())//
				.item(this.codeTableConverter.getCodeTableItem(visitItemData.getCodeTableItem()))//
				.geoLocation(this.geoLocationConverter.getGeoLocationFromData(visitItemData.getGeoLocation()))//
				.visitForm(visitItemData.getVisitForm())
				.visitType(VisitStatusApiModel.fromValue(visitItemData.getVisitType().toString()));
	}

	public List<VisitItemApiModel> getVisitItemsFromDataList(final List<VisitItemDto> visitItemDataList) {

		final List<VisitItemApiModel> visitItems = new ArrayList<>();
		if (visitItemDataList == null) {
			return visitItems;
		}

		visitItemDataList.forEach(visitItemData -> visitItems.add(getVisitItemFromData(visitItemData)));
		return visitItems;
	}

	public VisitItemDto getVisitItemData(final VisitItemApiModel visitItem) throws ServiceException {

		if (visitItem == null) {
			return null;
		}

		final CodeTableItemDto codeTableItemData = this.codeTableConverter.getCodeTableItemData(visitItem.getItem());
		String codeTableItemIdStr = null;
		if (codeTableItemData != null) {
			codeTableItemIdStr = codeTableItemData.getId() == null ? null : codeTableItemData.getId().toString();
		}

		final VisitItemDto visitItemData = new VisitItemDto();
		visitItemData.setVisitCodeTableItemId(codeTableItemIdStr);
		visitItemData.setNote(visitItem.getNote());
		visitItemData.setDate(visitItem.getDate());
		visitItemData.setCodeTableItem(codeTableItemData);
		visitItemData.setGeoLocation(this.geoLocationConverter.getGeoLocationData(visitItem.getGeoLocation()));
		visitItemData.setVisitType(getVisitStatusData(visitItem.getVisitType()));
		visitItemData.setVisitForm(visitItem.getVisitForm());

		return visitItemData;

	}

	private VisitType getVisitStatusData(final VisitStatusApiModel visitStatus) throws ServiceException {

		if (visitStatus == null) {
			return null;
		}

		try {
			return VisitType.valueOf(visitStatus.toString());
		} catch (final IllegalArgumentException e) {
			throw new ServiceException(String.format("Incompatible visit status: %s", visitStatus));
		}
	}

}
