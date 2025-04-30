package hu.ksh.idgs.worklist.api.controller.converter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.api.model.CodeTableItemApiModel;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CodeTableConverter {

	public CodeTableItemApiModel getCodeTableItem(final CodeTableItemDto codeTableItemData) {

		if (codeTableItemData == null) {
			return null;
		}

		final IdgsEntityId id = codeTableItemData.getId();

		return new CodeTableItemApiModel()//
				.id(id == null ? null : id.toString())//
				.code(codeTableItemData.getCode())//
				.description(codeTableItemData.getDesciption())//
				.valid(codeTableItemData.isValid());
	}

	public List<CodeTableItemApiModel> getCodeTableItems(final List<CodeTableItemDto> codeTableItemDataList) {

		final List<CodeTableItemApiModel> codeTableItems = new ArrayList<CodeTableItemApiModel>();

		if (codeTableItemDataList == null) {
			return codeTableItems;
		}

		codeTableItemDataList.forEach(codeTableItemData -> codeTableItems.add(getCodeTableItem(codeTableItemData)));
		return codeTableItems;
	}

	public List<CodeTableItemDto> getCodeTableItemDataList(final List<CodeTableItemApiModel> codeTableItems) {

		final List<CodeTableItemDto> codeTableItemDataList = new ArrayList<>();

		if (codeTableItems == null) {
			return codeTableItemDataList;
		}

		codeTableItems.forEach(codeTableItem -> codeTableItemDataList.add(getCodeTableItemData(codeTableItem)));
		return codeTableItemDataList;
	}

	public List<CodeTableItemDto> getCodeTableItemDataList_Form(
			final List<hu.ksh.idgs.form.api.model.CodeTableItemApiModel> codeTableItems) {

		final List<CodeTableItemDto> codeTableItemDataList = new ArrayList<>();

		if (codeTableItems == null) {
			return codeTableItemDataList;
		}

		codeTableItems.forEach(codeTableItem -> codeTableItemDataList.add(getCodeTableItemData_Form(codeTableItem)));
		return codeTableItemDataList;
	}

	public CodeTableItemDto getCodeTableItemData(final CodeTableItemApiModel codeTableItem) {

		if (codeTableItem == null) {
			return null;
		}

		final CodeTableItemDto codeTableData = new CodeTableItemDto();
		codeTableData.setId(IdgsEntityId.fromString(codeTableItem.getId()));
		codeTableData.setCode(codeTableItem.getCode());
		codeTableData.setDesciption(codeTableItem.getDescription());
		codeTableData.setValid(codeTableItem.getValid() == null || codeTableItem.getValid());

		return codeTableData;
	}

	public CodeTableItemDto getCodeTableItemData_Form(
			final hu.ksh.idgs.form.api.model.CodeTableItemApiModel codeTableItem) {

		if (codeTableItem == null) {
			return null;
		}

		final CodeTableItemDto codeTableData = new CodeTableItemDto();
		codeTableData.setId(IdgsEntityId.fromString(codeTableItem.getId()));
		codeTableData.setCode(codeTableItem.getCode());
		codeTableData.setDesciption(codeTableItem.getDescription());
		codeTableData.setValid(codeTableItem.getValid() == null || codeTableItem.getValid());

		return codeTableData;
	}

}
