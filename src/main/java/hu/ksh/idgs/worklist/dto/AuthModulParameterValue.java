package hu.ksh.idgs.worklist.dto;

public enum AuthModulParameterValue {

	AUTH("auth"), //
	USERMGNT("usermgnt");

	private String value;

	AuthModulParameterValue(final String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public static AuthModulParameterValue findByValue(final String value) {
		for (final AuthModulParameterValue param : values()) {
			if (param.getValue().equals(value)) {
				return param;
			}
		}
		return null;
	}

}
