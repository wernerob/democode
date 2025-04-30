package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import hu.ksh.idgs.core.model.IdgsEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "worklist_user")
public class WorklistUserEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = 8336884466797450045L;

	@Size(max = 36)
	@NotNull
	@Column(name = "user_id", length = 36, nullable = false)
	private String userId;

	@Size(max = 30)
	@Column(name = "user_name", length = 30)
	private String userName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worklist_item_id")
	private WorklistItemEntity worklistItem;

	@Size(max = 36)
	@Column(name = "role_name", length = 36)
	private String roleName;

	@Column(name = "valid_from")
	private OffsetDateTime validFrom;

	@Column(name = "valid_to")
	private OffsetDateTime validTo;

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public WorklistItemEntity getWorklistItem() {
		return this.worklistItem;
	}

	public void setWorklistItem(final WorklistItemEntity worklistItem) {
		this.worklistItem = worklistItem;
	}

	public String getRoleName() {
		return this.roleName;
	}

	public void setRoleName(final String roleName) {
		this.roleName = roleName;
	}

	public OffsetDateTime getValidFrom() {
		return this.validFrom;
	}

	public void setValidFrom(final OffsetDateTime validFrom) {
		this.validFrom = validFrom;
	}

	public OffsetDateTime getValidTo() {
		return this.validTo;
	}

	public void setValidTo(final OffsetDateTime validTo) {
		this.validTo = validTo;
	}

	@Override
	public String toString() {
		return "WorklistUserEntity [userId=" + this.userId + ", worklistItem=" + this.worklistItem + "]";
	}

}
