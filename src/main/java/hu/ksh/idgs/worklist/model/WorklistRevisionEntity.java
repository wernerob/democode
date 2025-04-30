package hu.ksh.idgs.worklist.model;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Entity
@Table(name = "revinfo")
@RevisionEntity(WorklistRevisionListener.class)
public class WorklistRevisionEntity implements Serializable {

	private static final long serialVersionUID = -5610056438273525156L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@RevisionNumber
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@RevisionTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "change_time", nullable = false)
	private Date changeTime;

	@Column(name = "user_id")
	private String userId;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Date getChangeTime() {
		return this.changeTime;
	}

	public void setChangeTime(final Date changeTime) {
		this.changeTime = changeTime;
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

}
