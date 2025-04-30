package hu.ksh.idgs.worklist.dao;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.model.ZoneEntity;

public interface ZoneRepository extends IdgsRepository<ZoneEntity> {

	IdgsEntityId findIdByExternalId(String externalId, String settlementCode, String zoneSeq);

}
