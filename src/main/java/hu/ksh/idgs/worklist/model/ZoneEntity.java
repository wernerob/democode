package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import hu.ksh.idgs.core.model.IdgsEntity;

@Entity
@Table(name = "zone")
public class ZoneEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = 7451574934534321525L;

	@Size(max = 10)
	@Column(name = "external_id", length = 10)
	private String externalId;

	@Size(max = 6)
	@Column(name = "settlement_code", length = 6)
	private String settlementCode;

	@Size(max = 4)
	@Column(name = "external_seq", length = 4)
	private String externalSeq;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "zone_address_link", joinColumns = @JoinColumn(name = "zone_id"), inverseJoinColumns = @JoinColumn(name = "address_id"))
	private final Set<AddressEntity> addresses = new HashSet<>();

	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(final String externalId) {
		this.externalId = externalId;
	}

	public String getSettlementCode() {
		return this.settlementCode;
	}

	public void setSettlementCode(final String settlementCode) {
		this.settlementCode = settlementCode;
	}

	public String getExternalSeq() {
		return this.externalSeq;
	}

	public void setExternalSeq(final String externalSeq) {
		this.externalSeq = externalSeq;
	}

	public Set<AddressEntity> getAddresses() {
		return this.addresses;
	}

	@Override
	public String toString() {
		return "ZoneEntity [externalId=" + this.externalId + ", addresses=" + this.addresses + "]";
	}

}
