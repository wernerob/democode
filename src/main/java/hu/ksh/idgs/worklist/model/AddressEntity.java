package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import hu.ksh.idgs.core.model.IdgsEntity;

@Audited
@Entity
@Table(name = "address")
public class AddressEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = -2412477266117210631L;

	@Size(max = 5)
	@Column(name = "zip_code", length = 5)
	private String zipCode;

	@Size(max = 50)
	@Column(name = "settlement", length = 50)
	private String settlement;

	@Size(max = 50)
	@Column(name = "place_name", length = 50)
	private String placeName;

	@Size(max = 30)
	@Column(name = "place_type", length = 30)
	private String placeType;

	@Size(max = 13)
	@Column(name = "house_number", length = 13)
	private String houseNumber;

	@Size(max = 20)
	@Column(name = "parcel_number", length = 20)
	private String parcelNumber;

	@Size(max = 5)
	@Column(name = "building", length = 5)
	private String building;

	@Size(max = 20)
	@Column(name = "stairway", length = 20)
	private String stairway;

	@Size(max = 8)
	@Column(name = "floor", length = 8)
	private String floor;

	@Size(max = 5)
	@Column(name = "door", length = 5)
	private String door;

	@NotAudited
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "zone_address_link", joinColumns = @JoinColumn(name = "address_id"), inverseJoinColumns = @JoinColumn(name = "zone_id"))
	private final Set<ZoneEntity> zones = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "address")
	private List<HouseHoldEntity> houseHolds;

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "address")
	private WorklistAssociationEntity worklistAssociation;

	@Size(max = 2)
	@Column(name = "county_code", length = 2)
	private String countyCode;

	@Size(max = 6)
	@Column(name = "settlement_code", length = 6)
	private String settlementCode;

	@Size(max = 3)
	@Column(name = "settlement_part", length = 3)
	private String settlementPart;

	@Size(max = 100)
	@Column(name = "formatted_address", length = 100)
	private String formattedAddress;

	public String getZipCode() {
		return this.zipCode;
	}

	public void setZipCode(final String zipCode) {
		this.zipCode = zipCode;
	}

	public String getSettlement() {
		return this.settlement;
	}

	public void setSettlement(final String settlement) {
		this.settlement = settlement;
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

	public Set<ZoneEntity> getZones() {
		return this.zones;
	}

	public List<HouseHoldEntity> getHouseHolds() {
		return this.houseHolds;
	}

	public void setHouseHolds(final List<HouseHoldEntity> houseHolds) {
		this.houseHolds = houseHolds;
	}

	public WorklistAssociationEntity getWorklistAssociation() {
		return this.worklistAssociation;
	}

	public void setWorklistAssociation(final WorklistAssociationEntity worklistAssociation) {
		this.worklistAssociation = worklistAssociation;
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

	public String getSettlementPart() {
		return this.settlementPart;
	}

	public void setSettlementPart(final String settlementPart) {
		this.settlementPart = settlementPart;
	}

	public String getFormattedAddress() {
		return this.formattedAddress;
	}

	public void setFormattedAddress(final String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	@Override
	public String toString() {
		return "AddressEntity [zipCode=" + this.zipCode + ", settlement=" + this.settlement + ", placeName="
				+ this.placeName + ", placeType=" + this.placeType + ", houseNumber=" + this.houseNumber
				+ ", parcelNumber=" + this.parcelNumber + ", building=" + this.building + ", stairway=" + this.stairway
				+ ", floor=" + this.floor + ", door=" + this.door + ", zones=" + this.zones + "]";
	}

}
