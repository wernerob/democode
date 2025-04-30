package hu.ksh.idgs.worklist.dto;

import java.io.Serializable;
import java.util.List;

public class WorklistZoneDto implements Serializable {

	private static final long serialVersionUID = -517010413912950665L;

	private String formCode;
	private Integer year;
	private String period;
	private String zoneId;
	private String settlementCode;
	private String zoneSeq;
	private List<WorklistAddressDto> addresses;

	public String getFormCode() {
		return this.formCode;
	}

	public void setFormCode(final String formCode) {
		this.formCode = formCode;
	}

	public Integer getYear() {
		return this.year;
	}

	public void setYear(final Integer year) {
		this.year = year;
	}

	public String getPeriod() {
		return this.period;
	}

	public void setPeriod(final String period) {
		this.period = period;
	}

	public String getZoneId() {
		return this.zoneId;
	}

	public void setZoneId(final String zoneId) {
		this.zoneId = zoneId;
	}

	public String getSettlementCode() {
		return this.settlementCode;
	}

	public void setSettlementCode(final String settlementCode) {
		this.settlementCode = settlementCode;
	}

	public String getZoneSeq() {
		return this.zoneSeq;
	}

	public void setZoneSeq(final String zoneSeq) {
		this.zoneSeq = zoneSeq;
	}

	public List<WorklistAddressDto> getAddresses() {
		return this.addresses;
	}

	public void setAddresses(final List<WorklistAddressDto> addresses) {
		this.addresses = addresses;
	}

}
