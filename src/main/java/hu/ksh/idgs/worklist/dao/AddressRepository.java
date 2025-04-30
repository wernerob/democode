package hu.ksh.idgs.worklist.dao;

import hu.ksh.idgs.core.dao.IdgsRepository;
import hu.ksh.idgs.worklist.model.AddressEntity;

public interface AddressRepository extends IdgsRepository<AddressEntity> {

	AddressEntity getAddressByExternalId(String externalAddressId);
}
