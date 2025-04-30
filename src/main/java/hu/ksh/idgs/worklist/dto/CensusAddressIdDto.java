package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;

public class CensusAddressIdDto implements Serializable {

	private static final long serialVersionUID = -7605547708698916193L;
	private String kshAddressId;
	private String houseHoldId;

	public CensusAddressIdDto() {
	}

	public CensusAddressIdDto(final String kshAddressId, final String houseHoldId) {
		this.kshAddressId = kshAddressId;
		this.houseHoldId = houseHoldId;
	}

	public String getKshAddressId() {
		return this.kshAddressId;
	}

	public void setKshAddressId(final String kshAddressId) {
		this.kshAddressId = kshAddressId;
	}

	public String getHouseHoldId() {
		return this.houseHoldId;
	}

	public void setHouseHoldId(final String houseHoldId) {
		this.houseHoldId = houseHoldId;
	}

}
