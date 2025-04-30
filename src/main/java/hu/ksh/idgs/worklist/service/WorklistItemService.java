package hu.ksh.idgs.worklist.service;

import java.util.List;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.dto.SaveOsapDto;
import hu.ksh.maja.core.dto.GetWorkitemStatusDto;
import hu.ksh.maja.core.dto.SaveOsapResultDto;
import hu.ksh.maja.core.exception.PermissionDeniedException;
import hu.ksh.maja.core.exception.ServiceException;

public interface WorklistItemService {

	void updateWorklistItemFormStatus(String authorization, IdgsEntityId worklistItemId, FormStatusType newStatus)
			throws ServiceException;

	void updateWorklistItemFormStatus(String authorization, IdgsEntityId worklistItemId, FormStatusType newStatus,
			boolean needReactivateToken) throws ServiceException;

	String getFormTemplateId(IdgsEntityId worklistItemId);

	FormStatusType getFormStatus(IdgsEntityId worklistItemId);

	IdgsEntityId getWorklistId(final String externalAddressId, final String houseHoldId, final String formTemplateId);

	List<FormTemplateDetailedInfoDto> getFormTemplalatesForNewAddress(final String authorization)
			throws ServiceException;

	String getWorklistUserId(final IdgsEntityId worklistId, final String roleName) throws ServiceException;

	void resend(final String formTemplateId) throws ServiceException;

	SaveOsapResultDto saveOsap(SaveOsapDto saveOsapData) throws ServiceException;

	GetWorkitemStatusDto getWorkitemStatusData(final IdgsEntityId worklistId)
			throws ServiceException, PermissionDeniedException;

	GetWorkitemStatusDto getWorkitemStatusData(String formTemplateId, String kshAddressId, String houseHoldId)
			throws ServiceException, PermissionDeniedException;

}
