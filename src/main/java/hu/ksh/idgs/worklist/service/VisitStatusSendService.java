package hu.ksh.idgs.worklist.service;

import hu.ksh.idgs.worklist.dto.SendStatus;
import hu.ksh.idgs.worklist.dto.VisitStatusSendDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface VisitStatusSendService {

	VisitStatusSendDto send(String authorization, String packageId) throws ServiceException;

	void updateSendStatus(String packageId, SendStatus sendStatus) throws ServiceException;

}
