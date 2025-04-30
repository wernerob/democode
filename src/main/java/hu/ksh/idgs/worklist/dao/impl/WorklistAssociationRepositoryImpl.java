package hu.ksh.idgs.worklist.dao.impl;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.WorklistAssociationRepository;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity_;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Repository("worklistAssociationRepository")
public class WorklistAssociationRepositoryImpl extends AbstractRepositoryImpl<WorklistAssociationEntity>
		implements WorklistAssociationRepository {

	public WorklistAssociationRepositoryImpl() {
		super(WorklistAssociationEntity.class);
	}

	@Override
	public IdgsEntityId findIdByExternalId(final String externalId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<IdgsEntityId> criteriaQuery = cb.createQuery(IdgsEntityId.class);
		final Root<WorklistAssociationEntity> worklistAssociation = criteriaQuery.from(WorklistAssociationEntity.class);

		criteriaQuery.select(getIdColumnFromRoot(worklistAssociation));
		criteriaQuery.where(cb.equal(worklistAssociation.get(WorklistAssociationEntity_.EXTERNAL_ID), externalId));

		return getSingleResult(criteriaQuery);
	}

	/**
	 * This is a database vendor specific
	 */
	@Override
	public long getNextAddressSequence() {

		final Query query = createNativeQuery("SELECT nextval('{h-schema}address_seq')");
		final Long seq = getSingleResult(query);
		return seq.longValue();
	}

}
