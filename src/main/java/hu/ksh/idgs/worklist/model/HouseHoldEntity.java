package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.util.List;

import org.hibernate.envers.Audited;

import hu.ksh.idgs.core.model.IdgsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Audited
@Entity
@Table(name = "household")
public class HouseHoldEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = -1613264045297932165L;

	@NotNull
	@Size(max = 6)
	@Column(name = "internal_id", length = 6, nullable = false)
	private String internalId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private AddressEntity address;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "houseHold")
	private List<WorklistItemEntity> worklist;

	public String getInternalId() {
		return this.internalId;
	}

	public void setInternalId(final String internalId) {
		this.internalId = internalId;
	}

	public AddressEntity getAddress() {
		return this.address;
	}

	public void setAddress(final AddressEntity address) {
		this.address = address;
	}

}
