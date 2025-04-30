package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class SaveTokenDto implements Serializable {

	private static final long serialVersionUID = -448746932871296797L;

	private String tokenId;
	private String token;

	public SaveTokenDto() {
	}

	public SaveTokenDto(final String tokenId, final String token) {
		this.tokenId = tokenId;
		this.token = token;
	}

	public String getTokenId() {
		return this.tokenId;
	}

	public void setTokenId(final String tokenId) {
		this.tokenId = tokenId;
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

}
