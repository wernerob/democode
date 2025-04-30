package hu.ksh.idgs.worklist.service;

import hu.ksh.idgs.worklist.dto.AddressAddDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface AddressAddService {

	WorklistItemDetailedDto saveAddress(AddressAddDto addressAddData) throws ServiceException;

}
