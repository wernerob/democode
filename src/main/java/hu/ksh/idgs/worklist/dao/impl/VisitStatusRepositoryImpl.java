package hu.ksh.idgs.worklist.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.model.IdgsEntity_;
import hu.ksh.idgs.worklist.dao.VisitStatusRepository;
import hu.ksh.idgs.worklist.dto.VisitStatusSendDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;
import hu.ksh.idgs.worklist.model.HouseHoldEntity_;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;
import hu.ksh.idgs.worklist.model.VisitStatusEntity_;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity_;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity_;
import hu.ksh.idgs.worklist.model.WorklistUserEntity;
import hu.ksh.idgs.worklist.model.WorklistUserEntity_;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.helper.RoleHelper;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;

@Repository("visitStatusRepository")
public class VisitStatusRepositoryImpl extends AbstractRepositoryImpl<VisitStatusEntity>
		implements VisitStatusRepository {

	public VisitStatusRepositoryImpl() {
		super(VisitStatusEntity.class);
	}

	@Override
	public VisitStatusSendDto getNextSendableVisitStatus(final List<String> formTemplateIds) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<VisitStatusSendDto> criteriaQuery = cb.createQuery(VisitStatusSendDto.class);
		final Root<VisitStatusEntity> visitStatus = criteriaQuery.from(VisitStatusEntity.class);
		final Join<VisitStatusEntity, WorklistItemEntity> worklistItem = visitStatus
				.join(VisitStatusEntity_.worklistItem);
		final Join<WorklistItemEntity, WorklistUserEntity> worklistUser = worklistItem
				.join(WorklistItemEntity_.worklistUsers);
		worklistUser.on(cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), RoleHelper.ROLE_INTERVIEWER));

		criteriaQuery.multiselect(//
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistUser.get(WorklistUserEntity_.USER_ID), worklistUser.get(WorklistUserEntity_.USER_NAME));
		criteriaQuery.where(//
				cb.equal(visitStatus.get(VisitStatusEntity_.SEND_STATUS), SendStatusType.NOT_SENT), //
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID).in(formTemplateIds));

		criteriaQuery.orderBy(cb.asc(visitStatus.get(VisitStatusEntity_.TIME)));
		return getSingleResult(criteriaQuery);
	}

	@Override
	public List<VisitStatusSendItemDto> getSendableWorklistItems(final String formTemplateId, final String userId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<VisitStatusSendItemDto> criteriaQuery = cb.createQuery(VisitStatusSendItemDto.class);
		final Root<VisitStatusEntity> visitStatus = criteriaQuery.from(VisitStatusEntity.class);
		final Join<VisitStatusEntity, WorklistItemEntity> worklistItem = visitStatus
				.join(VisitStatusEntity_.worklistItem);
		final Join<WorklistItemEntity, WorklistUserEntity> worklistUser = worklistItem
				.join(WorklistItemEntity_.worklistUsers);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);

		criteriaQuery.multiselect(//
				getIdColumnFromJoin(worklistItem), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				houseHold.get(HouseHoldEntity_.INTERNAL_ID), //
				cb.selectCase(visitStatus.get(VisitStatusEntity_.TYPE))
						.when(VisitType.SUCCESS, worklistItem.get(WorklistItemEntity_.CLOSE_DATE))
						.otherwise(visitStatus.get(VisitStatusEntity_.TIME)), //
				visitStatus.get(VisitStatusEntity_.VISIT_CODE), //
				getIdColumnFromRoot(visitStatus), //
				visitStatus.get(VisitStatusEntity_.VISIT_STATUS_ID), //
				visitStatus.get(VisitStatusEntity_.TYPE), //
				visitStatus.get(VisitStatusEntity_.GEO_LOCATION_ID), //
				worklistItem.get(WorklistItemEntity_.NOTE), //
				worklistItem.get(WorklistItemEntity_.CREATE_TYPE)

		);

		criteriaQuery.where(//
				cb.equal(visitStatus.get(VisitStatusEntity_.sendStatus), SendStatusType.NOT_SENT), //
				cb.equal(worklistUser.get(WorklistUserEntity_.USER_ID), userId), //
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));
		criteriaQuery.orderBy(cb.asc(visitStatus.get(VisitStatusEntity_.TIME)));

		return getResultList(criteriaQuery);
	}

	@Override
	public List<VisitStatusEntity> getVisitStatuses(final List<IdgsEntityId> visitStatusIds) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<VisitStatusEntity> criteriaQuery = cb.createQuery(VisitStatusEntity.class);
		final Root<VisitStatusEntity> visitStatus = criteriaQuery.from(VisitStatusEntity.class);

		criteriaQuery.select(visitStatus);
		criteriaQuery.where(visitStatus.get(IdgsEntity_.ID).in(visitStatusIds));

		return getResultList(criteriaQuery);
	}

}
