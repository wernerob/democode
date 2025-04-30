package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.time.OffsetDateTime;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import hu.ksh.idgs.core.model.IdgsEntity;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.maja.core.dto.VisitType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Audited
@Entity
@Table(name = "visit_status")
public class VisitStatusEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = -2008560394718207796L;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "worklist_item_id")
	private WorklistItemEntity worklistItem;

	@Size(max = 36)
	@Column(name = "visit_status_id", length = 36)
	private String visitStatusId;

	@Size(max = 36)
	@Column(name = "geo_location_id", length = 36)
	private String geoLocationId;

	@Size(max = 1024)
	@Column(name = "note", length = 1024)
	private String note;

	@NotNull
	@Column(name = "visit_time", nullable = false)
	private OffsetDateTime time;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "visit_type", nullable = false)
	private VisitType type;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "send_status", nullable = false)
	private SendStatusType sendStatus;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "visit_status_send_id")
	private VisitStatusSendEntity visitStatusSend;

	@Size(max = 2048)
	@Column(name = "visit_form", length = 2048)
	private String visitForm;

	@Size(max = 2)
	@Column(name = "visit_code", length = 2)
	private String visitCode;

	public SendStatusType getSendStatus() {
		return this.sendStatus;
	}

	public void setSendStatus(final SendStatusType sendStatus) {
		this.sendStatus = sendStatus;
	}

	public WorklistItemEntity getWorklistItem() {
		return this.worklistItem;
	}

	public void setWorklistItem(final WorklistItemEntity worklistItem) {
		this.worklistItem = worklistItem;
	}

	public String getVisitStatusId() {
		return this.visitStatusId;
	}

	public void setVisitStatusId(final String visitStatusId) {
		this.visitStatusId = visitStatusId;
	}

	public String getGeoLocationId() {
		return this.geoLocationId;
	}

	public void setGeoLocationId(final String geoLocationId) {
		this.geoLocationId = geoLocationId;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public OffsetDateTime getTime() {
		return this.time;
	}

	public void setTime(final OffsetDateTime time) {
		this.time = time;
	}

	public VisitType getType() {
		return this.type;
	}

	public void setType(final VisitType type) {
		this.type = type;
	}

	public VisitStatusSendEntity getVisitStatusSend() {
		return this.visitStatusSend;
	}

	public void setVisitStatusSend(final VisitStatusSendEntity visitStatusSend) {
		this.visitStatusSend = visitStatusSend;
	}

	public String getVisitForm() {
		return this.visitForm;
	}

	public void setVisitForm(final String visitForm) {
		this.visitForm = visitForm;
	}

	public String getVisitCode() {
		return this.visitCode;
	}

	public void setVisitCode(final String visitCode) {
		this.visitCode = visitCode;
	}

	@Override
	public String toString() {
		return "VisitStatusEntity [worklistItem=" + this.worklistItem + ", visitStatusId=" + this.visitStatusId
				+ ", geoLocationId=" + this.geoLocationId + "]";
	}

}
