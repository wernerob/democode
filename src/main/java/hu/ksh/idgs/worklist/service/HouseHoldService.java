package hu.ksh.idgs.worklist.service;

import hu.ksh.maja.core.exception.ServiceException;

public interface HouseHoldService {

	boolean existsOnForm(String formTemplateId, String kshAddressId, String houseHoldId) throws ServiceException;

}
