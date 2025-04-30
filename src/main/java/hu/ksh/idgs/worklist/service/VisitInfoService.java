package hu.ksh.idgs.worklist.service;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.VisitInfoDto;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.Language;

public interface VisitInfoService {

	VisitInfoDto getVisitInfo(String authorization, IdgsEntityId worklistItemId, Language language)
			throws ServiceException;

}
