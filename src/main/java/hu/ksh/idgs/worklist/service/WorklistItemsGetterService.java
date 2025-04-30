package hu.ksh.idgs.worklist.service;

import java.util.List;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dto.WorklistAdminItemDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.Language;

public interface WorklistItemsGetterService {

	List<WorklistItemDetailedDto> getWorklistItems(String authorization, Language language) throws ServiceException;

	WorklistItemDetailedDto getWorklistItemForSelfLoader(String authorization, Language language)
			throws ServiceException;

	List<WorklistAdminItemDto> getWorklistAdminItems(final String authorization, String formTemplateId, String search,
			Language language) throws ServiceException;

	List<WorklistAdminItemDto> getWorkItemInfo(final String authorization, final List<IdgsEntityId> worklistIds,
			final Language language) throws ServiceException;
}
