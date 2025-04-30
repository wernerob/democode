package hu.ksh.idgs.worklist.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Component;

import hu.ksh.idgs.core.dto.form.DataArrivalDto;
import hu.ksh.idgs.core.dto.form.FormSaveDto;
import hu.ksh.idgs.core.dto.form.FormSaveFieldDto;
import hu.ksh.idgs.core.dto.form.FormSaveFormDto;
import hu.ksh.idgs.core.dto.form.FormSaveRecordDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitStatusSendFieldDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.model.value.WorklistCreateType;
import hu.ksh.idgs.worklist.service.VisitStatusSendItems;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormLoad;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;

@Component("visitStatusSendAcItems")
public class VisitStatusSendAcItems extends VisitStatusSendCommonItems implements VisitStatusSendItems {

	private static final Integer MAIN_FORM_IDX = 1;

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

		final VisitType visitType = sendItemData.getVisitType();

		final DataArrivalDto dataArrival = getDataArrival(authorization, worklistId);
		final String saveRole = dataArrival == null ? null : dataArrival.getSaveRole();
		final VisitStatusSendItemDto itemA = createSendableItem(authorization, sendItemData,
				VisitStatusSendAcItems.MAIN_FORM_IDX, null, saveRole, dataArrival);
		itemA.setSpendTime(getSpendTime(dataArrival));
		itemA.setSaveRole(dataArrival.getSaveRole());
		itemWithChilds.add(itemA);

		itemA.setFields(getFields(authorization, worklistId, visitType, sendItemData.getVisitStatusCode(),
				sendItemData.getWorklistCreateType()));

		return itemWithChilds;
	}

	private List<VisitStatusSendFieldDto> getFields(final String authorization, final IdgsEntityId worklistId,
			final VisitType visitType, final String visitStatusCode, final WorklistCreateType worklistCreateType)
			throws ServiceException {

		final JsonParser jsonParser = JsonParserFactory.getJsonParser();

		FormSaveRecordDto mainFormRecord = null;
		final Map<String, FormSaveFormDto> tableMap = new HashMap<>();

		if (VisitType.SUCCESS.equals(visitType)) {
			FormSaveFormDto formData = null;
			List<FormSaveRecordDto> mainFormRecords = null;
			List<FormSaveFormDto> tables = new ArrayList<>();
			final FormSaveDto formSaveData = this.formServiceProxyCallFormLoad.getForm(authorization, worklistId);

			if (formSaveData != null) {
				formData = formSaveData.getForm();
			}
			if (formData != null) {
				mainFormRecords = formData.getRecords();
				tables = formData.getTables();
				tables.stream().collect(Collectors.toMap(FormSaveFormDto::getId, Function.identity()));

			}
			if (mainFormRecords != null && !mainFormRecords.isEmpty()) {
				mainFormRecord = mainFormRecords.get(0);
			}

			if (tables != null) {
				tableMap.putAll(tables.stream().collect(Collectors.toMap(FormSaveFormDto::getId, Function.identity())));
			}
		}

		final List<VisitStatusSendFieldDto> fields = new ArrayList<>();

		final String hogFieldValue = getHogValue(tableMap);
		final String henFieldValue = getHenValue(tableMap);
		final String grapeFieldValue = getGrapeValue(tableMap);
		final String fruityFieldValue = getFruityValue(tableMap);

		addField(fields, getFieldEconomyWithModul(visitStatusCode));
		addField(fields, getFieldEconomyWithoutModul(visitStatusCode));
		addField(fields, getFieldUnderLimit(visitStatusCode));
		addField(fields, getFieldNonDataProvider(visitStatusCode));
		addField(fields, getFieldLot(visitStatusCode));
		addField(fields, getFieldSuccessNewEconomy(worklistCreateType));
		addField(fields, getFieldStatusCategory(visitStatusCode));
		addField(fields, getFieldKshMailFrequency(mainFormRecord));
		//
		addField(fields, getFieldJustHog(tableMap, jsonParser, hogFieldValue));
		addField(fields, getFieldJustHen(tableMap, jsonParser, henFieldValue));
		addField(fields, getFieldJustGrape(tableMap, jsonParser, grapeFieldValue));
		addField(fields, getFieldJustFruity(tableMap, jsonParser, fruityFieldValue));
		addField(fields, getFieldStartWeb(mainFormRecord));
		//
		addField(fields, getFieldArableLand(mainFormRecord));
		addField(fields, getFieldGarden(mainFormRecord));
		addField(fields, getFieldFruity(fruityFieldValue));
		addField(fields, getFieldGrape(grapeFieldValue));
		addField(fields, getFieldLawn(tableMap));
		addField(fields, getFieldForest(mainFormRecord));
		addField(fields, getFieldFishPond(mainFormRecord));
		addField(fields, getFieldReed(mainFormRecord));
		//
		addField(fields, getFieldCow(tableMap));
		addField(fields, getFieldHog(hogFieldValue));
		addField(fields, getFieldSheep(tableMap));
		addField(fields, getFieldGoat(tableMap));
		addField(fields, getFieldOstrich(mainFormRecord));
		addField(fields, getFieldHen(henFieldValue));
		addField(fields, getFieldTurkey(tableMap));
		addField(fields, getFieldDuck(tableMap));
		addField(fields, getFieldGoose(tableMap));
		addField(fields, getFieldRabbit(tableMap));
		//
		addField(fields, getFieldAgriculture(mainFormRecord, jsonParser));

		return fields;

	}

	private String getTableFieldValue(final FormSaveFormDto table, final String fieldId, final int recordIdx) {

		if (table == null) {
			return null;
		}

		final List<FormSaveRecordDto> records = table.getRecords();
		if (records == null || records.isEmpty()) {
			return null;
		}

		final FormSaveRecordDto record = records.get(recordIdx);
		return getRecordValue(record, fieldId);

	}

	private String getRecordValue(final FormSaveRecordDto record, final String fieldId) {
		if (record == null) {
			return null;
		}

		final List<FormSaveFieldDto> fields = record.getFields();
		if (fields == null) {
			return null;
		}

		for (final FormSaveFieldDto field : fields) {
			if (fieldId.equals(field.getName())) {
				return field.getValue();
			}
		}

		return null;
	}

	private void addField(final List<VisitStatusSendFieldDto> fields, final VisitStatusSendFieldDto field)
			throws ServiceException {
		fields.add(field);
	}

	private VisitStatusSendFieldDto getFieldEconomyWithModul(final String visitStatusCode) {

		final String fieldValue = "10".equals(visitStatusCode) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.ECONOMY_WITH_MODUL, fieldValue);
	}

	private VisitStatusSendFieldDto getFieldEconomyWithoutModul(final String visitStatusCode) {

		final String fieldValue = "11".equals(visitStatusCode) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.ECONOMY_WITHOUT_MODUL, fieldValue);
	}

	private VisitStatusSendFieldDto getFieldUnderLimit(final String visitStatusCode) {

		final String fieldValue = "12".equals(visitStatusCode) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.UNDER_LIMIT, fieldValue);
	}

	private VisitStatusSendFieldDto getFieldNonDataProvider(final String visitStatusCode) {

		final String fieldValue = "13".equals(visitStatusCode) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.NON_DATA_PROVIDER, fieldValue);
	}

	private VisitStatusSendFieldDto getFieldLot(final String visitStatusCode) {

		final String fieldValue = "14".equals(visitStatusCode) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.LOT, fieldValue);
	}

	private VisitStatusSendFieldDto getFieldSuccessNewEconomy(final WorklistCreateType worklistCreateType) {

		final String fieldValue = WorklistCreateType.NEW_HOUSEHOLD.equals(worklistCreateType) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.NEW_ECONOMY, fieldValue);

	}

	private VisitStatusSendFieldDto getFieldStatusCategory(final String visitStatusCode) {

		final String visitStatusCodeSecu = visitStatusCode == null ? "" : visitStatusCode;

		String fieldValue;
		switch (visitStatusCodeSecu) {
		case "10":
		case "11":
			fieldValue = "GA";
			break;
		case "12":
			fieldValue = "KA";
			break;
		case "13":
			fieldValue = "NA";
			break;
		case "14":
			fieldValue = "HG";
			break;
		case "22":
			fieldValue = "NL";
			break;
		default:
			fieldValue = "";
		}

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.STATUS_CATEGORY, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldKshMailFrequency(final FormSaveRecordDto record) {

		final String mail = getRecordValue(record, "END_1");
		final String fieldValue = "ksh@ksh.hu".equals(mail) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.KSH_MAIL_FREQ, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldJustHog(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser, final String hogFieldValue) {

		final VisitStatusSendFieldDto zeroField = new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_HOG, "0");
		final List<Integer> a0ValuableRowIndexes = getA0ValuableRowIndexes(tableMap, jsonParser);
		if (a0ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b000ValuableRowIndexes = getB000ValuableRowIndexes(tableMap, jsonParser);
		if (b000ValuableRowIndexes.size() != 1) {
			return zeroField;
		}

		if (b000ValuableRowIndexes.get(0) != 1) {
			return zeroField;
		}

		final float hogSum = hogFieldValue == null ? 0 : Float.parseFloat(hogFieldValue);
		if (hogSum < 2 || hogSum > 3) {
			return zeroField;
		}

		final List<Integer> b001ValuableRowIndexes = getB001ValuableRowIndexes(tableMap, jsonParser);
		if (b001ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b01ValuableRowIndexes = getB01ValuableRowIndexes(tableMap, jsonParser);
		if (b01ValuableRowIndexes.size() != 0) {
			return zeroField;
		}

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_HOG, "1");

	}

	private VisitStatusSendFieldDto getFieldJustHen(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser, final String henFieldValue) {

		final VisitStatusSendFieldDto zeroField = new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_HEN, "0");
		final List<Integer> a0ValuableRowIndexes = getA0ValuableRowIndexes(tableMap, jsonParser);
		if (a0ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b000ValuableRowIndexes = getB000ValuableRowIndexes(tableMap, jsonParser);
		if (b000ValuableRowIndexes.size() != 1) {
			return zeroField;
		}

		if (b000ValuableRowIndexes.get(0) != 3) {
			return zeroField;
		}

		final float henSum = henFieldValue == null ? 0 : Float.parseFloat(henFieldValue);
		if (henSum < 50 || henSum > 60) {
			return zeroField;
		}

		final List<Integer> b001ValuableRowIndexes = getB001ValuableRowIndexes(tableMap, jsonParser);
		if (b001ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b01ValuableRowIndexes = getB01ValuableRowIndexes(tableMap, jsonParser);
		for (final Integer b01ValuableRowIndex : b01ValuableRowIndexes) {
			if (b01ValuableRowIndex != 0) {
				return zeroField;
			}
		}

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_HEN, "1");

	}

	private VisitStatusSendFieldDto getFieldJustGrape(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser, final String grapeFieldValue) {

		final VisitStatusSendFieldDto zeroField = new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_GRAPE, "0");
		final List<Integer> a0ValuableRowIndexes = getA0ValuableRowIndexes(tableMap, jsonParser);
		if (a0ValuableRowIndexes.size() != 1) {
			return zeroField;
		}
		if (a0ValuableRowIndexes.get(0) != 4) {
			return zeroField;
		}

		final float grapeSum = grapeFieldValue == null ? 0 : Float.parseFloat(grapeFieldValue);
		if (grapeSum < 1000 || grapeSum > 1100) {
			return zeroField;
		}

		final List<Integer> b000ValuableRowIndexes = getB000ValuableRowIndexes(tableMap, jsonParser);
		if (b000ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b001ValuableRowIndexes = getB001ValuableRowIndexes(tableMap, jsonParser);
		if (b001ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b01ValuableRowIndexes = getB01ValuableRowIndexes(tableMap, jsonParser);
		if (b01ValuableRowIndexes.size() != 0) {
			return zeroField;
		}

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_GRAPE, "1");
	}

	private VisitStatusSendFieldDto getFieldJustFruity(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser, final String fruityFieldValue) {

		final VisitStatusSendFieldDto zeroField = new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_FRUITY, "0");

		final List<Integer> a0ValuableRowIndexes = getA0ValuableRowIndexes(tableMap, jsonParser);
		if (a0ValuableRowIndexes.size() != 1) {
			return zeroField;
		}
		if (a0ValuableRowIndexes.get(0) != 3) {
			return zeroField;
		}

		final float fruitySum = fruityFieldValue == null ? 0 : Float.parseFloat(fruityFieldValue);
		if (fruitySum < 2500 || fruitySum > 2750) {
			return zeroField;
		}

		final List<Integer> b000ValuableRowIndexes = getB000ValuableRowIndexes(tableMap, jsonParser);
		if (b000ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b001ValuableRowIndexes = getB001ValuableRowIndexes(tableMap, jsonParser);
		if (b001ValuableRowIndexes.size() != 0) {
			return zeroField;
		}
		final List<Integer> b01ValuableRowIndexes = getB01ValuableRowIndexes(tableMap, jsonParser);
		if (b01ValuableRowIndexes.size() != 0) {
			return zeroField;
		}

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.JUST_FRUITY, "1");

	}

	private VisitStatusSendFieldDto getFieldStartWeb(final FormSaveRecordDto record) {

		final boolean fieldValue = Boolean.valueOf(getRecordValue(record, "START_WEB"));
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.START_WEB, fieldValue ? "1" : "0");
	}

	private VisitStatusSendFieldDto getFieldArableLand(final FormSaveRecordDto record) {
		final String fieldValue = getRecordValue(record, "MFAA001_0100");
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.ARABLE_LAND, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldGarden(final FormSaveRecordDto record) {

		final String fieldValue = getRecordValue(record, "A11");
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.GARDEN, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldFruity(final String fieldValue) {

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.FRUITY, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldGrape(final String fieldValue) {
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.GRAPE, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldLawn(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("A18");
		final String fieldValue = getTableFieldValue(table, "MFAA001", 3);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.LAWN, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldForest(final FormSaveRecordDto record) {

		final String fieldValue = getRecordValue(record, "A23");
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.FOREST, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldFishPond(final FormSaveRecordDto record) {

		final String fieldValue = getRecordValue(record, "A28");
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.FISHPOND, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldReed(final FormSaveRecordDto record) {

		final String fieldValue = getRecordValue(record, "A26");
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.REED, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldCow(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B1");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 15);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.COW, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldHog(final String fieldValue) {

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.HOG, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldSheep(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B3");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 4);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.SHEEP, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldGoat(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B8");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 5);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.GOAT, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldOstrich(final FormSaveRecordDto record) {

		final String fieldValue = getRecordValue(record, "B11_9_1");
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.OSTRICH, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldHen(final String fieldValue) {

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.HEN, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldTurkey(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B7");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 5);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.TURKEY, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldDuck(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B6");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 5);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.DUCK, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldGoose(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B5");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 5);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.GOOSE, getValue(fieldValue));

	}

	private VisitStatusSendFieldDto getFieldRabbit(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B11_2");
		final String fieldValue = getTableFieldValue(table, "MAAA004", 0);

		return new VisitStatusSendFieldDto(VisitStatusSendAcField.RABBIT, getValue(fieldValue));
	}

	private VisitStatusSendFieldDto getFieldAgriculture(final FormSaveRecordDto record, final JsonParser jsonParser) {

		final String json = getRecordValue(record, "E3");
		final Object jsonValue = getJsonValue(json, "value", jsonParser);
		final String fieldValue = "4".equals(jsonValue) ? "1" : "0";
		return new VisitStatusSendFieldDto(VisitStatusSendAcField.AGRICULTURE,
				fieldValue == null ? null : fieldValue.toString());

	}

	private List<Integer> getTableValuableRowIndexes(final Map<String, FormSaveFormDto> tableMap, final String tableId,
			final String fieldId, final JsonParser jsonParser) {

		final List<Integer> valuableRowIndexes = new ArrayList<>();

		final FormSaveFormDto table = tableMap.get(tableId);
		if (table == null) {
			return Collections.emptyList();
		}

		final List<FormSaveRecordDto> records = table.getRecords();
		if (records == null || records.isEmpty()) {
			return Collections.emptyList();
		}

		for (int rowIdx = 0; rowIdx < records.size(); rowIdx++) {
			final FormSaveRecordDto record = records.get(rowIdx);
			if (record == null) {
				continue;
			}

			final String value = getRecordValue(record, fieldId);
			final Object jsonValue = getJsonValue(value, "value", jsonParser);
			if ("1".equals(jsonValue)) {
				valuableRowIndexes.add(rowIdx);
			}
		}

		return valuableRowIndexes;

	}

	private Object getJsonValue(final String json, final String property, final JsonParser jsonParser) {

		if (json == null) {
			return null;
		}
		final Map<String, Object> jsonMap = jsonParser.parseMap(json);
		return jsonMap.get(property);
	}

	private String getFruityValue(final Map<String, FormSaveFormDto> tableMap) throws ServiceException {

		final FormSaveFormDto tableA12_1 = tableMap.get("A12_1");
		Float a12_1Sum = getSumTableField(tableA12_1, "MIGE001");

		final FormSaveFormDto tableA12_2 = tableMap.get("A12_2");
		Float a12_2Sum = getSumTableField(tableA12_2, "MIGE101");

		if (a12_1Sum == null) {
			a12_1Sum = 0f;
		}

		if (a12_2Sum == null) {
			a12_2Sum = 0f;
		}

		final float sum = Float.sum(a12_1Sum, a12_2Sum);
		return String.format(Locale.ROOT, "%.4f", sum);
	}

	private String getGrapeValue(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("A15");
		return getTableFieldValue(table, "MIGE201", 3);

	}

	private String getHenValue(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B4");
		return getTableFieldValue(table, "MAAA004", 0);

	}

	private String getHogValue(final Map<String, FormSaveFormDto> tableMap) {

		final FormSaveFormDto table = tableMap.get("B2");
		return getTableFieldValue(table, "MAAA004", 10);
	}

	private List<Integer> getA0ValuableRowIndexes(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser) {
		return getTableValuableRowIndexes(tableMap, "A0", "HASZNALAT", jsonParser);
	}

	private List<Integer> getB000ValuableRowIndexes(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser) {

		return getTableValuableRowIndexes(tableMap, "B000", "TARTAS", jsonParser);
	}

	private List<Integer> getB001ValuableRowIndexes(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser) {
		return getTableValuableRowIndexes(tableMap, "B001", "TARTAS", jsonParser);
	}

	private List<Integer> getB01ValuableRowIndexes(final Map<String, FormSaveFormDto> tableMap,
			final JsonParser jsonParser) {
		return getTableValuableRowIndexes(tableMap, "B01", "SANI", jsonParser);
	}

	private String getValue(final String fieldValue) {
		return fieldValue == null ? "0" : fieldValue;
	}

	private Float getSumTableField(final FormSaveFormDto table, final String fieldId) throws ServiceException {

		if (table == null) {
			return null;
		}

		BigDecimal sum = BigDecimal.ZERO;

		for (final FormSaveRecordDto record : table.getRecords()) {
			if (record == null) {
				continue;
			}
			for (final FormSaveFieldDto field : record.getFields()) {
				if (field == null) {
					continue;
				}
				if (fieldId.equals(field.getName())) {

					final String fieldValue = field.getValue() == null ? "0" : field.getValue();
					try {
						final BigDecimal value = new BigDecimal(fieldValue);
						sum = sum.add(value);
						sum = sum.setScale(4, RoundingMode.CEILING);
					} catch (final NumberFormatException e) {
						throw new ServiceException(
								String.format("%s field value %s is not a number", fieldId, field.getValue()));
					}
				}
			}

		}
		return sum.floatValue();

	}

}
