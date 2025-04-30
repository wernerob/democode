package hu.ksh.idgs.worklist.service.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.NoteRepository;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dto.WorklistAdminItemDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.service.WorklistItemsGetterService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.TokenAuthProxyCallCensusItem;
import hu.ksh.maja.core.dto.CensusItemDto;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;
import jakarta.persistence.PersistenceException;

@Service("worklistItemsGetterService")
public class WorklistItemsGetterServiceImpl implements WorklistItemsGetterService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorklistItemsGetterServiceImpl.class);

	@Lazy
	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Lazy
	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	@Lazy
	@Autowired
	private TokenAuthProxyCallCensusItem tokenAuthProxyCallCensusItem;

	@Lazy
	@Autowired
	private NoteRepository noteRepository;

	@Override
	public List<WorklistItemDetailedDto> getWorklistItems(final String authorization, final Language language)
			throws ServiceException {

		try {

			final List<FormTemplateDetailedInfoDto> formTemplates = this.formServiceProxyCallFormTemplate
					.getFormTemplates(authorization, language, true, true);
			if (formTemplates.isEmpty()) {
				return Collections.emptyList();
			}
			final List<String> formTemplateIds = formTemplates.stream()
					.map(formTemplate -> formTemplate.getId().toString()).toList();
			final Map<String, FormTemplateDetailedInfoDto> formTemplateMap = getFormTemplateMap(formTemplates);
			final String userId = this.majaAuthenticationService.getUserId();
			final List<WorklistItemDetailedDto> worklistItems = this.worklistItemRepository.getWorklistItems(userId,
					formTemplateIds);
			for (final WorklistItemDetailedDto worklistItem : worklistItems) {
				final FormTemplateDetailedInfoDto formTemplateInfo = formTemplateMap
						.get(worklistItem.getFormTemplateId());
				if (formTemplateInfo == null) {
					continue;
				}
				worklistItem.setWorklistNote(this.noteRepository.getNotes(worklistItem.getWorklistItemId()));
				worklistItem.setFormTemplateInfo(formTemplateInfo);
				if (worklistItem.getCensusStartDate() == null) {
					worklistItem.setCensusStartDate(formTemplateInfo.getCensusStartDate());
				}
				if (worklistItem.getCensusEndDate() == null) {
					worklistItem.setCensusEndDate(formTemplateInfo.getCensusEndDate());
				}
				worklistItem.setAddressType(worklistItem.getAddressType());
				worklistItem.setSuccessfulPreviousVisit(worklistItem.getSuccessfulPreviousVisit());
			}

			return worklistItems;
		} catch (final PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private Map<String, FormTemplateDetailedInfoDto> getFormTemplateInfoMap(final Set<String> distinctFormTemplateIds,
			final String authorization, final Language language) throws ServiceException {

		final Map<String, FormTemplateDetailedInfoDto> formTemplateInfoMap = new HashMap<>();
		for (final String formTemplateId : distinctFormTemplateIds) {
			final FormTemplateDetailedInfoDto formTemplateInfo = this.formServiceProxyCallFormTemplate
					.getFormTemplateInfo(authorization, language, formTemplateId);
			if (formTemplateInfo == null) {
				WorklistItemsGetterServiceImpl.LOGGER.error("Form teplate information was not found for id {}",
						formTemplateId);
				continue;
			}
			formTemplateInfoMap.put(formTemplateId, formTemplateInfo);

		}

		return formTemplateInfoMap;
	}

	@Override
	public WorklistItemDetailedDto getWorklistItemForSelfLoader(final String authorization, final Language language)
			throws ServiceException {

		final CensusItemDto censusItem = this.tokenAuthProxyCallCensusItem.getCensusItem(authorization);
		if (censusItem == null) {
			return null;
		}

		final FormTemplateDetailedInfoDto templateInfo = this.formServiceProxyCallFormTemplate
				.getFormTemplatePeriodInfo(authorization, censusItem.getOsapCode(), censusItem.getOsapYear(),
						censusItem.getOsapPeriod(), language, RoleHelper.ROLE_SELF_LOADER);
		if (templateInfo == null) {
			return null;
		}

		final WorklistItemDetailedDto result = this.worklistItemRepository.getWorklistItemForSelfLoader(
				censusItem.getKshAddressId(), censusItem.getHouseHoldId(), templateInfo.getId().toString());

		if (result == null) {
			return null;
		}

		result.setOsapCode(censusItem.getOsapCode());
		result.setOsapYear(censusItem.getOsapYear());
		result.setOsapPeriod(censusItem.getOsapPeriod());

		return result;

	}

	@Override
	public List<WorklistAdminItemDto> getWorklistAdminItems(final String authorization, final String formTemplateId,
			final String search, final Language language) throws ServiceException {

		if (List.of("f7403ba5-d17f-4fc4-9e1b-637cfef600e1", "fabddb6b-c5e2-47bf-bab3-880c6b1827f1")
				.contains(formTemplateId) && (search == null || search.isBlank())) {
			throw new ServiceException("Without search parameter AC2020 cannot be listed");
		}

		final FormTemplateDetailedInfoDto formTemplateInfo = getFormTemplateInfo(authorization, formTemplateId);

		final List<WorklistAdminItemDto> worklistItems = this.worklistItemRepository
				.getAdminWorklistItems(formTemplateId, search);

		for (final WorklistAdminItemDto worklistItem : worklistItems) {

			worklistItem.setFormTemplateInfo(formTemplateInfo);
			worklistItem.setCensusStartDate(formTemplateInfo.getCensusStartDate());
			worklistItem.setCensusEndDate(formTemplateInfo.getCensusEndDate());
			worklistItem.setSuccesfulPreviousVisit(worklistItem.getSuccesfulPreviousVisit()); //TODO is this need here ?
		}

		return worklistItems;
	}

	private FormTemplateDetailedInfoDto getFormTemplateInfo(final String authorization, final String formTemplateId)
			throws ServiceException {

		final List<FormTemplateDetailedInfoDto> formTemplates = this.formServiceProxyCallFormTemplate
				.getFormTemplates(authorization, Language.getDefault(), null, null);

		return formTemplates.stream().filter(ft -> ft.getId().toString().equals(formTemplateId)).findFirst()
				.orElseThrow(() -> new ServiceException(String.format("Invalid form template id: %s", formTemplateId)));

	}

	@Override
	public List<WorklistAdminItemDto> getWorkItemInfo(final String authorization, final List<IdgsEntityId> worklistIds,
			final Language language) throws ServiceException {

		final List<WorklistAdminItemDto> worklistItems = this.worklistItemRepository.getWorklistInfo(worklistIds);

		final Set<String> distinctFormTemplateIds = worklistItems.stream()
				.map(worklistItem -> worklistItem.getFormTemplateId()).collect(Collectors.toSet());
		final Map<String, FormTemplateDetailedInfoDto> formTemplateInfoMap = getFormTemplateInfoMap(
				distinctFormTemplateIds, authorization, language);

		for (final WorklistAdminItemDto worklistItem : worklistItems) {
			final FormTemplateDetailedInfoDto formTemplateInfo = formTemplateInfoMap
					.get(worklistItem.getFormTemplateId());
			if (formTemplateInfo == null) {
				continue;
			}

			worklistItem.setFormTemplateInfo(formTemplateInfo);
			worklistItem.setCensusStartDate(formTemplateInfo.getCensusStartDate());
			worklistItem.setCensusEndDate(formTemplateInfo.getCensusEndDate());

		}

		return worklistItems;
	}

	private Map<String, FormTemplateDetailedInfoDto> getFormTemplateMap(
			final List<FormTemplateDetailedInfoDto> formTemplates) {

		final Map<String, FormTemplateDetailedInfoDto> formTemplateMap = new HashMap<>();

		for (final FormTemplateDetailedInfoDto formTemplate : formTemplates) {
			formTemplateMap.put(formTemplate.getId().toString(), formTemplate);
		}

		return formTemplateMap;

	}

}
