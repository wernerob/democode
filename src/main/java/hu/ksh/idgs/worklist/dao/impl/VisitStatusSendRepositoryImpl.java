package hu.ksh.idgs.worklist.dao.impl;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.worklist.dao.VisitStatusSendRepository;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;
import hu.ksh.idgs.worklist.model.VisitStatusSendEntity;
import hu.ksh.idgs.worklist.model.VisitStatusSendEntity_;

@Repository("visitStatusSendRepository")
public class VisitStatusSendRepositoryImpl extends AbstractRepositoryImpl<VisitStatusSendEntity>
		implements VisitStatusSendRepository {

	public VisitStatusSendRepositoryImpl() {
		super(VisitStatusSendEntity.class);
	}

	@Override
	public List<VisitStatusEntity> getVisitItems(final String packageId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<VisitStatusEntity> criteriaQuery = cb.createQuery(VisitStatusEntity.class);
		final Root<VisitStatusSendEntity> visitStatusSend = criteriaQuery.from(VisitStatusSendEntity.class);
		final Join<VisitStatusSendEntity, VisitStatusEntity> visitStatus = visitStatusSend
				.join(VisitStatusSendEntity_.visitStatus);

		criteriaQuery.select(visitStatus);
		criteriaQuery.where(cb.equal(visitStatusSend.get(VisitStatusSendEntity_.PACKAGE_ID), packageId));

		return getResultList(criteriaQuery);
	}

	@Override
	public boolean hasExistingPackageId(final String packageId) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<Long> criteriaQuery = cb.createQuery(Long.class);
		final Root<VisitStatusSendEntity> visitStatusSend = criteriaQuery.from(VisitStatusSendEntity.class);

		criteriaQuery.select(cb.count(visitStatusSend));
		criteriaQuery.where(cb.equal(visitStatusSend.get(VisitStatusSendEntity_.PACKAGE_ID), packageId));
		return isCountGreaterThenZero(criteriaQuery);
	}

}
