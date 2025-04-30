package hu.ksh.idgs.worklist.dao.impl;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.worklist.dao.AddressRepository;
import hu.ksh.idgs.worklist.model.AddressEntity;
import hu.ksh.idgs.worklist.model.AddressEntity_;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity_;

@Repository("addressRepository")
public class AddressRepositoryImpl extends AbstractRepositoryImpl<AddressEntity> implements AddressRepository {

	public AddressRepositoryImpl() {
		super(AddressEntity.class);
	}

	@Override
	public AddressEntity getAddressByExternalId(final String externalAddressId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<AddressEntity> criteriaQuery = cb.createQuery(AddressEntity.class);
		final Root<AddressEntity> address = criteriaQuery.from(AddressEntity.class);
		final Join<AddressEntity, WorklistAssociationEntity> worklistAssociation = address
				.join(AddressEntity_.worklistAssociation);

		criteriaQuery.select(address);
		criteriaQuery
				.where(cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalAddressId));

		return getSingleResult(criteriaQuery);
	}

}
