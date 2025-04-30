package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class WorklistUserDto implements Serializable {

	private static final long serialVersionUID = 5142478959844785080L;

	private String userName;
	private String lastName;
	private String firstName;
	private String passwordHash;
	private String passwordHashPbkdf2;
	private String token;
	private String email;

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getPasswordHash() {
		return this.passwordHash;
	}

	public void setPasswordHash(final String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getPasswordHashPbkdf2() {
		return this.passwordHashPbkdf2;
	}

	public void setPasswordHashPbkdf2(final String passwordHashPbkdf2) {
		this.passwordHashPbkdf2 = passwordHashPbkdf2;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

}
