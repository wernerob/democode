package hu.ksh.idgs.worklist.dao;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;

public interface WorklistAssociationRepository extends IdgsRepository<WorklistAssociationEntity> {

	IdgsEntityId findIdByExternalId(String externalId);

	long getNextAddressSequence();

}
