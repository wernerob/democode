package hu.ksh.idgs.worklist.service;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitItemDto;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;

public interface VisitSavingService {

	void saveVisit(String authorization, IdgsEntityId worklistItemId, VisitItemDto visitItemData)
			throws ServiceException;

	void saveVisitWithCode(String authorization, IdgsEntityId worklistItemId, final String visitStatusCode,
			VisitType visitType) throws ServiceException;

}
