package hu.ksh.idgs.worklist.dao;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;

public interface HouseHoldRepository extends IdgsRepository<HouseHoldEntity> {

	boolean existsHouseHold(String externalAddressId, String houseHoldId);

	Integer getMaxHouseHold(String externalAddressId, String formTemplateId);

	boolean existsOnForm(String formTemplateId, String externalAddressId, String houseHoldId);

}
