package hu.ksh.idgs.worklist.dao.impl;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.worklist.dao.HouseHoldRepository;
import hu.ksh.idgs.worklist.model.AddressEntity;
import hu.ksh.idgs.worklist.model.AddressEntity_;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;
import hu.ksh.idgs.worklist.model.HouseHoldEntity_;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity_;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.ListJoin;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.SetJoin;

@Repository("houseHoldRepository")
public class HouseHoldRepositoryImpl extends AbstractRepositoryImpl<HouseHoldEntity> implements HouseHoldRepository {

	public HouseHoldRepositoryImpl() {
		super(HouseHoldEntity.class);
	}

	@Override
	public boolean existsHouseHold(final String externalAddressId, final String houseHoldId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		final Root<HouseHoldEntity> houseHold = criteriaQuery.from(HouseHoldEntity.class);
		final Join<HouseHoldEntity, AddressEntity> address = houseHold.join(HouseHoldEntity_.address);
		final Join<AddressEntity, WorklistAssociationEntity> worklistAssociation = address
				.join(AddressEntity_.worklistAssociation);

		criteriaQuery.select(cb.count(houseHold));
		criteriaQuery.where(//
				cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalAddressId), //
				cb.equal(houseHold.get(HouseHoldEntity_.INTERNAL_ID), houseHoldId));

		return isCountGreaterThenZero(criteriaQuery);
	}

	@Override
	public Integer getMaxHouseHold(final String externalAddressId, final String formTemplateId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<Integer> criteriaQuery = cb.createQuery(Integer.class);
		final Root<HouseHoldEntity> houseHold = criteriaQuery.from(HouseHoldEntity.class);
		final ListJoin<HouseHoldEntity, WorklistItemEntity> worklistItem = houseHold.join(HouseHoldEntity_.worklist);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklistItem
				.join(WorklistItemEntity_.worklistAssociations);

		criteriaQuery.select(cb.max(houseHold.get(HouseHoldEntity_.INTERNAL_ID).as(Integer.class)));
		criteriaQuery.where(//
				cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalAddressId),
				cb.equal(worklistItem.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId));
		return getSingleResult(criteriaQuery);
	}

	@Override
	public boolean existsOnForm(final String formTemplateId, final String externalAddressId, final String houseHoldId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		final Root<HouseHoldEntity> houseHold = criteriaQuery.from(HouseHoldEntity.class);
		final ListJoin<HouseHoldEntity, WorklistItemEntity> worklist = houseHold.join(HouseHoldEntity_.worklist);
		final SetJoin<WorklistItemEntity, WorklistAssociationEntity> worklistAssociation = worklist
				.join(WorklistItemEntity_.worklistAssociations);

		criteriaQuery.select(cb.count(houseHold));
		criteriaQuery.where(//
				cb.equal(worklist.get(WorklistItemEntity_.FORM_TEMPLATE_ID), formTemplateId), //
				cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalAddressId), //
				cb.equal(houseHold.get(HouseHoldEntity_.INTERNAL_ID), houseHoldId));

		return isCountGreaterThenZero(criteriaQuery);
	}

}
