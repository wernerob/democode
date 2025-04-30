package hu.ksh.idgs.worklist.service;

import java.util.List;

import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.maja.core.exception.ServiceException;

public interface VisitStatusSendItems {

	List<VisitStatusSendItemDto> getItems(final String authorization, List<VisitStatusSendItemDto> items)
			throws ServiceException;

}
