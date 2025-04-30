package hu.ksh.idgs.worklist.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import hu.ksh.idgs.worklist.api.model.NoteApiModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.dao.NoteRepository;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dto.ResetSendStatusDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedExportDto;
import hu.ksh.idgs.worklist.dto.WorklistSendFilterDto;
import hu.ksh.idgs.worklist.dto.WorklistSendItemDto;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.service.WorklistSendService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.exception.ServiceException;

@Service("worklistSendService")
public class WorklistSendServiceImpl implements WorklistSendService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorklistSendServiceImpl.class);

	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Autowired
	private NoteRepository noteRepository;

	@TransactionRequired
	@Override
	public List<WorklistSendItemDto> getItems(final WorklistSendFilterDto worklistSendFilter) throws ServiceException {

		final String formTemplateId = getFormTemplateId(///
				worklistSendFilter.getFormCode(), //
				worklistSendFilter.getYear(), //
				worklistSendFilter.getPeriod(), //
				worklistSendFilter.getAuthorization());

		if (formTemplateId == null) {
			throw new ServiceException(String.format("Form template was not found (formCode: %s, year: %d, period: %s)",
					worklistSendFilter.getFormCode(), worklistSendFilter.getYear(), worklistSendFilter.getPeriod()));
		}

		LOGGER.debug("Form template ID: {}", formTemplateId);

		final List<WorklistSendItemDto> sendItems = this.worklistItemRepository.getWorklistSendItems(formTemplateId,
				worklistSendFilter.getMaxResult());

		setSendStatusOnWorklistItems(sendItems, SendStatusType.PENDING );

		return sendItems;
	}

	private void setSendStatusOnWorklistItems(final List<WorklistSendItemDto> sendItems) throws ServiceException {

		for (final WorklistSendItemDto sendItem : sendItems) {
			setSendStatusOnWorklistItem(sendItem.getWorklistId());
		}
	}
	private void setSendStatusOnWorklistItems(final List<WorklistSendItemDto> sendItems, SendStatusType statusType) throws ServiceException {

		for (final WorklistSendItemDto sendItem : sendItems) {
			setSendStatusOnWorklistItem(sendItem.getWorklistId(), statusType);
		}
	}

	private void setSendStatusOnWorklistItem(final IdgsEntityId worklistId) throws ServiceException {
		setSendStatusOnWorklistItem(worklistId, SendStatusType.SENT);
	}

	private void setSendStatusOnWorklistItem(final IdgsEntityId worklistId, SendStatusType statusType) throws ServiceException {
		final Optional<WorklistItemEntity> worklistOptional = this.worklistItemRepository.findById(worklistId);
		if (worklistOptional.isEmpty()) {
			return;
		}
		final WorklistItemEntity worklistItem = worklistOptional.get();
		worklistItem.setSendStatus(statusType);
		worklistItem.setSendStatusUpdateTime(Date.from(Instant.now()));
		saveWorklistItem(worklistItem);
	}

	private WorklistItemEntity saveWorklistItem(final WorklistItemEntity worklistItem) throws ServiceException {

		try {
			return this.worklistItemRepository.save(worklistItem);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@TransactionRequired
	@Override
	public void resetSendStatus(final String authorization, final ResetSendStatusDto resetSendStatusData)
			throws ServiceException {

		try {

			Assert.notNull(authorization, "authorization must be not null");
			Assert.notNull(resetSendStatusData, "resetSendStatusData must be not null");

			final List<String> externalIds = resetSendStatusData.getExternalIds();
			final String formCode = resetSendStatusData.getFormCode();
			final Integer year = resetSendStatusData.getYear();
			final String period = resetSendStatusData.getPeriod();

			Assert.notNull(externalIds, "externalIds must be not null");
			Assert.notNull(formCode, "formCode must be not null");
			Assert.notNull(year, "year must be not null");

			final String formTemplateId = getFormTemplateId(formCode, year, period, authorization);
			Assert.notNull(formTemplateId, String
					.format("Form template was not found (formcode: %s, year: %d, period: %s", formCode, year, period));

			if (externalIds.isEmpty()) {
				return;
			}

			final List<WorklistItemEntity> worklistItems = this.worklistItemRepository
					.getWorklistItemsByExternalId(externalIds, formTemplateId);

			if (worklistItems.isEmpty()) {
				return;
			}

			for (final WorklistItemEntity worklistItem : worklistItems) {
				worklistItem.setSendStatus(SendStatusType.NOT_SENT);
				saveWorklistItem(worklistItem);
			}

		} catch (final IllegalArgumentException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	@TransactionRequired
	@Override
	public void resetSendStatusesByWorklistId(final String authorization, final List<String> worklistIds)
			throws ServiceException {

		try {

			Assert.notNull(authorization, "authorization must be not null");
			Assert.notNull(worklistIds, "WorklistIds must not be null");

			final List<WorklistItemEntity> worklistItems = new ArrayList<WorklistItemEntity>();

			for (final String id : worklistIds) {
				if (this.worklistItemRepository.findById(IdgsEntityId.fromString(id)).isPresent()) {
					worklistItems.add(this.worklistItemRepository.findById(IdgsEntityId.fromString(id)).get());
				}
			}

			if (worklistItems.isEmpty()) {
				return;
			}

			for (final WorklistItemEntity worklistItem : worklistItems) {
				worklistItem.setSendStatus(SendStatusType.NOT_SENT);
				saveWorklistItem(worklistItem);
			}

		} catch (final IllegalArgumentException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	@TransactionRequired
	@Override
	public void setStatusesByTransmitterProcessResult(final String authorization,
												  final List<NoteApiModel> worklistIdsSucces,
												  final List<NoteApiModel> worklistIdsFailed) throws ServiceException {
		try {
			Assert.notNull(authorization, "authorization must be not null");
			Assert.notNull(worklistIdsSucces, "worklistIdsSucces must not be null");
			Assert.notNull(worklistIdsFailed, "worklistIdsFailed must not be null");

			for (final NoteApiModel noteApiModel : worklistIdsSucces) {
				if(noteApiModel.getWorklistId()!=null && !noteApiModel.getWorklistId().isEmpty()) {
					if (this.worklistItemRepository.findById(IdgsEntityId.fromString(noteApiModel.getWorklistId())).isPresent()) {
						WorklistItemEntity worklistItem = this.worklistItemRepository.findById(IdgsEntityId.fromString(noteApiModel.getWorklistId())).get();
						worklistItem.setSendStatus(SendStatusType.SENT);
						if (noteApiModel.getNote() != null && !noteApiModel.getNote().isEmpty()) {
							worklistItem.setSendProcessMsg(noteApiModel.getNote().substring(0, Math.min(noteApiModel.getNote().length(), 1024) - 1));
						}
						worklistItem.setSendStatusUpdateTime(Date.from(Instant.now()));
						saveWorklistItem(worklistItem);
					} else {
						this.LOGGER.warn("Update failed. Could not find WorklistItemEntity[" + IdgsEntityId.fromString(noteApiModel.getWorklistId()) + "]");
					}
				}
			}

			for (final NoteApiModel noteApiModelFl : worklistIdsFailed) {
				if(noteApiModelFl.getWorklistId()!=null && !noteApiModelFl.getWorklistId().isEmpty()) {
					if (this.worklistItemRepository.findById(IdgsEntityId.fromString(noteApiModelFl.getWorklistId())).isPresent()) {
						WorklistItemEntity worklistItem = this.worklistItemRepository.findById(IdgsEntityId.fromString(noteApiModelFl.getWorklistId())).get();
						worklistItem.setSendStatus(SendStatusType.ERROR);
						if (noteApiModelFl.getNote() != null && !noteApiModelFl.getNote().isEmpty()) {
							worklistItem.setSendProcessMsg(noteApiModelFl.getNote().substring(0, Math.min(noteApiModelFl.getNote().length(), 1024) - 1));
						}
						worklistItem.setSendStatusUpdateTime(Date.from(Instant.now()));
						saveWorklistItem(worklistItem);
					} else {
						this.LOGGER.warn("Update failed. Could not find WorklistItemEntity[" + IdgsEntityId.fromString(noteApiModelFl.getWorklistId()) + "]");
					}
				}
			}


		} catch (final IllegalArgumentException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private String getFormTemplateId(final String formCode, final Integer year, final String period,
			final String authorization) throws ServiceException {

		return this.formServiceProxyCallFormTemplate.getFormTemplateId(///
				formCode, //
				year, //
				period, //
				authorization);
	}

	@Override
	public List<WorklistItemDetailedExportDto> getWorklistItemsForExport(final List<IdgsEntityId> worklistIds)
			throws ServiceException {

		final List<WorklistItemDetailedExportDto> worklistItems = this.worklistItemRepository
				.getWorklistItemsForExport(worklistIds);

		worklistItems.forEach(item -> item.setWorklistNotes(this.noteRepository.getNotes(item.getWorklistItemId())));

		return worklistItems;
	}

}
