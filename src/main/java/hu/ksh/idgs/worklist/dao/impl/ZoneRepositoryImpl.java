package hu.ksh.idgs.worklist.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import hu.ksh.idgs.core.dao.AbstractRepositoryImpl;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.ZoneRepository;
import hu.ksh.idgs.worklist.model.ZoneEntity;
import hu.ksh.idgs.worklist.model.ZoneEntity_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository("zoneRepository")
public class ZoneRepositoryImpl extends AbstractRepositoryImpl<ZoneEntity> implements ZoneRepository {

	public ZoneRepositoryImpl() {
		super(ZoneEntity.class);
	}

	@Override
	public IdgsEntityId findIdByExternalId(final String externalId, final String settlementCode, final String zoneSeq) {

		final CriteriaBuilder cb = getCriteriaBuilder();
		final CriteriaQuery<IdgsEntityId> criteriaQuery = cb.createQuery(IdgsEntityId.class);
		final Root<ZoneEntity> zone = criteriaQuery.from(ZoneEntity.class);

		final List<Predicate> predicates = new ArrayList<Predicate>();
		if (externalId != null && !externalId.isBlank()) {
			predicates.add(cb.equal(zone.get(ZoneEntity_.EXTERNAL_ID), externalId));
		}

		if (predicates.isEmpty() && settlementCode != null && zoneSeq != null) {
			predicates.add(cb.and(//
					cb.equal(zone.get(ZoneEntity_.SETTLEMENT_CODE), settlementCode),
					cb.equal(zone.get(ZoneEntity_.EXTERNAL_SEQ), zoneSeq)));
		}

		if (predicates.isEmpty()) {
			predicates.add(cb.or());
		}

		criteriaQuery.select(getIdColumnFromRoot(zone));
		criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));

		return getSingleResult(criteriaQuery);
	}

}
