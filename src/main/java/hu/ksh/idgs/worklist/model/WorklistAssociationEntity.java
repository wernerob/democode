package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import hu.ksh.idgs.core.model.IdgsEntity;
import hu.ksh.idgs.worklist.model.value.ExternalSystem;

@Entity
@Table(name = "worklist_association")
public class WorklistAssociationEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = 4057557449906000308L;

	@NotNull
	@Size(max = 36)
	@Column(name = "external_id", length = 36, nullable = false)
	private String externalId;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "external_system", nullable = false)
	private ExternalSystem externalSystem;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "worklist_association_item_link", joinColumns = @JoinColumn(name = "worklist_association_id"), inverseJoinColumns = @JoinColumn(name = "worklist_item_id"))
	private final Set<WorklistItemEntity> worklistItems = new HashSet<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id")
	private AddressEntity address;

	public String getExternalId() {
		return this.externalId;
	}

	public void setExternalId(final String externalId) {
		this.externalId = externalId;
	}

	public ExternalSystem getExternalSystem() {
		return this.externalSystem;
	}

	public void setExternalSystem(final ExternalSystem externalSystem) {
		this.externalSystem = externalSystem;
	}

	public Set<WorklistItemEntity> getWorklistItems() {
		return this.worklistItems;
	}

	public AddressEntity getAddress() {
		return this.address;
	}

	public void setAddress(final AddressEntity address) {
		this.address = address;
	}

	@Override
	public String toString() {
		return "WorklistAssociationEntity [externalId=" + this.externalId + ", externalSystem=" + this.externalSystem
				+ ", worklistItems=" + this.worklistItems + ", address=" + this.address + "]";
	}

}
