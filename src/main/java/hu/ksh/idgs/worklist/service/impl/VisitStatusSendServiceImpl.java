package hu.ksh.idgs.worklist.service.impl;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import hu.ksh.idgs.core.dto.codetable.CodeTableItemDto;
import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.VisitStatusRepository;
import hu.ksh.idgs.worklist.dao.VisitStatusSendRepository;
import hu.ksh.idgs.worklist.dto.SendStatus;
import hu.ksh.idgs.worklist.dto.VisitStatusSendDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendItemDto;
import hu.ksh.idgs.worklist.model.VisitStatusEntity;
import hu.ksh.idgs.worklist.model.VisitStatusSendEntity;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.service.VisitStatusSendItems;
import hu.ksh.idgs.worklist.service.VisitStatusSendService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallGetCodeTable;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.Language;

@Service("visitStatusSendService")
public class VisitStatusSendServiceImpl implements VisitStatusSendService {

	@Lazy
	@Autowired
	private VisitStatusRepository visitStatusRepository;

	@Lazy
	@Autowired
	private VisitStatusSendRepository visitStatusSendRepository;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Lazy
	@Autowired
	private FormServiceProxyCallGetCodeTable formServiceProxyCallGetCodeTable;

	@Lazy
	@Autowired
	private VisitStatusSendItems visitStatusSendLuszItems;

	@Lazy
	@Autowired
	private VisitStatusSendItems visitStatusSendPnszItems;

	@Lazy
	@Autowired
	private VisitStatusSendItems visitStatusSendMefItems;

	@Lazy
	@Autowired
	private VisitStatusSendItems visitStatusSendTimeUseItems;

	@TransactionRequired
	@Override
	public VisitStatusSendDto send(final String authorization, final String packageId) throws ServiceException {

		final List<FormTemplateDetailedInfoDto> activeFormTemplates = this.formServiceProxyCallFormTemplate
				.getFormTemplates(authorization, Language.getDefault(), true, null);

		if (activeFormTemplates.isEmpty()) {
			return null;
		}

		final List<String> activeFormTemplateIds = activeFormTemplates.stream()
				.map(formTemplate -> formTemplate.getId().toString()).toList();

		final VisitStatusSendDto sendData = this.visitStatusRepository
				.getNextSendableVisitStatus(activeFormTemplateIds);
		if (sendData == null) {
			return null;
		}

		final FormTemplateDetailedInfoDto formTemplateData = getFormData(activeFormTemplates,
				sendData.getFormTemplateId());
		if (formTemplateData == null) {
			throw new ServiceException(
					String.format("Form template was not found with id: %s", sendData.getFormTemplateId()));
		}

		final VisitStatusSendEntity visitStatusSend = getVisitStatusSend(packageId);
		final List<VisitStatusSendItemDto> items = this.visitStatusRepository
				.getSendableWorklistItems(sendData.getFormTemplateId(), sendData.getInterviewerUserId());
		setSendItemsVisitCode(authorization, items);
		updateSendItems(visitStatusSend, items);

		sendData.setFormTemplateInfo(formTemplateData);
		sendData.setItems(getAllItems(authorization, formTemplateData.getCode(), items));

		return sendData;
	}

	private VisitStatusSendEntity getVisitStatusSend(final String packageId) throws ServiceException {

		if (this.visitStatusSendRepository.hasExistingPackageId(packageId)) {
			throw new ServiceException(String.format("Package id already exists: %s", packageId));
		}

		final VisitStatusSendEntity visitStatusSend = createVisitStatusSend(packageId);
		return saveVisitStatusSend(visitStatusSend);
	}

	private FormTemplateDetailedInfoDto getFormData(final List<FormTemplateDetailedInfoDto> activeFormTemplates,
			final String formTemplateId) throws ServiceException {

		for (final FormTemplateDetailedInfoDto formTemplate : activeFormTemplates) {

			if (formTemplate.getId().toString().equals(formTemplateId)) {
				return formTemplate;
			}
		}

		return null;

	}

	private void setSendItemsVisitCode(final String authorization, final List<VisitStatusSendItemDto> items)
			throws ServiceException {

		final Map<String, String> visitStatusCodeTableIdCodeMap = new HashMap<>();

		for (final VisitStatusSendItemDto item : items) {

			if (item.getVisitStatusCode() != null) {
				continue;
			}

			final String visitStatusCodeTableId = item.getVisitStatusCodeTableId();
			String visitStatusCodeTableCode = visitStatusCodeTableIdCodeMap.get(visitStatusCodeTableId);

			if (visitStatusCodeTableCode == null) {
				visitStatusCodeTableCode = getCodeTableCode(authorization, visitStatusCodeTableId);
			}

			visitStatusCodeTableIdCodeMap.put(visitStatusCodeTableId, visitStatusCodeTableCode);
			item.setVisitStatusCode(visitStatusCodeTableCode);
		}

	}

	private String getCodeTableCode(final String authorization, final String codeTableId) throws ServiceException {

		final CodeTableItemDto codeTableItem = this.formServiceProxyCallGetCodeTable.getCodeTableItem(authorization,
				codeTableId, Language.HU);

		if (codeTableItem == null) {
			throw new ServiceException(String.format("Missing codetable id: %s", codeTableId));
		}

		return codeTableItem.getCode();
	}

	private VisitStatusSendEntity createVisitStatusSend(final String packageId) {

		final VisitStatusSendEntity visitStatusSend = new VisitStatusSendEntity();
		visitStatusSend.setPackageId(packageId);
		visitStatusSend.setEventTime(OffsetDateTime.now());

		return visitStatusSend;
	}

	private List<IdgsEntityId> getVisitStatusIds(final List<VisitStatusSendItemDto> items) {

		if (items == null) {
			return Collections.emptyList();
		}

		return items.stream().map(item -> item.getVisitStatusId()).collect(Collectors.toList());
	}

	private void updateSendItems(final VisitStatusSendEntity visitStatusSend, final List<VisitStatusSendItemDto> items)
			throws ServiceException {

		if (items == null || items.isEmpty()) {
			return;
		}

		final List<IdgsEntityId> visitStatusIds = getVisitStatusIds(items);
		final List<VisitStatusEntity> visitStatuses = this.visitStatusRepository.getVisitStatuses(visitStatusIds);
		updateSendStatus(visitStatusSend, visitStatuses, SendStatusType.PENDING);
	}

	private List<VisitStatusEntity> getCommunicatedVisitItems(final String packageId) {
		return this.visitStatusSendRepository.getVisitItems(packageId);
	}

	private void updateSendStatus(final VisitStatusSendEntity visitStatusSend, final String packageId,
			final SendStatusType status) throws ServiceException {

		final List<VisitStatusEntity> visitStatuses = getCommunicatedVisitItems(packageId);

		if (visitStatuses.isEmpty()) {
			throw new ServiceException(String.format("No items found for package id: %s", packageId));
		}

		updateSendStatus(visitStatusSend, visitStatuses, status);
	}

	private void updateSendStatus(final VisitStatusSendEntity visitStatusSend,
			final List<VisitStatusEntity> visitStatuses, final SendStatusType status) throws ServiceException {

		for (final VisitStatusEntity visitStatus : visitStatuses) {
			visitStatus.setSendStatus(status);

			if (visitStatusSend != null) {
				visitStatus.setVisitStatusSend(visitStatusSend);
			}
		}
		saveVisitStatuses(visitStatuses);
	}

	@TransactionRequired
	@Override
	public void updateSendStatus(final String packageId, final SendStatus sendStatus) throws ServiceException {
		switch (sendStatus) {
		case OK:
			updateSendStatus(null, packageId, SendStatusType.SENT);
			break;
		case ERROR:
			updateSendStatus(null, packageId, SendStatusType.ERROR);
			break;
		default:
			throw new ServiceException(String.format("Invalid form status: %s", sendStatus));
		}

	}

	public void saveVisitStatuses(final List<VisitStatusEntity> visitStatuses) throws ServiceException {
		try {
			this.visitStatusRepository.updateAll(visitStatuses);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private VisitStatusSendEntity saveVisitStatusSend(final VisitStatusSendEntity visitStatusSend)
			throws ServiceException {

		try {
			return this.visitStatusSendRepository.create(visitStatusSend);
		} catch (final DaoExeption | DataIntegrityViolationException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private List<VisitStatusSendItemDto> getAllItems(final String authorization, final String formCode,
			final List<VisitStatusSendItemDto> items) throws ServiceException {

		if (formCode == null || formCode.isBlank()) {
			throw new ServiceException("Missing form code");
		}

		switch (formCode) {
		case "1942":
		case "2003":
		case "2218":
		case "2417":
		case "2219":
		case "2375":
		case "1943":
		case "2007":
		case "2546":
		case "8060":
		case "2229":
		case "2374":
		case "2243":
		case "8061":
		case "1711":
		case "8075":
			return this.visitStatusSendLuszItems.getItems(authorization, items);
		case "1755":
			return this.visitStatusSendPnszItems.getItems(authorization, items);
		case "8056":
			return this.visitStatusSendMefItems.getItems(authorization, items);
		case "8074":
			return this.visitStatusSendTimeUseItems.getItems(authorization, items);
		default:
			throw new ServiceException(String.format("Unhandled form code %s for send visit status", formCode));
		}

	}

}
