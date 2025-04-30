package hu.ksh.idgs.worklist.service.impl;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.core.value.FormTemplateSettingType;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dao.WorklistUserRepository;
import hu.ksh.idgs.worklist.dto.SaveOsapDto;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.repository.WorklistItemDataRepository;
import hu.ksh.idgs.worklist.service.WorklistItemService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplateSetting;
import hu.ksh.idgs.worklist.service.proxy.call.TokenAuthProxyCallReactivateUserToken;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.dto.GetWorkitemStatusDto;
import hu.ksh.maja.core.dto.SaveOsapResultDto;
import hu.ksh.maja.core.exception.PermissionDeniedException;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.helper.AddressHelper;
import hu.ksh.maja.core.helper.PermissionHelper;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;
import hu.ksh.maja.core.value.LoginIssuerValue;
import jakarta.persistence.PersistenceException;

@Service("worklistItemService")
public class WorklistItemServiceImpl implements WorklistItemService {

	@Lazy
	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Lazy
	@Autowired
	private WorklistItemDataRepository worklistItemDataRepository;

	@Lazy
	@Autowired
	private WorklistUserRepository worklistUserRepository;

	@Lazy
	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplateSetting formServiceProxyCallFormTemplateSetting;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Lazy
	@Autowired
	private TokenAuthProxyCallReactivateUserToken tokenAuthProxyCallReactivateUserToken;

	@TransactionRequired
	@Override
	public void updateWorklistItemFormStatus(final String authorization, final IdgsEntityId worklistItemId,
			final FormStatusType newStatus) throws ServiceException {

		updateWorklistItemFormStatus(authorization, worklistItemId, newStatus, true);
	}

	@TransactionRequired
	@Override
	public void updateWorklistItemFormStatus(final String authorization, final IdgsEntityId worklistItemId,
			final FormStatusType newStatus, final boolean needReactivateToken) throws ServiceException {

		if (worklistItemId == null) {
			throw new ServiceException("Worklist item id must be not null");
		}

		final Optional<WorklistItemEntity> worklistItemOptional = this.worklistItemRepository.findById(worklistItemId);
		if (worklistItemOptional.isEmpty()) {
			throw new ServiceException(
					String.format("Worklist item was not found with id: %s", worklistItemId.toString()));
		}

		final WorklistItemEntity worklistItem = worklistItemOptional.get();
		final FormStatusType oldStatus = worklistItem.getFormStatus();

		if (FormStatusType.CLOSED.equals(newStatus)) {
			worklistItem.setCloseDate(OffsetDateTime.now());
		} else {
			worklistItem.setCloseDate(null);
			worklistItem.setSendStatus(SendStatusType.NOT_SENT);
		}
		worklistItem.setFormStatus(newStatus);
		saveWorklistItem(worklistItem);

		if (needReactivateToken) {
			reActivateUserToken(oldStatus, newStatus, worklistItemId, worklistItem.getFormTemplateId(), authorization);
		}

	}

	private void reActivateUserToken(final FormStatusType oldStatus, final FormStatusType newStatus,
			final IdgsEntityId worklistItemId, final String formTemplateId, final String authorization)
			throws ServiceException {

		if (!isReopen(oldStatus, newStatus)) {
			return;
		}

		GetWorkitemStatusDto worklistItemStatus;
		try {
			worklistItemStatus = getWorkitemStatusData(worklistItemId);
		} catch (final PermissionDeniedException e) {
			throw new ServiceException(e.getMessage(), e);
		}
		if (worklistItemStatus == null) {
			throw new ServiceException(String.format("Unknown worklist id: %s", worklistItemId));
		}

		final FormTemplateDetailedInfoDto formTemplateInfo = this.formServiceProxyCallFormTemplate
				.getFormTemplateInfo(authorization, Language.getDefault(), formTemplateId, RoleHelper.ROLE_SELF_LOADER);

		if (formTemplateInfo == null) {
			throw new ServiceException(String.format("Missing formtemplate for id: %s", formTemplateId));
		}

		this.tokenAuthProxyCallReactivateUserToken.reactivate(authorization, formTemplateInfo.getCode(),
				formTemplateInfo.getYear(), formTemplateInfo.getPeriod(), worklistItemStatus.getExternalId(),
				worklistItemStatus.getHouseHold());

	}

	private boolean isReopen(final FormStatusType oldStatus, final FormStatusType newStatus) {

		return (FormStatusType.CLOSED.equals(oldStatus) || FormStatusType.FAILED.equals(oldStatus))
				&& FormStatusType.STARTED.equals(newStatus);
	}

	private WorklistItemEntity saveWorklistItem(final WorklistItemEntity worklistItem) throws ServiceException {

		try {
			return this.worklistItemRepository.save(worklistItem);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public String getFormTemplateId(final IdgsEntityId worklistItemId) {

		return this.worklistItemRepository.getFormTemplateId(worklistItemId);
	}

	@Override
	public FormStatusType getFormStatus(final IdgsEntityId worklistItemId) {

		return this.worklistItemRepository.getFormStatus(worklistItemId);
	}

	@Override
	public IdgsEntityId getWorklistId(final String externalAddressId, final String houseHoldId,
			final String formTemplateId) {

		return this.worklistItemRepository.getWorklistId(externalAddressId, houseHoldId, formTemplateId);
	}

	@Override
	public List<FormTemplateDetailedInfoDto> getFormTemplalatesForNewAddress(final String authorization)
			throws ServiceException {

		final List<FormTemplateDetailedInfoDto> formTemplates = new ArrayList<>();
		final String userId = this.majaAuthenticationService.getUserId();
		List<FormTemplateDetailedInfoDto> validFormTempaltes = this.formServiceProxyCallFormTemplate
				.getFormTemplates(authorization, Language.getDefault(), true, true);

		final String roleName = this.majaAuthenticationService.getRoleName();
		if (!RoleHelper.isAdmin(roleName) && !RoleHelper.isOsapAdmin(roleName)) {
			validFormTempaltes = validFormTempaltes.stream().filter(
					ftl -> this.worklistUserRepository.existsFormTemplateIdsForUser(userId, ftl.getId().toString()))
					.toList();

		}

		final String formTemplateEnableAddAddress = Boolean.TRUE.toString();

		for (final FormTemplateDetailedInfoDto validFormTempalte : validFormTempaltes) {

			final String formTemplateSettingValue = this.formServiceProxyCallFormTemplateSetting.getSettingValue(
					authorization, validFormTempalte.getId().toString(), FormTemplateSettingType.ENABLE_ADD_ADDRESS);
			if (formTemplateEnableAddAddress.equals(formTemplateSettingValue)) {
				formTemplates.add(validFormTempalte);
			}
		}

		return formTemplates;

	}

	@Override
	public String getWorklistUserId(final IdgsEntityId worklistId, final String roleName) throws ServiceException {

		try {
			return this.worklistUserRepository.getWorklistUserId(worklistId, roleName);
		} catch (final PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@TransactionRequired
	@Override
	public void resend(final String formTemplateId) throws ServiceException {
		try {
			this.worklistItemDataRepository.resetFormTemplateSendStatus(formTemplateId);
		} catch (final PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	@TransactionRequired
	@Override
	public SaveOsapResultDto saveOsap(final SaveOsapDto saveOsapData) throws ServiceException {

		final IdgsEntityId worklistId = saveOsapData.getWorklistId();

		try {

			final Optional<WorklistItemEntity> worklistOptional = this.worklistItemRepository.findById(worklistId);
			if (worklistOptional.isEmpty()) {
				throw new ServiceException(String.format("Worklistid was not found: %s", worklistId));
			}

			final WorklistItemEntity worklist = worklistOptional.get();

			if (worklist.getStartDate() == null) {
				worklist.setStartDate(saveOsapData.getStartDate());
			}

			worklist.setSaveDate(OffsetDateTime.now());
			worklist.setFillDate(saveOsapData.getFillDate());
			saveWorklistItem(worklist);

			final GetWorkitemStatusDto workItemStatus = this.worklistItemRepository
					.getWorkitemStatusData(worklist.getId());

			return createSaveOsapResult(worklist, workItemStatus);

		} catch (final PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private SaveOsapResultDto createSaveOsapResult(final WorklistItemEntity worklist,
			final GetWorkitemStatusDto workItemStatus) {

		final SaveOsapResultDto result = new SaveOsapResultDto();
		result.setFormStatus(worklist.getFormStatus());
		result.setFormTemplateId(worklist.getFormTemplateId());
		result.setKshAddressId(workItemStatus.getExternalId());
		result.setHouseHoldId(workItemStatus.getHouseHold());

		return result;
	}

	@Override
	public GetWorkitemStatusDto getWorkitemStatusData(final IdgsEntityId worklistId)
			throws ServiceException, PermissionDeniedException {

		try {

			final GetWorkitemStatusDto workItemStatus = this.worklistItemRepository.getWorkitemStatusData(worklistId);
			checkWorklistPermission(worklistId, workItemStatus.getExternalId(), workItemStatus.getHouseHold());
			return workItemStatus;
		} catch (final PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private void checkWorklistPermission(final IdgsEntityId worklistId, final String externalId,
			final String houseHoldId) throws PermissionDeniedException {

		if (LoginIssuerValue.TOKEN_AUTH.toString().equals(this.majaAuthenticationService.getIssuer())) {
			if (!this.majaAuthenticationService.getUserName()
					.equals(AddressHelper.getSelfLoaderUserName(externalId, houseHoldId))) {
				throw new PermissionDeniedException();
			}
			return;
		}

		final List<String> permissions = this.majaAuthenticationService.getPermissionCodes();
		if (!permissions.contains(PermissionHelper.MAJA_ADMIN_WORKLIST.getPermissionCode())) {
			final String userId = this.majaAuthenticationService.getUserId();
			if (!this.worklistUserRepository.existsWorklistUser(worklistId, userId)) {
				throw new PermissionDeniedException();
			}
		}

	}

	@Override
	public GetWorkitemStatusDto getWorkitemStatusData(final String formTemplateId, final String kshAddressId,
			final String houseHoldId) throws ServiceException, PermissionDeniedException {

		final IdgsEntityId worklistId = this.worklistItemRepository.getWorklistId(kshAddressId, houseHoldId,
				formTemplateId);
		if (worklistId == null) {
			throw new ServiceException(String.format("Worklist was not found (formtemplate: %s, addressid: %s, hh: %s)",
					formTemplateId, kshAddressId, houseHoldId));
		}

		return getWorkitemStatusData(worklistId);
	}

}
