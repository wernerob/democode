package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.util.List;

public class AddressDto implements Serializable {

	private static final long serialVersionUID = 255561169808019293L;

	private String zipCode;
	private String countyCode;
	private String settlementCode;
	private String settlement;
	private String settlementPart;
	private String placeName;
	private String placeType;
	private String houseNumber;
	private String parcelNumber;
	private String building;
	private String stairway;
	private String floor;
	private String door;
	private List<HouseHoldDto> households;
	private String formattedAddress;

	public AddressDto(final String zipCode, final String settlementCode, final String settlement,
			final String placeName, final String placeType, final String houseNumber, final String parcelNumber,
			final String building, final String stairway, final String floor, final String door) {

		this.zipCode = zipCode;
		this.settlementCode = settlementCode;
		this.settlement = settlement;
		this.placeName = placeName;
		this.placeType = placeType;
		this.houseNumber = houseNumber;
		this.parcelNumber = parcelNumber;
		this.building = building;
		this.stairway = stairway;
		this.floor = floor;
		this.door = door;
	}

	public AddressDto() {
	}

	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	public String getCountyCode() {
		return this.countyCode;
	}

	public void setCountyCode(final String countyCode) {
		this.countyCode = countyCode;
	}

	public String getSettlementCode() {
		return this.settlementCode;
	}

	public void setSettlementCode(final String settlementCode) {
		this.settlementCode = settlementCode;
	}

	public String getSettlement() {
		return this.settlement;
	}

	public void setSettlement(final String settlement) {
		this.settlement = settlement;
	}

	public String getSettlementPart() {
		return this.settlementPart;
	}

	public void setSettlementPart(final String settlementPart) {
		this.settlementPart = settlementPart;
	}

	public String getPlaceName() {
		return this.placeName;
	}

	public void setPlaceName(final String placeName) {
		this.placeName = placeName;
	}

	public String getPlaceType() {
		return this.placeType;
	}

	public void setPlaceType(final String placeType) {
		this.placeType = placeType;
	}

	public String getHouseNumber() {
		return this.houseNumber;
	}

	public void setHouseNumber(final String houseNumber) {
		this.houseNumber = houseNumber;
	}

	public String getParcelNumber() {
		return this.parcelNumber;
	}

	public void setParcelNumber(final String parcelNumber) {
		this.parcelNumber = parcelNumber;
	}

	public String getBuilding() {
		return this.building;
	}

	public void setBuilding(final String building) {
		this.building = building;
	}

	public String getStairway() {
		return this.stairway;
	}

	public void setStairway(final String stairway) {
		this.stairway = stairway;
	}

	public String getFloor() {
		return this.floor;
	}

	public void setFloor(final String floor) {
		this.floor = floor;
	}

	public String getDoor() {
		return this.door;
	}

	public void setDoor(final String door) {
		this.door = door;
	}

	public List<HouseHoldDto> getHouseholds() {
		return this.households;
	}

	public void setHouseholds(final List<HouseHoldDto> households) {
		this.households = households;
	}

	public String getFormattedAddress() {
		return this.formattedAddress;
	}

	public void setFormattedAddress(final String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

}
