package hu.ksh.idgs.worklist.service.impl;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hu.ksh.idgs.core.dto.FormTemplateVisitItemsDto;
import hu.ksh.idgs.core.dto.GeoLocationDto;
import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.core.value.FormTemplateSettingType;
import hu.ksh.idgs.worklist.dao.VisitStatusRepository;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dao.WorklistUserRepository;
import hu.ksh.idgs.worklist.dto.VisitInfoDto;
import hu.ksh.idgs.worklist.dto.VisitItemDto;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.service.VisitInfoService;
import hu.ksh.idgs.worklist.service.VisitSavingService;
import hu.ksh.idgs.worklist.service.WorklistItemService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplateSetting;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallGeoLocation;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallGetCodeTable;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallVisitFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.TokenAuthProxyCallCensusItem;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.dto.CensusItemDto;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;

@Service("visitSavingService")
public class VisitSavingServiceImpl implements VisitSavingService {

	@Lazy
	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Lazy
	@Autowired
	private VisitStatusRepository visitStatusRepository;

	@Lazy
	@Autowired
	private WorklistItemService worklistItemService;

	@Lazy
	@Autowired
	private FormServiceProxyCallGeoLocation formServiceProxyCallGeoLocation;

	@Lazy
	@Autowired
	private FormServiceProxyCallGetCodeTable formServiceProxyCallGetCodeTable;

	@Lazy
	@Autowired
	private FormServiceProxyCallVisitFormTemplate formServiceProxyCallVisitFormTemplate;

	@Lazy
	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	@Lazy
	@Autowired
	private WorklistUserRepository worklistUserRepository;

	@Lazy
	@Autowired
	private TokenAuthProxyCallCensusItem tokenAuthProxyCallCensusItem;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Lazy
	@Autowired
	private VisitInfoService visitInfoService;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplateSetting formServiceProxyCallFormTemplateSetting;

	@TransactionRequired
	@Override
	public void saveVisit(final String authorization, final IdgsEntityId worklistItemId,
			final VisitItemDto visitItemData) throws ServiceException {

		try {

			Assert.notNull(worklistItemId, "Worklist id must be not null");
			Assert.notNull(visitItemData, "Visit item must be not null");

			checkAccess(authorization, worklistItemId);
			final WorklistItemEntity worklistItem = getWorklistItem(worklistItemId);
			final String visitStatusCode = getVisitStatusCode(authorization, visitItemData.getVisitCodeTableItemId(),
					Language.getDefault());
			checVisitMinRequirement(authorization, visitStatusCode, worklistItem.getFormTemplateId(), worklistItemId);

			VisitStatusEntity visitStatus = createNewVisitStatus(authorization, worklistItem, visitStatusCode,
					visitItemData.getVisitCodeTableItemId(), visitItemData.getCodeTableItem(),
					visitItemData.getGeoLocation(), visitItemData.getVisitForm(), visitItemData.getVisitType());
			visitStatus = saveVisitStatus(visitStatus);
			worklistItem.setLatestVisitCode(visitStatusCode);
			updateWorklistItem(worklistItem);
			final VisitType visitStatusType = visitItemData.getVisitType();
			if (!VisitType.SUCCESS.equals(visitStatusType)) {
				handleFormStatus(authorization, worklistItemId, visitStatusType);
			}

		} catch (final IllegalArgumentException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private void checVisitMinRequirement(final String authorization, final String visitCode,
			final String formTemplateId, final IdgsEntityId worklistItemId) throws ServiceException {

		if (!"30".equals(visitCode) && !"31".equals(visitCode)) {
			return;
		}

		final String minVisitSetting = this.formServiceProxyCallFormTemplateSetting.getSettingValue(authorization,
				formTemplateId, FormTemplateSettingType.MIN_TMP_VISIT);
		if (minVisitSetting == null || "0".equals(minVisitSetting)) {
			return;
		}

		int minVisitSettingNum = 0;
		try {
			minVisitSettingNum = Integer.valueOf(minVisitSetting);
		} catch (final NumberFormatException e) {
			throw new ServiceException(String.format("Min tmp visit parameter for osap (%) is not a number: %s",
					formTemplateId, minVisitSetting));
		}

		final VisitInfoDto visitInfo = this.visitInfoService.getVisitInfo(authorization, worklistItemId,
				Language.getDefault());

		if (visitInfo == null) {
			throw new ServiceException(String.format("Missing visitinfo for worklist: %s", worklistItemId));
		}

		final List<CodeTableItemDto> tmpVisitItems = visitInfo.getTempVisitCodeTableItems();
		if (tmpVisitItems == null || tmpVisitItems.size() < minVisitSettingNum) {
			throw new ServiceException(
					String.format("Visit code %s does not meet min (%d) temp visits (formTemplate: %s, worklistId: %s)",
							visitCode, minVisitSettingNum, formTemplateId, worklistItemId));
		}

	}

	private WorklistItemEntity getWorklistItem(final IdgsEntityId worklistItemId) throws ServiceException {

		final Optional<WorklistItemEntity> worklistItemOptional = this.worklistItemRepository.findById(worklistItemId);
		if (worklistItemOptional.isEmpty()) {
			throw new ServiceException(
					String.format("Worklist item was not found with id %s", worklistItemId.toString()));
		}

		return worklistItemOptional.get();

	}

	private VisitStatusEntity createNewVisitStatus(final String authorization, final WorklistItemEntity worklistItem,
			final String visitCode, final String visitCodeTableItemId, final CodeTableItemDto codeTableItem,
			final GeoLocationDto geoLocation, final String visitForm, final VisitType visitType)
			throws ServiceException {

		final VisitType usedVisitType = codeTableItem == null ? visitType
				: getVisitTypeFromVisit(authorization, worklistItem.getId(), codeTableItem.getId());
		final VisitStatusEntity visitStatus = new VisitStatusEntity();
		visitStatus.setWorklistItem(worklistItem);
		visitStatus.setVisitStatusId(visitCodeTableItemId);
		visitStatus
				.setGeoLocationId(this.formServiceProxyCallGeoLocation.saveNewGeolocation(authorization, geoLocation));
		visitStatus.setTime(OffsetDateTime.now());
		visitStatus.setType(usedVisitType);
		visitStatus.setSendStatus(SendStatusType.NOT_SENT);
		visitStatus.setVisitForm(visitForm);
		visitStatus.setVisitCode(visitCode);

		return visitStatus;
	}

	private VisitType getVisitTypeFromVisit(final String authorization, final IdgsEntityId worklistId,
			final IdgsEntityId visitCodeTableItemId) throws ServiceException {

		final String formTempalteId = this.worklistItemRepository.getFormTemplateId(worklistId);
		final FormTemplateVisitItemsDto templateVisitCodeItems = this.formServiceProxyCallVisitFormTemplate
				.getVisitCodeItemsByFormTemlate(authorization, formTempalteId, Language.getDefault());

		if (findCodeTableItem(templateVisitCodeItems.getSuccessCodeTableItems(), visitCodeTableItemId).isPresent()) {
			return VisitType.SUCCESS;
		}
		if (findCodeTableItem(templateVisitCodeItems.getFinalCodeTableItems(), visitCodeTableItemId).isPresent()) {
			return VisitType.FINAL;
		}
		if (findCodeTableItem(templateVisitCodeItems.getTempCodeTableItems(), visitCodeTableItemId).isPresent()) {
			return VisitType.TEMP;
		}
		if (findCodeTableItem(templateVisitCodeItems.getAdminFinalCodeTableItems(), visitCodeTableItemId).isPresent()) {
			return VisitType.FINAL;
		}
		if (findCodeTableItem(templateVisitCodeItems.getOtherFinalCodeTableItems(), visitCodeTableItemId).isPresent()) {
			return VisitType.FINAL;
		}

		throw new ServiceException(String.format("Visit code table item id is not valid: %s", visitCodeTableItemId));

	}

	private Optional<CodeTableItemDto> findCodeTableItem(final List<CodeTableItemDto> codeTableItems,
			final IdgsEntityId codeTableItemId) {

		return codeTableItems.stream().filter(ci -> ci.getId().equals(codeTableItemId)).findFirst();
	}

	private VisitStatusEntity saveVisitStatus(final VisitStatusEntity visitStatus) throws ServiceException {

		try {
			return this.visitStatusRepository.save(visitStatus);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private WorklistItemEntity updateWorklistItem(final WorklistItemEntity worklistItem) throws ServiceException {

		try {
			return this.worklistItemRepository.update(worklistItem);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private void handleFormStatus(final String authorization, final IdgsEntityId worklistItemId,
			final VisitType visitStatus) throws ServiceException {

		if (VisitType.FINAL.equals(visitStatus)) {
			this.worklistItemService.updateWorklistItemFormStatus(authorization, worklistItemId, FormStatusType.FAILED,
					false);
			return;
		}

		this.worklistItemService.updateWorklistItemFormStatus(authorization, worklistItemId, FormStatusType.STARTED,
				false);

	}

	private String getVisitStatusCode(final String authorization, final String visitStatusCodeId,
			final Language language) throws ServiceException {

		if (visitStatusCodeId == null) {
			return null;
		}

		final CodeTableItemDto codeTableItemData = this.formServiceProxyCallGetCodeTable.getCodeTableItem(authorization,
				visitStatusCodeId, language);
		if (codeTableItemData == null) {
			return null;
		}
		return codeTableItemData.getCode();

	}

	private void checkAccess(final String authorization, final IdgsEntityId worklistItemId) throws ServiceException {

		final String roleName = this.majaAuthenticationService.getRoleName();
		final String userName = this.majaAuthenticationService.getUserName();

		if (RoleHelper.isAdmin(roleName) || RoleHelper.isOsapAdmin(roleName)) {
			return;
		}

		if (RoleHelper.isSelfLoader(roleName)) {

			final CensusItemDto censusItem = this.tokenAuthProxyCallCensusItem.getCensusItem(authorization);
			if (censusItem == null) {
				throw new ServiceException(
						String.format("User (%s) does not have access to worklist (%s) (selfloader missing censusinfo)",
								userName, worklistItemId));
			}

			final String formTemplateId = this.formServiceProxyCallFormTemplate.getFormTemplateId(
					censusItem.getOsapCode(), censusItem.getOsapYear(), censusItem.getOsapPeriod(), authorization);
			if (formTemplateId == null) {
				throw new ServiceException(String.format(
						"User (%s) does not have access to worklist (%s) (code: %s, year: %d, period: %s) (missing form)",
						userName, worklistItemId, censusItem.getOsapCode(), censusItem.getOsapYear(),
						censusItem.getOsapPeriod()));
			}

			final IdgsEntityId storedWorklistId = this.worklistItemService.getWorklistId(censusItem.getKshAddressId(),
					censusItem.getHouseHoldId(), formTemplateId);
			if (storedWorklistId == null) {
				throw new ServiceException(String.format(
						"User (%s) does not have access to worklist (%s) (code: %s, year: %d, period: %s) (missing worklist)",
						userName, worklistItemId, censusItem.getOsapCode(), censusItem.getOsapYear(),
						censusItem.getOsapPeriod()));
			}

			if (!storedWorklistId.equals(worklistItemId)) {
				throw new ServiceException(
						String.format("Current worklistId (%s) does not match with token related (%s) (user: %s) ",
								worklistItemId, storedWorklistId, userName));
			}

			return;

		}

		if (!this.worklistUserRepository.existsWorklistUser(worklistItemId,
				this.majaAuthenticationService.getUserId())) {
			throw new ServiceException(String.format("User (%s) does not have access to worklist (%s)",
					this.majaAuthenticationService.getUserName(), worklistItemId));
		}

	}

	@TransactionRequired
	@Override
	public void saveVisitWithCode(final String authorization, final IdgsEntityId worklistItemId,
			final String visitStatusCode, final VisitType visitType) throws ServiceException {

		try {

			// Temp disable check access.
			// OSAP 8054 saves visit code for osap 1711
//			checkAccess(authorization, worklistItemId);
			final WorklistItemEntity worklistItem = getWorklistItem(worklistItemId);

			checVisitMinRequirement(authorization, visitStatusCode, worklistItem.getFormTemplateId(), worklistItemId);

			VisitStatusEntity visitStatus = createNewVisitStatus(authorization, worklistItem, visitStatusCode, null,
					null, null, null, visitType);
			visitStatus = saveVisitStatus(visitStatus);
			worklistItem.setLatestVisitCode(visitStatusCode);
			updateWorklistItem(worklistItem);
			handleFormStatus(authorization, worklistItemId, visitType);

		} catch (final Throwable e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

}
