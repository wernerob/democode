package hu.ksh.idgs.worklist.dao.impl;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.model.IdgsEntity_;
import hu.ksh.idgs.worklist.dao.WorklistUserRepository;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity_;
import hu.ksh.idgs.worklist.model.WorklistUserEntity;
import hu.ksh.idgs.worklist.model.WorklistUserEntity_;

@Repository("worklistUserRepository")
public class WorklistUserRepositoryImpl extends AbstractRepositoryImpl<WorklistUserEntity>
		implements WorklistUserRepository {

	public WorklistUserRepositoryImpl() {
		super(WorklistUserEntity.class);
	}

	@Override
	public WorklistUserEntity getWorklistUserForHouseHold(final String formTemplateId, final IdgsEntityId houseHoldId,
			final String roleName) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistUserEntity> criteriaQuery = cb.createQuery(WorklistUserEntity.class);
		final Root<WorklistUserEntity> worklistUser = criteriaQuery.from(WorklistUserEntity.class);
		final Join<WorklistUserEntity, WorklistItemEntity> worklistItem = worklistUser
				.join(WorklistUserEntity_.worklistItem);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);

		criteriaQuery.select(worklistUser);
		criteriaQuery.where(//
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId), //
				cb.equal(houseHold.get(IdgsEntity_.ID), houseHoldId), //
				cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), roleName));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public String getWorklistUserId(final IdgsEntityId worklistId, final String roleName) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<String> criteriaQuery = cb.createQuery(String.class);
		final Root<WorklistUserEntity> worklistUser = criteriaQuery.from(WorklistUserEntity.class);
		final Join<WorklistUserEntity, WorklistItemEntity> worklistItem = worklistUser
				.join(WorklistUserEntity_.worklistItem);

		criteriaQuery.select(worklistUser.get(WorklistUserEntity_.USER_ID));
		criteriaQuery.where(//
				cb.equal(worklistItem.get(IdgsEntity_.ID), worklistId), //
				cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), roleName));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public WorklistUserEntity getWorklistUser(final IdgsEntityId worklistId, final String roleName) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistUserEntity> criteriaQuery = cb.createQuery(WorklistUserEntity.class);
		final Root<WorklistUserEntity> worklistUser = criteriaQuery.from(WorklistUserEntity.class);
		final Join<WorklistUserEntity, WorklistItemEntity> worklistItem = worklistUser
				.join(WorklistUserEntity_.worklistItem);

		criteriaQuery.select(worklistUser);
		criteriaQuery.where(//
				cb.equal(worklistItem.get(IdgsEntity_.ID), worklistId), //
				cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), roleName));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public boolean existsWorklistUser(final IdgsEntityId worklistId, final String userId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		final Root<WorklistUserEntity> worklistUser = criteriaQuery.from(WorklistUserEntity.class);
		final Join<WorklistUserEntity, WorklistItemEntity> worklistItem = worklistUser
				.join(WorklistUserEntity_.worklistItem);

		criteriaQuery.select(cb.count(worklistUser));
		criteriaQuery.where(//
				cb.equal(worklistItem.get(IdgsEntity_.ID), worklistId), //
				cb.equal(worklistUser.get(WorklistUserEntity_.USER_ID), userId));

		return isCountGreaterThenZero(criteriaQuery);
	}

	@Override
	public List<WorklistUserEntity> getWorklistUsersForFormTemplateRoleName(final String formTemplateId,
			final String userRoleName) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistUserEntity> criteriaQuery = cb.createQuery(WorklistUserEntity.class);
		final Root<WorklistUserEntity> worklistUser = criteriaQuery.from(WorklistUserEntity.class);
		final Join<WorklistUserEntity, WorklistItemEntity> worklistItem = worklistUser
				.join(WorklistUserEntity_.worklistItem);

		criteriaQuery.select(worklistUser);
		criteriaQuery.where(//
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId), //
				cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), userRoleName));

		return getResultList(criteriaQuery);
	}

	@Override
	public boolean existsFormTemplateIdsForUser(final String userId, final String formTemplateId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		final Root<WorklistUserEntity> worklistUser = criteriaQuery.from(WorklistUserEntity.class);

		criteriaQuery.select(cb.count(worklistUser));
		criteriaQuery.where(//
				cb.equal(worklistUser.get(WorklistUserEntity_.USER_ID), userId),
				cb.equal(worklistUser.get(WorklistUserEntity_.WORKLIST_ITEM).get(WorklistItemEntity_.FORM_TEMPLATE_ID),
						formTemplateId));

		return isCountGreaterThenZero(criteriaQuery);
	}

}
