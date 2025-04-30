package hu.ksh.idgs.worklist.dao.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.core.dto.TupleIdValueDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.model.IdgsEntityId_;
import hu.ksh.idgs.core.model.IdgsEntity_;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dto.VisitItemDto;
import hu.ksh.idgs.worklist.dto.WorklistAdminItemDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedExportDto;
import hu.ksh.idgs.worklist.dto.WorklistSendItemDto;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatFillRateItemDto;
import hu.ksh.idgs.worklist.model.AddressEntity;
import hu.ksh.idgs.worklist.model.AddressEntity_;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;
import hu.ksh.idgs.worklist.model.HouseHoldEntity_;
import hu.ksh.idgs.worklist.model.NoteEntity;
import hu.ksh.idgs.worklist.model.NoteEntity_;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;
import hu.ksh.idgs.worklist.model.VisitStatusEntity_;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity_;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity_;
import hu.ksh.idgs.worklist.model.WorklistUserEntity;
import hu.ksh.idgs.worklist.model.WorklistUserEntity_;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.maja.core.dto.GetWorkitemStatusDto;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.model.MajaEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;

@Repository("worklistItemRepository")
public class WorklistItemRepositoryImpl extends AbstractRepositoryImpl<WorklistItemEntity>
		implements WorklistItemRepository {

	public WorklistItemRepositoryImpl() {
		super(WorklistItemEntity.class);
	}

	@Override
	public WorklistItemEntity getWorklistItemForHouseHold(final IdgsEntityId houseHoldId, final String formTemplateId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistItemEntity> criteriaQuery = cb.createQuery(WorklistItemEntity.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);

		criteriaQuery.select(worklistItem);
		criteriaQuery.where(//
				cb.equal(houseHold.get(IdgsEntity_.ID), houseHoldId), //
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public List<WorklistItemDetailedDto> getWorklistItems(final String userId, final List<String> formTemplateIds) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistItemDetailedDto> criteriaQuery = cb.createQuery(WorklistItemDetailedDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);
		final Join<HouseHoldEntity, AddressEntity> address = houseHold.join(HouseHoldEntity_.address);
		final ListJoin<WorklistItemEntity, WorklistUserEntity> worklistUser = worklistItem
				.join(WorklistItemEntity_.worklistUsers);
		worklistUser.on(//
				cb.equal(worklistUser.get(WorklistUserEntity_.USER_ID), userId), //
				cb.or(//
						cb.isNull(worklistUser.get(WorklistUserEntity_.VALID_FROM)), //
						cb.greaterThanOrEqualTo(cb.currentTimestamp(),
								worklistUser.get(WorklistUserEntity_.VALID_FROM))),
				cb.or(//
						cb.isNull(worklistUser.get(WorklistUserEntity_.VALID_TO)), //
						cb.lessThanOrEqualTo(cb.currentTimestamp(), worklistUser.get(WorklistUserEntity_.VALID_TO))));

		criteriaQuery.multiselect(//
				worklistItem.get(IdgsEntity_.ID), //
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				address.get(AddressEntity_.ZIP_CODE), //
				address.get(AddressEntity_.SETTLEMENT_CODE), //
				address.get(AddressEntity_.SETTLEMENT), //
				address.get(AddressEntity_.PLACE_NAME), //
				address.get(AddressEntity_.PLACE_TYPE), //
				address.get(AddressEntity_.HOUSE_NUMBER), //
				address.get(AddressEntity_.PARCEL_NUMBER), //
				address.get(AddressEntity_.BUILDING), //
				address.get(AddressEntity_.STAIRWAY), //
				address.get(AddressEntity_.FLOOR), //
				address.get(AddressEntity_.DOOR), //
				houseHold.get(HouseHoldEntity_.INTERNAL_ID), //
				worklistItem.get(WorklistItemEntity_.CLOSE_DATE), //
				worklistItem.get(WorklistItemEntity_.LATEST_VISIT_CODE), //
				worklistItem.get(WorklistItemEntity_.WORKLIST_INFO), //
				worklistItem.get(WorklistItemEntity_.OSPEC), //
				worklistUser.get(WorklistUserEntity_.VALID_FROM), //
				worklistUser.get(WorklistUserEntity_.VALID_TO), //
				worklistItem.get(WorklistItemEntity_.ADDRESS_TYPE),
				worklistItem.get(WorklistItemEntity_.SUCCESFUL_PREVIOUS_VISIT));

		criteriaQuery.where(//
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID).in(formTemplateIds));
		criteriaQuery.orderBy(//
				cb.asc(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID)), //
				cb.asc(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID)), //
				cb.asc(houseHold.get(HouseHoldEntity_.INTERNAL_ID)));

		return getResultList(criteriaQuery);

	}

	@Override
	public WorklistItemDetailedDto getWorklistItemForSelfLoader(final String kshAddressId, final String houseHoldId,
			final String formTemplateId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistItemDetailedDto> criteriaQuery = cb.createQuery(WorklistItemDetailedDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);
		final Join<HouseHoldEntity, AddressEntity> address = houseHold.join(HouseHoldEntity_.address);

		criteriaQuery.multiselect(//
				worklistItem.get(IdgsEntity_.ID), //
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				address.get(AddressEntity_.ZIP_CODE), //
				address.get(AddressEntity_.SETTLEMENT_CODE), //
				address.get(AddressEntity_.SETTLEMENT), //
				address.get(AddressEntity_.PLACE_NAME), //
				address.get(AddressEntity_.PLACE_TYPE), //
				address.get(AddressEntity_.HOUSE_NUMBER), //
				address.get(AddressEntity_.PARCEL_NUMBER), //
				address.get(AddressEntity_.BUILDING), //
				address.get(AddressEntity_.STAIRWAY), //
				address.get(AddressEntity_.FLOOR), //
				address.get(AddressEntity_.DOOR), //
				worklistItem.get(WorklistItemEntity_.ADDRESS_TYPE), //
				worklistItem.get(WorklistItemEntity_.SUCCESFUL_PREVIOUS_VISIT)//

		);
		criteriaQuery.where(//
				cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), kshAddressId), //
				cb.equal(houseHold.get(HouseHoldEntity_.INTERNAL_ID), houseHoldId), //
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public List<TupleIdValueDto> getNumberOfVisitStatus(final List<IdgsEntityId> worklistId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<TupleIdValueDto> criteriaQuery = cb.createQuery(TupleIdValueDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final ListJoin<WorklistItemEntity, VisitStatusEntity> visitStatus = worklistItem
				.join(WorklistItemEntity_.visitStatuses, JoinType.LEFT);

		criteriaQuery.multiselect(//
				getIdColumnFromRoot(worklistItem), //
				cb.count(visitStatus));
		criteriaQuery.where(worklistItem.get(IdgsEntity_.ID).in(worklistId));
		criteriaQuery.groupBy(getIdColumnFromRoot(worklistItem));
		return getResultList(criteriaQuery);
	}

	@Override
	public String getFormTemplateId(final IdgsEntityId worklistItemId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<String> criteriaQuery = cb.createQuery(String.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);

		criteriaQuery.select(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID));
		criteriaQuery.where(cb.equal(worklistItem.get(IdgsEntity_.ID), worklistItemId));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public List<VisitItemDto> getVisitItemsByWorklist(final IdgsEntityId worklistItemId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<VisitItemDto> criteriaQuery = cb.createQuery(VisitItemDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final ListJoin<WorklistItemEntity, VisitStatusEntity> visitStatus = worklistItem
				.join(WorklistItemEntity_.visitStatuses);

		criteriaQuery.multiselect(//
				visitStatus.get(VisitStatusEntity_.VISIT_STATUS_ID), //
				visitStatus.get(VisitStatusEntity_.VISIT_CODE), //
				visitStatus.get(VisitStatusEntity_.NOTE), //
				visitStatus.get(VisitStatusEntity_.TIME), //
				visitStatus.get(VisitStatusEntity_.VISIT_FORM), //
				visitStatus.get(VisitStatusEntity_.TYPE));

		criteriaQuery.where(//
				cb.equal(worklistItem.get(IdgsEntity_.ID), worklistItemId));
		criteriaQuery.orderBy(cb.desc(visitStatus.get(VisitStatusEntity_.TIME)));

		return getResultList(criteriaQuery);
	}

	@Override
	public FormStatusType getFormStatus(final IdgsEntityId worklistItemId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<FormStatusType> criteriaQuery = cb.createQuery(FormStatusType.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);

		criteriaQuery.select(worklistItem.get(WorklistItemEntity_.FORM_STATUS));
		criteriaQuery.where(//
				cb.equal(worklistItem.get(IdgsEntity_.ID), worklistItemId));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public IdgsEntityId getWorklistId(final String externalAddressId, final String houseHoldId,
			final String formTemplateId) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<IdgsEntityId> criteriaQuery = cb.createQuery(IdgsEntityId.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);

		criteriaQuery.select(worklistItem.get(IdgsEntity_.ID));

		criteriaQuery.where(//
				cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalAddressId),
				cb.equal(houseHold.get(HouseHoldEntity_.INTERNAL_ID), houseHoldId),
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));

		return getSingleResult(criteriaQuery);

	}

	@Override
	public List<WorklistSendItemDto> getWorklistSendItems(final String formTemplateId, final Integer maxResult) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistSendItemDto> criteriaQuery = cb.createQuery(WorklistSendItemDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);

		criteriaQuery.multiselect(//
				worklistItem.get(IdgsEntity_.ID), //
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				worklistItem.get(WorklistItemEntity_.LATEST_VISIT_CODE));

//		criteriaQuery.where(//
//				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId), //
//				cb.equal(worklistItem.get(WorklistItemEntity_.SEND_STATUS), SendStatusType.NOT_SENT), //
//				worklistItem.get(WorklistItemEntity_.FORM_STATUS).in(FormStatusType.CLOSED, FormStatusType.FAILED));

		// and
		Predicate p00 = worklistItem.get(WorklistItemEntity_.FORM_STATUS).in(FormStatusType.CLOSED, FormStatusType.FAILED);
		// and
		Predicate p11 = cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId);
		// and
		// (
		Predicate p12 = cb.equal(worklistItem.get(WorklistItemEntity_.SEND_STATUS), SendStatusType.NOT_SENT);
		// or (
		Predicate p21 = cb.equal(worklistItem.get(WorklistItemEntity_.SEND_STATUS), SendStatusType.PENDING);
		// and
		Date expectedBeLower = Date.from(Instant.now().minus(5, ChronoUnit.MINUTES));
		Predicate p22 = cb.lessThan(worklistItem.get(WorklistItemEntity_.SEND_STATUS_UPDATE_TIME), (Comparable)expectedBeLower);
		// ) or
		// ) and
		// PENDING and dateUpdated at least 5min old
		Predicate p2xCombined = cb.and(p21, p22);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.and(p00, p11, cb.or(p12, p2xCombined)));
		criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));

		return this.createQuery(criteriaQuery).setMaxResults(maxResult).getResultList();
	}

	@Override
	public List<WorklistAdminItemDto> getAdminWorklistItems(final String formTemplateId,
			final String externalAddressId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistAdminItemDto> criteriaQuery = cb.createQuery(WorklistAdminItemDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);
		final Join<HouseHoldEntity, AddressEntity> address = houseHold.join(HouseHoldEntity_.address);
		final ListJoin<WorklistItemEntity, WorklistUserEntity> worklistUser = worklistItem
				.join(WorklistItemEntity_.worklistUsers, JoinType.LEFT);
		worklistUser.on(cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), RoleHelper.ROLE_INTERVIEWER));
		final Join<WorklistItemEntity, NoteEntity> lastNote = worklistItem.join(WorklistItemEntity_.latestNote,
				JoinType.LEFT);
		lastNote.on(cb.equal(lastNote.get(NoteEntity_.WORKLIST_ITEM).get(MajaEntity_.ID),
				worklistItem.get(IdgsEntity_.ID)));

		criteriaQuery.multiselect(//
				worklistItem.get(IdgsEntity_.ID), //
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				address.get(AddressEntity_.ZIP_CODE), //
				address.get(AddressEntity_.SETTLEMENT_CODE), //
				address.get(AddressEntity_.SETTLEMENT), //
				address.get(AddressEntity_.PLACE_NAME), //
				address.get(AddressEntity_.PLACE_TYPE), //
				address.get(AddressEntity_.HOUSE_NUMBER), //
				address.get(AddressEntity_.PARCEL_NUMBER), //
				address.get(AddressEntity_.BUILDING), //
				address.get(AddressEntity_.STAIRWAY), //
				address.get(AddressEntity_.FLOOR), //
				address.get(AddressEntity_.DOOR), //
				houseHold.get(HouseHoldEntity_.INTERNAL_ID), //
				worklistUser.get(WorklistUserEntity_.USER_NAME), //
				worklistItem.get(WorklistItemEntity_.CLOSE_DATE), //
				worklistItem.get(WorklistItemEntity_.LATEST_VISIT_CODE), // //
				lastNote.get(NoteEntity_.NOTE), //
				worklistItem.get(WorklistItemEntity_.SUCCESFUL_PREVIOUS_VISIT),
				worklistItem.get(WorklistItemEntity_.SEND_STATUS)

		);

		final List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));
		if (externalAddressId != null && !externalAddressId.isBlank()) {
			predicates
					.add(cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalAddressId));
		}

		criteriaQuery.where(predicates.stream().toArray(Predicate[]::new));
		criteriaQuery.orderBy(//
				cb.asc(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID)), //
				cb.asc(houseHold.get(HouseHoldEntity_.INTERNAL_ID)));

		return getResultList(criteriaQuery);

	}

	@Override
	public List<WorklistAdminItemDto> getWorklistInfo(final List<IdgsEntityId> worklistIds) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistAdminItemDto> criteriaQuery = cb.createQuery(WorklistAdminItemDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);
		final Join<HouseHoldEntity, AddressEntity> address = houseHold.join(HouseHoldEntity_.address);
		final ListJoin<WorklistItemEntity, WorklistUserEntity> worklistUser = worklistItem
				.join(WorklistItemEntity_.worklistUsers, JoinType.LEFT);
		worklistUser.on(cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), RoleHelper.ROLE_INTERVIEWER));
		final Join<WorklistItemEntity, NoteEntity> note = worklistItem.join(WorklistItemEntity_.latestNote,
				JoinType.LEFT);

		criteriaQuery.multiselect(//
				worklistItem.get(IdgsEntity_.ID), //
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				address.get(AddressEntity_.ZIP_CODE), //
				address.get(AddressEntity_.SETTLEMENT_CODE), //
				address.get(AddressEntity_.SETTLEMENT), //
				address.get(AddressEntity_.PLACE_NAME), //
				address.get(AddressEntity_.PLACE_TYPE), //
				address.get(AddressEntity_.HOUSE_NUMBER), //
				address.get(AddressEntity_.PARCEL_NUMBER), //
				address.get(AddressEntity_.BUILDING), //
				address.get(AddressEntity_.STAIRWAY), //
				address.get(AddressEntity_.FLOOR), //
				address.get(AddressEntity_.DOOR), //
				houseHold.get(HouseHoldEntity_.INTERNAL_ID), //
				worklistUser.get(WorklistUserEntity_.USER_NAME), //
				worklistItem.get(WorklistItemEntity_.CLOSE_DATE), //
				worklistItem.get(WorklistItemEntity_.LATEST_VISIT_CODE), //
				note.get(NoteEntity_.NOTE), //
				worklistItem.get(WorklistItemEntity_.SUCCESFUL_PREVIOUS_VISIT),
				worklistItem.get(WorklistItemEntity_.SEND_STATUS)
		);

		criteriaQuery.where(//
				worklistItem.get(IdgsEntityId_.ID).in(worklistIds));
		criteriaQuery.orderBy(//
				cb.asc(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID)), //
				cb.asc(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID)));

		return getResultList(criteriaQuery);
	}

	@Override
	public List<WorklistStatFillRateItemDto> getFillRateStat(final String formTemplateId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistStatFillRateItemDto> criteriaQuery = cb
				.createQuery(WorklistStatFillRateItemDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);

		criteriaQuery.multiselect(//
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				cb.count(worklistItem));
		criteriaQuery.where(cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));
		criteriaQuery.groupBy(worklistItem.get(WorklistItemEntity_.FORM_STATUS));

		return getResultList(criteriaQuery);
	}

	@Override
	public List<WorklistItemEntity> getWorklistItemsByExternalId(final List<String> externalAddressIds,
			final String formTemplateId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistItemEntity> criteriaQuery = cb.createQuery(WorklistItemEntity.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);

		criteriaQuery.select(worklistItem);
		criteriaQuery.where(//
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID).in(externalAddressIds), //
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));

		return getResultList(criteriaQuery);
	}

	@Deprecated
	@Override
	public void resend(final String formTemplateId) {
		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaUpdate<WorklistItemEntity> criteriaUpdate = cb.createCriteriaUpdate(WorklistItemEntity.class);
		final Root<WorklistItemEntity> worklistItem = criteriaUpdate.from(WorklistItemEntity.class);

		criteriaUpdate.set(WorklistItemEntity_.SEND_STATUS, SendStatusType.NOT_SENT);
		criteriaUpdate.where(//
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId), //
				cb.equal(worklistItem.get(WorklistItemEntity_.SEND_STATUS), SendStatusType.SENT));
		executeUpdate(criteriaUpdate);
	}

	@Override
	public GetWorkitemStatusDto getWorkitemStatusData(final IdgsEntityId worklistId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<GetWorkitemStatusDto> criteriaQuery = cb.createQuery(GetWorkitemStatusDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final Join<WorklistItemEntity, HouseHoldEntity> houseHold = worklistItem.join(WorklistItemEntity_.houseHold);
		final Join<HouseHoldEntity, AddressEntity> address = houseHold.join(HouseHoldEntity_.address);
		final Join<AddressEntity, WorklistAssociationEntity> worklistAssociation = address
				.join(AddressEntity_.worklistAssociation);

		criteriaQuery.multiselect(//
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				worklistItem.get(WorklistItemEntity_.START_DATE), //
				worklistItem.get(WorklistItemEntity_.SAVE_DATE), //
				worklistItem.get(WorklistItemEntity_.FILL_DATE), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				houseHold.get(HouseHoldEntity_.INTERNAL_ID), //
				worklistItem.get(IdgsEntity_.ID));

		criteriaQuery.where(//
				cb.equal(worklistItem.get(IdgsEntity_.ID), worklistId));

		return getSingleResult(criteriaQuery);
	}

	@Override
	public List<WorklistItemDetailedExportDto> getWorklistItemsForExport(final List<IdgsEntityId> worklistIds) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<WorklistItemDetailedExportDto> criteriaQuery = cb
				.createQuery(WorklistItemDetailedExportDto.class);
		final Root<WorklistItemEntity> worklistItem = criteriaQuery.from(WorklistItemEntity.class);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);
		final Join<WorklistItemEntity, HouseHoldEntity> household = worklistItem.join(WorklistItemEntity_.houseHold);
		final Join<HouseHoldEntity, AddressEntity> address = household.join(HouseHoldEntity_.address);
		final ListJoin<WorklistItemEntity, WorklistUserEntity> worklistUser = worklistItem
				.join(WorklistItemEntity_.worklistUsers, JoinType.LEFT);
		worklistUser.on(cb.equal(worklistUser.get(WorklistUserEntity_.ROLE_NAME), RoleHelper.ROLE_INTERVIEWER));

		criteriaQuery.multiselect(//
				worklistItem.get(IdgsEntity_.id), //
				worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), //
				worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), //
				household.get(HouseHoldEntity_.INTERNAL_ID), //
				address.get(AddressEntity_.ZIP_CODE), //
				address.get(AddressEntity_.SETTLEMENT_CODE), //
				address.get(AddressEntity_.SETTLEMENT), //
				address.get(AddressEntity_.PLACE_NAME), //
				address.get(AddressEntity_.PLACE_TYPE), //
				address.get(AddressEntity_.HOUSE_NUMBER), //
				address.get(AddressEntity_.PARCEL_NUMBER), //
				address.get(AddressEntity_.BUILDING), //
				address.get(AddressEntity_.STAIRWAY), //
				address.get(AddressEntity_.FLOOR), //
				address.get(AddressEntity_.DOOR), //
				worklistItem.get(WorklistItemEntity_.FORM_STATUS), //
				worklistItem.get(WorklistItemEntity_.LATEST_VISIT_CODE), //
				worklistUser.get(WorklistUserEntity_.USER_NAME), //
				worklistItem.get(WorklistItemEntity_.START_DATE), //
				worklistItem.get(WorklistItemEntity_.FILL_DATE), //
				worklistItem.get(WorklistItemEntity_.SAVE_DATE), //
				worklistItem.get(WorklistItemEntity_.CLOSE_DATE), //
				worklistItem.get(WorklistItemEntity_.WORKLIST_INFO), //
				worklistItem.get(WorklistItemEntity_.OSPEC));

		criteriaQuery.where(//
				worklistItem.get(IdgsEntityId_.ID).in(worklistIds));
		criteriaQuery.orderBy(//
				cb.asc(worklistItem.get(WorklistItemEntity_.FILL_DATE)));

		return getResultList(criteriaQuery);
	}

}
