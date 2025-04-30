package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class AddressAddDto implements Serializable {

	private static final long serialVersionUID = -182376370792087758L;

	private AddressDto address;
	private String formTemplateId;
	private String authorization;
	private String email;
	private String userName;

	public AddressDto getAddress() {
		return this.address;
	}

	public void setAddress(final AddressDto address) {
		this.address = address;
	}

	public String getFormTemplateId() {
		return this.formTemplateId;
	}

	public void setFormTemplateId(final String formTemplateId) {
		this.formTemplateId = formTemplateId;
	}

	public String getAuthorization() {
		return this.authorization;
	}

	public void setAuthorization(final String authorization) {
		this.authorization = authorization;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

}
