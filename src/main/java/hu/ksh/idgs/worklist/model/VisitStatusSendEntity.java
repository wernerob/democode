package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import hu.ksh.idgs.core.model.IdgsEntity;

@Entity
@Table(name = "visit_status_send")
public class VisitStatusSendEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = 7184843367797811894L;

	@Size(max = 15)
	@NotNull
	@Column(name = "package_id", length = 15, nullable = false)
	private String packageId;

	@NotNull
	@Column(name = "event_time", nullable = false)
	private OffsetDateTime eventTime;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "visitStatusSend")
	private List<VisitStatusEntity> visitStatus;

	public String getPackageId() {
		return this.packageId;
	}

	public void setPackageId(final String packageId) {
		this.packageId = packageId;
	}

	public OffsetDateTime getEventTime() {
		return this.eventTime;
	}

	public void setEventTime(final OffsetDateTime eventTime) {
		this.eventTime = eventTime;
	}

	public List<VisitStatusEntity> getVisitStatus() {
		return this.visitStatus;
	}

	public void setVisitStatus(final List<VisitStatusEntity> visitStatus) {
		this.visitStatus = visitStatus;
	}

}
