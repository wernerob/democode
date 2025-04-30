package hu.ksh.idgs.worklist.dto;

import hu.ksh.maja.core.value.MajaErrorValue;

public enum AddNewHouseHoldErrorValue implements MajaErrorValue {

	EXTERNAL_ADDRESS_NOT_FOUND, //
	FORM_TEMPLATE_NOT_FOUND, //
	GENERAL_ERROR;

	@Override
	public String getErrorCode() {
		return name();
	}

}
