package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.*;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;

import hu.ksh.idgs.core.model.IdgsEntity;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.model.value.WorklistCreateType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.envers.RevisionTimestamp;

@Audited
@Entity
@Table(name = "worklist_item")
public class WorklistItemEntity extends IdgsEntity implements Serializable {

	private static final long serialVersionUID = -7869224992021536112L;

	@NotNull
	@Size(max = 36)
	@Column(name = "form_tmpl_id", length = 36, nullable = false)
	private String formTemplateId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "household_id")
	private HouseHoldEntity houseHold;

	@Size(max = 1024)
	@Column(name = "note", length = 1024)
	private String note;

	@NotAudited
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "worklist_association_item_link", joinColumns = @JoinColumn(name = "worklist_item_id"), inverseJoinColumns = @JoinColumn(name = "worklist_association_id"))
	private final Set<WorklistAssociationEntity> worklistAssociations = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "worklistItem")
	private final List<VisitStatusEntity> visitStatuses = new ArrayList<>();

	@NotAudited
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "worklistItem")
	private final List<WorklistUserEntity> worklistUsers = new ArrayList<>();

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "form_status", nullable = false)
	private FormStatusType formStatus;

	@Column(name = "close_date")
	private OffsetDateTime closeDate;

	@Size(max = 100)
	@Column(name = "worklist_info", length = 100)
	private String worklistInfo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "latest_visit_status_id")
	private VisitStatusEntity latestVisitStatus;

	@Size(max = 3)
	@Column(name = "latest_visit_code", length = 3)
	private String latestVisitCode;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "send_status", nullable = false)
	private SendStatusType sendStatus;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "send_status_update_time", nullable = false)
	private Date sendStatusUpdateTime;

	@Size(max = 1024)
	@Column(name = "send_process_msg", length = 1024)
	private String sendProcessMsg;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "create_type", nullable = false)
	private WorklistCreateType createType;

	@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "latest_note_id")
	private NoteEntity latestNote;

	@Column(name = "item_order")
	private Integer order;

	@Column(name = "start_date")
	private OffsetDateTime startDate;

	@Column(name = "save_date")
	private OffsetDateTime saveDate;

	@Column(name = "fill_date")
	private OffsetDateTime fillDate;

	@Size(max = 100)
	@Column(name = "ospec", length = 100)
	private String ospec;

	@Size(max = 1)
	@Column(name = "succesfull_previous_visit", length = 1)
	private String succesfulPreviousVisit;

	@Size(max = 5)
	@Column(name = "address_type", length = 5)
	private String addressType;

	public String getFormTemplateId() {
		return this.formTemplateId;
	}

	public void setFormTemplateId(final String formTemplateId) {
		this.formTemplateId = formTemplateId;
	}

	public HouseHoldEntity getHouseHold() {
		return this.houseHold;
	}

	public void setHouseHold(final HouseHoldEntity houseHold) {
		this.houseHold = houseHold;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public FormStatusType getFormStatus() {
		return this.formStatus;
	}

	public void setFormStatus(final FormStatusType formStatus) {
		this.formStatus = formStatus;
	}

	public OffsetDateTime getCloseDate() {
		return this.closeDate;
	}

	public void setCloseDate(final OffsetDateTime closeDate) {
		this.closeDate = closeDate;
	}

	public String getWorklistInfo() {
		return this.worklistInfo;
	}

	public void setWorklistInfo(final String worklistInfo) {
		this.worklistInfo = worklistInfo;
	}

	public VisitStatusEntity getLatestVisitStatus() {
		return this.latestVisitStatus;
	}

	public void setLatestVisitStatus(final VisitStatusEntity latestVisitStatus) {
		this.latestVisitStatus = latestVisitStatus;
	}

	public String getLatestVisitCode() {
		return this.latestVisitCode;
	}

	public void setLatestVisitCode(final String latestVisitCode) {
		this.latestVisitCode = latestVisitCode;
	}

	public SendStatusType getSendStatus() {
		return this.sendStatus;
	}

	public void setSendStatus(final SendStatusType sendStatus) {
		this.sendStatus = sendStatus;
	}

	public Date getSendStatusUpdateTime() { return sendStatusUpdateTime; }

	public void setSendStatusUpdateTime(Date sendStatusUpdateTime) { this.sendStatusUpdateTime = sendStatusUpdateTime; }

	public @Size(max = 1024) String getSendProcessMsg() { return sendProcessMsg; }

	public void setSendProcessMsg(@Size(max = 1024) String sendProcessMsg) { this.sendProcessMsg = sendProcessMsg; }

	public WorklistCreateType getCreateType() {
		return this.createType;
	}

	public void setCreateType(final WorklistCreateType createType) {
		this.createType = createType;
	}

	public Integer getOrder() {
		return this.order;
	}

	public void setOrder(final Integer order) {
		this.order = order;
	}

	public OffsetDateTime getStartDate() {
		return this.startDate;
	}

	public void setStartDate(final OffsetDateTime startDate) {
		this.startDate = startDate;
	}

	public OffsetDateTime getSaveDate() {
		return this.saveDate;
	}

	public void setSaveDate(final OffsetDateTime saveDate) {
		this.saveDate = saveDate;
	}

	public OffsetDateTime getFillDate() {
		return this.fillDate;
	}

	public void setFillDate(final OffsetDateTime fillDate) {
		this.fillDate = fillDate;
	}

	public Set<WorklistAssociationEntity> getWorklistAssociations() {
		return this.worklistAssociations;
	}

	public List<VisitStatusEntity> getVisitStatuses() {
		return this.visitStatuses;
	}

	public List<WorklistUserEntity> getWorklistUsers() {
		return this.worklistUsers;
	}

	public String getOspec() {
		return this.ospec;
	}

	public void setOspec(final String ospec) {
		this.ospec = ospec;
	}

	public NoteEntity getLatestNote() {
		return this.latestNote;
	}

	public void setLatestNote(final NoteEntity latestNote) {
		this.latestNote = latestNote;
	}

	public String getAddressType() {
		return this.addressType;
	}

	public void setAddressType(final String addressType) {
		this.addressType = addressType;
	}

	public String getSuccesfulPreviousVisit() {
		return this.succesfulPreviousVisit;
	}

	public void setSuccesfulPreviousVisit(final String succesfulPreviousVisit) {
		this.succesfulPreviousVisit = succesfulPreviousVisit;
	}

}
