package hu.ksh.idgs.worklist.api.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import hu.ksh.idgs.worklist.api.model.*;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.api.WorklistApi;
import hu.ksh.idgs.worklist.api.controller.converter.VisitInfoConverter;
import hu.ksh.idgs.worklist.api.controller.converter.VisitItemConverter;
import hu.ksh.idgs.worklist.api.controller.converter.WorklistItemDetailedConverter;
import hu.ksh.idgs.worklist.api.controller.converter.WorklistItemUpdateConverter;
import hu.ksh.idgs.worklist.api.controller.mapper.AddNewAddressMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.NewAddressOsapListMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.NoteMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.VisitStatusSendMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.WorklistItemMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.WorklistSaveAddressMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.WorklistSendMapper;
import hu.ksh.idgs.worklist.api.controller.mapper.WorklistStatMapper;
import hu.ksh.idgs.worklist.dto.AddNewHouseHoldErrorValue;
import hu.ksh.idgs.worklist.dto.AddressAddDto;
import hu.ksh.idgs.worklist.dto.NoteDto;
import hu.ksh.idgs.worklist.dto.ResetSendStatusDto;
import hu.ksh.idgs.worklist.dto.SaveOsapDto;
import hu.ksh.idgs.worklist.dto.SendStatus;
import hu.ksh.idgs.worklist.dto.VisitInfoDto;
import hu.ksh.idgs.worklist.dto.VisitStatusSendDto;
import hu.ksh.idgs.worklist.dto.WorklistAdminItemDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedExportDto;
import hu.ksh.idgs.worklist.dto.WorklistSaveAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistSendFilterDto;
import hu.ksh.idgs.worklist.dto.WorklistSendItemDto;
import hu.ksh.idgs.worklist.dto.stat.WorklistStatDto;
import hu.ksh.idgs.worklist.service.AddressAddService;
import hu.ksh.idgs.worklist.service.NoteService;
import hu.ksh.idgs.worklist.service.VisitInfoService;
import hu.ksh.idgs.worklist.service.VisitSavingService;
import hu.ksh.idgs.worklist.service.VisitStatusSendService;
import hu.ksh.idgs.worklist.service.WorklistItemService;
import hu.ksh.idgs.worklist.service.WorklistItemsGetterService;
import hu.ksh.idgs.worklist.service.WorklistSavingService;
import hu.ksh.idgs.worklist.service.WorklistSendService;
import hu.ksh.idgs.worklist.service.WorklistStatService;
import hu.ksh.maja.core.dto.GetWorkitemStatusDto;
import hu.ksh.maja.core.dto.SaveOsapResultDto;
import hu.ksh.maja.core.exception.PermissionDeniedException;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.value.Language;
import hu.ksh.maja.core.value.MajaErrorValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
public class WorklistApiController implements WorklistApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorklistApiController.class);

	@Lazy
	@Autowired
	private WorklistSavingService worklistSavingService;

	@Lazy
	@Autowired
	private WorklistItemsGetterService worklistItemsGetterService;

	@Lazy
	@Autowired
	private WorklistItemService worklistItemService;

	@Lazy
	@Autowired
	private VisitInfoService visitInfoService;

	@Lazy
	@Autowired
	private VisitSavingService visitSavingService;

	@Lazy
	@Autowired
	private VisitStatusSendService visitStatusSendService;

	@Lazy
	@Autowired
	private AddressAddService addressAddService;

	@Lazy
	@Autowired
	private WorklistItemDetailedConverter worklistItemDetailedConverter;

	@Lazy
	@Autowired
	private WorklistItemUpdateConverter worklistItemUpdateConverter;

	@Lazy
	@Autowired
	private VisitInfoConverter visitInfoConverter;

	@Lazy
	@Autowired
	private VisitItemConverter visitItemConverter;

	@Lazy
	@Autowired
	private WorklistSendService worklistSendService;

	@Lazy
	@Autowired
	private WorklistStatService worklistStatService;

	@Lazy
	@Autowired
	private NoteService noteService;

	@Lazy
	@Autowired
	private VisitStatusSendMapper visitStatusSendMapper;

	@Lazy
	@Autowired
	private WorklistSaveAddressMapper worklistSaveAddressMapper;

	private final NoteMapper noteMapper = Mappers.getMapper(NoteMapper.class);

	private final AddNewAddressMapper addNewAddressMapper = Mappers.getMapper(AddNewAddressMapper.class);
	private final NewAddressOsapListMapper newAddressOsapListMapper = Mappers.getMapper(NewAddressOsapListMapper.class);
	private final WorklistSendMapper worklistSendMapper = Mappers.getMapper(WorklistSendMapper.class);
	private final WorklistItemMapper worklistItemMapper = Mappers.getMapper(WorklistItemMapper.class);
	private final WorklistStatMapper worklistStatMapper = Mappers.getMapper(WorklistStatMapper.class);

	private Language getLanguageFromModel(final LanguageApiModel language) throws ServiceException {

		if (language == null) {
			return null;
		}

		try {
			return Language.valueOf(language.toString());
		} catch (final IllegalArgumentException e) {
			throw new ServiceException(String.format("Unsupported language %s", language));
		}
	}

	private FormStatusType getFormStatusFromModel(final FormStatusApiModel status) throws ServiceException {

		if (status == null) {
			return null;
		}

		try {
			return FormStatusType.valueOf(status.toString());
		} catch (final IllegalArgumentException e) {
			throw new ServiceException(String.format("Unsupported form status: %s", status));
		}

	}

	private FormStatusApiModel getFormStatusFromData(final FormStatusType status) throws ServiceException {

		if (status == null) {
			return null;
		}

		try {
			return FormStatusApiModel.valueOf(status.toString());
		} catch (final IllegalArgumentException e) {
			throw new ServiceException(String.format("Unsupported form status: %s", status));
		}

	}

	private List<IdgsEntityId> getIdsFromUuid(final List<UUID> uuids) {

		if (uuids == null) {
			return Collections.emptyList();
		}
		final List<IdgsEntityId> ids = new ArrayList<>();
		for (final UUID uuid : uuids) {
			ids.add(IdgsEntityId.fromUUID(uuid));
		}

		return ids;
	}

	@PreAuthorize("hasAnyAuthority('MAJA_INTERVIEWER_WORKLIST','MAJA_ADMIN_WORKLIST')")
	@Override
	public ResponseEntity<GetNotesResposeApiModel> getNotes(@NotNull final String authorization,
			@NotNull @Valid final String worklistId) {
		final GetNotesResposeApiModel response = new GetNotesResposeApiModel();
		try {
			final List<NoteDto> noteApiModelList = this.noteService.getNotes(worklistId);
			response.setNotes(this.noteMapper.getNoteApiModels(noteApiModelList));
			response.setStatus(ResultStatusApiModel.OK);
			return ResponseEntity.ok(response);
		} catch (final Exception e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<WorklistSaveAddressListResponseApiModel> saveAddressList(@NotNull final String authorization,
			@Valid final WorklistSaveAddressListRequestApiModel worklistSaveAddressListRequestApiModel) {

		final WorklistSaveAddressListResponseApiModel response = new WorklistSaveAddressListResponseApiModel();

		final WorklistSaveAddressDto worklistSaveAddressData = this.worklistSaveAddressMapper
				.getDtoFromRequest(worklistSaveAddressListRequestApiModel, authorization);
		try {
			this.worklistSavingService.saveAddresses(worklistSaveAddressData, null);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setErrorMessage(e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_INTERVIEWER_WORKLIST')")
	@Override
	public ResponseEntity<FormWorklistResponseApiModel> getInterviewerWorklist(@NotNull final String authorization,
			@NotNull @Valid final LanguageApiModel lang) {

		try {

			final Language language = getLanguageFromModel(lang);

			final List<WorklistItemDetailedDto> worklistItemsData = this.worklistItemsGetterService
					.getWorklistItems(authorization, language);

			return ResponseEntity.ok(this.worklistItemDetailedConverter.getResponse(false, worklistItemsData));
		} catch (final ServiceException | IllegalArgumentException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(this.worklistItemDetailedConverter.getResponse(true, Collections.emptyList()));
		}
	}

	// TODO user can read any worklistid
	@PreAuthorize("hasAuthority('WORKLIST_VISITINFO')")
	@Override
	public ResponseEntity<VisitInfoResponseApiModel> visitInfo(@NotNull final String authorization,
			@NotNull @Valid final UUID worklistId, @NotNull @Valid final LanguageApiModel lang) {

		try {
			final Language language = getLanguageFromModel(lang);
			final VisitInfoDto visitInfoData = this.visitInfoService.getVisitInfo(authorization,
					IdgsEntityId.fromUUID(worklistId), language);
			final VisitInfoResponseApiModel visitInfo = this.visitInfoConverter.getResponse(visitInfoData, false);

			return ResponseEntity.ok(visitInfo);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(this.visitInfoConverter.getResponse(new VisitInfoDto(), true));
		}
	}

	@PreAuthorize("hasAnyAuthority('MAJA_INTERVIEWER_WORKLIST','MAJA_ADMIN_WORKLIST')")
	@Override
	public ResponseEntity<WorklistUpdateResponseApiModel> addNote(@NotNull final String authorization,
			@Valid final NoteApiModel noteApiModel) {
		try {

			this.noteService.saveNote(this.noteMapper.getNoteDto(noteApiModel));

			return ResponseEntity.ok(new WorklistUpdateResponseApiModel().status(ResultStatusApiModel.OK));
		} catch (final ServiceException | DaoExeption e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(new WorklistUpdateResponseApiModel().status(ResultStatusApiModel.ERROR));
		}
	}

	// TODO user can write any worklistid
	@PreAuthorize("hasAuthority('MAJA_OSAP_WRITE')")
	@Override
	public ResponseEntity<VisitSaveResponseApiModel> saveVisit(@NotNull final String authorization,
			@Valid final VisitSaveRequestApiModel visitSaveRequestApiModel) {

		final IdgsEntityId worklistItemId = IdgsEntityId.fromString(visitSaveRequestApiModel.getWorklistId());

		try {
			this.visitSavingService.saveVisit(authorization, worklistItemId,
					this.visitItemConverter.getVisitItemData(visitSaveRequestApiModel.getVisit()));
			return ResponseEntity.ok(new VisitSaveResponseApiModel().status(ResultStatusApiModel.OK));
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(new VisitSaveResponseApiModel().status(ResultStatusApiModel.ERROR));

		}
	}

	// TODO user can override any worklistid
	@PreAuthorize("hasAuthority('MAJA_INTERVIEWER_WORKLIST')")
	@Override
	public ResponseEntity<WorklistUpdateResponseApiModel> updateWorklistItem(@NotNull final String authorization,
			@Valid final FormWorklistItemApiModel formWorklistItemApiModel) {

		try {
			final WorklistItemDetailedDto worklistItemDetailedData = this.worklistItemUpdateConverter
					.getDto(formWorklistItemApiModel);
			this.worklistSavingService.updateWorklistItem(worklistItemDetailedData);

			return ResponseEntity.ok(new WorklistUpdateResponseApiModel().status(ResultStatusApiModel.OK));
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(new WorklistUpdateResponseApiModel().status(ResultStatusApiModel.ERROR));
		}
	}

	// TODO user can override any worklistid
	@PreAuthorize("hasAuthority('MAJA_OSAP_WRITE')")
	@Override
	public ResponseEntity<WorklistItemUpdateFormStatusResponseApiModel> updateFormStatus(
			@NotNull final String authorization,
			@Valid final WorklistItemUpdateFormStatusRequestApiModel worklistItemUpdateFormStatusRequestApiModel) {

		final IdgsEntityId worklistItemId = IdgsEntityId
				.fromString(worklistItemUpdateFormStatusRequestApiModel.getWorklistId());
		try {
			final FormStatusType formStatusData = getFormStatusFromModel(
					worklistItemUpdateFormStatusRequestApiModel.getFormStatus());
			this.worklistItemService.updateWorklistItemFormStatus(authorization, worklistItemId, formStatusData);
			return ResponseEntity
					.ok(new WorklistItemUpdateFormStatusResponseApiModel().status(ResultStatusApiModel.OK));
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity
					.ok(new WorklistItemUpdateFormStatusResponseApiModel().status(ResultStatusApiModel.ERROR));
		}

	}

	@PreAuthorize("hasAuthority('MAJA_OSAP_READ')")
	@Override
	public ResponseEntity<WorklistItemFormTemplateIdResponseApiModel> getFormTemplateIdFromWorklist(
			@NotNull final String authorization,
			@Valid final WorklistItemFormTemplateIdRequestApiModel worklistItemFormTemplateIdRequestApiModel) {

		final String formTemplateId = this.worklistItemService
				.getFormTemplateId(IdgsEntityId.fromString(worklistItemFormTemplateIdRequestApiModel.getWorklistId()));

		return ResponseEntity.ok(new WorklistItemFormTemplateIdResponseApiModel().formTemplateId(formTemplateId));
	}

	@PreAuthorize("hasAuthority('MAJA_OSAP_READ')")
	@Override
	public ResponseEntity<WorklistItemGetFormStatusResponseApiModel> getFormStatus(@NotNull final String authorization,
			@Valid final WorklistItemGetFormStatusRequestApiModel worklistItemGetFormStatusRequestApiModel) {

		final WorklistItemGetFormStatusResponseApiModel response = new WorklistItemGetFormStatusResponseApiModel();
		final FormStatusType formStatus = this.worklistItemService
				.getFormStatus(IdgsEntityId.fromString(worklistItemGetFormStatusRequestApiModel.getWorklistId()));
		try {
			response.formStatus(getFormStatusFromData(formStatus));

		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_OSAP_READ')")
	@Override
	public ResponseEntity<GetWorklistIdResponseApiModel> getWorklistId(@NotNull final String authorization,
			@Valid final GetWorklistIdRequestApiModel getWorklistIdRequestApiModel) {

		final IdgsEntityId worklistId = this.worklistItemService.getWorklistId(
				getWorklistIdRequestApiModel.getExternalAddressId(), getWorklistIdRequestApiModel.getHouseHoldId(),
				getWorklistIdRequestApiModel.getFormTemplateId());

		final String worklistIdStr = worklistId == null ? null : worklistId.toString();

		final GetWorklistIdResponseApiModel response = new GetWorklistIdResponseApiModel();
		response.setWorklistId(worklistIdStr);

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<VisitStatusSendResponseApiModel> visitStatusSend(@NotNull final String authorization,
			@Valid final VisitStatusSendRequestApiModel visitStatusSendRequestApiModel) {

		try {
			final VisitStatusSendDto visitStatusSendData = this.visitStatusSendService.send(authorization,
					visitStatusSendRequestApiModel.getPackageId());
			if (visitStatusSendData == null) {
				return ResponseEntity.ok(new VisitStatusSendResponseApiModel().status(ResultStatusApiModel.WARNING));
			}

			final VisitStatusSendResponseApiModel resposne = this.visitStatusSendMapper
					.getResponse(visitStatusSendData);
			return ResponseEntity.ok(resposne.status(ResultStatusApiModel.OK));
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(new VisitStatusSendResponseApiModel().status(ResultStatusApiModel.ERROR));
		}

	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<VisitStatusReceiveResponseApiModel> visitStatusReceive(@NotNull final String authorization,
			@Valid final VisitStatusReceiveRequestApiModel visitStatusReceiveRequestApiModel) {

		final VisitStatusReceiveResponseApiModel response = new VisitStatusReceiveResponseApiModel();

		final SendStatus status = SendStatus.valueOf(visitStatusReceiveRequestApiModel.getStatus().toString());

		try {
			this.visitStatusSendService.updateSendStatus(visitStatusReceiveRequestApiModel.getReferencedPackageId(),
					status);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setErrorMessage(e.getMessage());
		}

		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasAuthority('MAJA_ADD_NEW_UNIT')")
	@Override
	public ResponseEntity<AddNewAddressResponseApiModel> addNewAddress(@NotNull final String authorization,
			@Valid final AddNewAddressRequestApiModel addNewAddressRequestApiModel) {

		final AddNewAddressResponseApiModel response = new AddNewAddressResponseApiModel();
		final AddressAddDto addressAddData = this.addNewAddressMapper.newAddress(addNewAddressRequestApiModel);
		addressAddData.setAuthorization(authorization);

		try {
			final WorklistItemDetailedDto newItemData = this.addressAddService.saveAddress(addressAddData);
			final FormWorklistItemApiModel newItem = this.worklistItemDetailedConverter.getWorklistItem(newItemData);
			response.setNewItem(newItem);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_ADD_NEW_UNIT')")
	@Override
	public ResponseEntity<NewAddressOsapListResponseApiModel> newAddressOsapList(@NotNull final String authorization) {

		final NewAddressOsapListResponseApiModel response = new NewAddressOsapListResponseApiModel();

		try {
			final List<FormTemplateDetailedInfoDto> formTemplateDataList = this.worklistItemService
					.getFormTemplalatesForNewAddress(authorization);
			final List<NewAddressOsapApiModel> formTempaltes = this.newAddressOsapListMapper
					.getResponse(formTemplateDataList);
			response.setForm(formTempaltes);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_SELFLOADER')")
	@Override
	public ResponseEntity<FormWorkItemResponseApiModel> getWorkItem(@NotNull final String authorization,
			@NotNull @Valid final LanguageApiModel lang) {

		final FormWorkItemResponseApiModel response = new FormWorkItemResponseApiModel();

		try {
			final WorklistItemDetailedDto worklistItemData = this.worklistItemsGetterService
					.getWorklistItemForSelfLoader(authorization, getLanguageFromModel(lang));

			if (worklistItemData == null) {
				response.setStatus(ResultStatusApiModel.WARNING);
				return ResponseEntity.ok(response);
			}

			final FormWorklistItemApiModel worklistItem = this.worklistItemDetailedConverter
					.getWorklistItem(worklistItemData);
			response.setStatus(ResultStatusApiModel.OK);
			response.setWorkItem(worklistItem);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasAuthority('MAJA_ADD_NEW_UNIT')")
	@Override
	public ResponseEntity<AddNewHouseHoldResponseApiModel> addNewHouseHold(@NotNull final String authorization,
			@Valid final AddNewHouseHoldRequestApiModel addNewHouseHoldRequestApiModel) {

		final AddNewHouseHoldResponseApiModel response = new AddNewHouseHoldResponseApiModel();

		try {
			final WorklistItemDetailedDto worklistItemData = this.worklistSavingService.addNewHouseHold(authorization,
					addNewHouseHoldRequestApiModel.getExternalAddressId(),
					addNewHouseHoldRequestApiModel.getFormTemplateId(),
					addNewHouseHoldRequestApiModel.getNewHouseHold(), //
					addNewHouseHoldRequestApiModel.getNewWorklistId(), //
					addNewHouseHoldRequestApiModel.getAddressType());

			final FormWorklistItemApiModel worklistItemModel = this.worklistItemDetailedConverter
					.getWorklistItem(worklistItemData);
			response.setStatus(ResultStatusApiModel.OK);
			response.setItem(worklistItemModel);
		} catch (final ServiceException e) {

			MajaErrorValue error = e.getErrorValue();
			if (error == null) {
				WorklistApiController.LOGGER.error(e.getMessage(), e);
				error = AddNewHouseHoldErrorValue.GENERAL_ERROR;
			}

			response.setStatus(ResultStatusApiModel.ERROR);
			response.setError(AddNewHouseHoldErrorApiModel.fromValue(error.getErrorCode()));

		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_OSAP_READ')")
	@Override
	public ResponseEntity<GetWorklistUserResponseApiModel> getWorklistUser(@NotNull final String authorization,
			@NotNull @Valid final UUID worklistId, @NotNull @Valid final String roleName) {

		try {
			final String userId = this.worklistItemService.getWorklistUserId(IdgsEntityId.fromUUID(worklistId),
					roleName);

			final GetWorklistUserResponseApiModel response = new GetWorklistUserResponseApiModel();
			response.setUserId(userId == null ? null : UUID.fromString(userId));
			response.setStatus(ResultStatusApiModel.OK);
			return ResponseEntity.ok(response);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			return ResponseEntity.ok(new GetWorklistUserResponseApiModel().status(ResultStatusApiModel.ERROR));
		}

	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<WorklistSendResponseApiModel> worklistSend(@NotNull final String authorization,
			@NotNull @Size(max = 5) @Valid final String formCode,
			@NotNull @Min(2020) @Max(3000) @Valid final Integer year, @Size(max = 3) @Valid final String period,
			@Min(1) @Max(2000) @Valid final Integer maxResult) {

		final WorklistSendResponseApiModel response = new WorklistSendResponseApiModel();

		try {
			final List<WorklistSendItemDto> itemDataList = this.worklistSendService
					.getItems(new WorklistSendFilterDto(formCode, year, period, maxResult, authorization));
			final List<WorklistSendItemApiModel> reponseItems = this.worklistSendMapper.getResponseItems(itemDataList);

			response.setItems(reponseItems);
			response.setStatus(itemDataList.isEmpty() ? ResultStatusApiModel.WARNING : ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setMessage(e.getMessage());
			response.setItems(Collections.emptyList());
		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_ADMIN_WORKLIST')")
	@Override
	public ResponseEntity<AdminWorklistResponseApiModel> getAdminWorklist(@NotNull final String authorization,
			@NotNull @Valid final LanguageApiModel lang, @NotNull @Valid final String formTemplateId,
			@Valid final String search) {

		final AdminWorklistResponseApiModel response = new AdminWorklistResponseApiModel();

		try {
			final Language language = getLanguageFromModel(lang);
			final List<WorklistAdminItemDto> worklistDataItems = this.worklistItemsGetterService
					.getWorklistAdminItems(authorization, formTemplateId, search, language);
			final List<AdminWorklistItemApiModel> worklistItems = this.worklistItemMapper
					.getAdminWorklistItemsModel(worklistDataItems);
			response.setStatus(ResultStatusApiModel.OK);
			response.setWorklistItems(worklistItems);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setWorklistItems(Collections.emptyList());
		}

		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasAuthority('WORKLIST_STAT')")
	@Override
	public ResponseEntity<WorklistStatResponseApiModel> stat(@NotNull final String authorization,
			@NotNull @Size(max = 36) @Valid final String formTemplateId) {

		final WorklistStatDto worklistStat = this.worklistStatService.getStat(formTemplateId);
		final WorklistStatResponseApiModel response = this.worklistStatMapper.getStatResponse(worklistStat);
		response.setStatus(ResultStatusApiModel.OK);
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<ResetSendStatusResponseApiModel> resetSendStatus(@NotNull final String authorization,
			@Valid final ResetSendStatusRequestApiModel resetSendStatusRequestApiModel) {

		final ResetSendStatusResponseApiModel response = new ResetSendStatusResponseApiModel();
		final ResetSendStatusDto resetSendStatusData = this.worklistSendMapper
				.getResetSendStatusData(resetSendStatusRequestApiModel);
		try {
			this.worklistSendService.resetSendStatus(authorization, resetSendStatusData);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage());
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setErrorMessage(e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<ResetSendStatusResponseApiModel> resetSendStatusByWorklistId(
			@NotNull final String authorization,
			@Valid final ResetSendStatusByWorklistIdRequestApiModel resetSendStatusByWorklistIdRequestApiModel) {

		final ResetSendStatusResponseApiModel response = new ResetSendStatusResponseApiModel();
		final @NotNull @Size(min = 1, max = 1000) List<String> worklistIds = resetSendStatusByWorklistIdRequestApiModel
				.getWorklistIds();

		try {
			this.worklistSendService.resetSendStatusesByWorklistId(authorization, worklistIds);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage());
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setErrorMessage(e.getMessage());
		}

		return ResponseEntity.ok(response);
	}

//	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<ResetSendStatusResponseApiModel> setStatusesByTransmitterProcessResult(
			@NotNull final String authorization,
			@Valid final SetStatusByDataTransmitterRequestApiModel setStatusByDataTransmitterRequestApiModel) {

		final ResetSendStatusResponseApiModel response = new ResetSendStatusResponseApiModel();
		final @NotNull List<NoteApiModel> worklistIdsSucces = setStatusByDataTransmitterRequestApiModel
				.getWorklistIdsSuccess();
		final @NotNull List<NoteApiModel> worklistIdsFailed = setStatusByDataTransmitterRequestApiModel
				.getWorklistIdsFailed();

		try {
			this.worklistSendService.setStatusesByTransmitterProcessResult(authorization, worklistIdsSucces, worklistIdsFailed);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage());
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setErrorMessage(e.getMessage());
		}

		return ResponseEntity.ok(response);
	}


	@PreAuthorize("hasAuthority('MAJA_ADMIN_WORKLIST')")
	@Override
	public ResponseEntity<WorkItemInfoResponseApiModel> getWorkItemInfo(@NotNull final String authorization,
			@NotNull @Valid final List<UUID> worklistIds, @NotNull @Valid final LanguageApiModel lang) {

		final WorkItemInfoResponseApiModel response = new WorkItemInfoResponseApiModel();
		try {
			final Language language = getLanguageFromModel(lang);
			final List<WorklistAdminItemDto> workitemInfoDataList = this.worklistItemsGetterService
					.getWorkItemInfo(authorization, getIdsFromUuid(worklistIds), language);
			final List<AdminWorklistItemApiModel> worklistItems = this.worklistItemMapper
					.getAdminWorklistItemsModel(workitemInfoDataList);
			response.setStatus(ResultStatusApiModel.OK);
			response.setItems(worklistItems);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);

	}

	@PreAuthorize("hasAuthority('ADMIN_FUNC_WRITE')")
	@Override
	public ResponseEntity<ResendResponseApiModel> resend(@NotNull final String authorization,
			@Valid final ResendRequestApiModel resendRequestApiModel) {
		final ResendResponseApiModel response = new ResendResponseApiModel();

		try {
			this.worklistItemService.resend(resendRequestApiModel.getFormTemplateId());
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}
		return ResponseEntity.ok(response);
	}

	@PreAuthorize("hasAuthority('MAJA_OSAP_WRITE')")
	@Override
	public ResponseEntity<SaveOsapResponseApiModel> saveOsap(@NotNull final String authorization,
			@Valid final SaveOsapRequestApiModel saveOsapRequestApiModel) {

		try {
			final SaveOsapDto saveOsapData = this.worklistItemMapper.getSaveOsapData(saveOsapRequestApiModel);
			final SaveOsapResultDto osapResult = this.worklistItemService.saveOsap(saveOsapData);
			final SaveOsapResponseApiModel response = this.worklistItemMapper.getSaveOsapResponseModel(osapResult);
			response.setStatus(ResultStatusApiModel.OK);
			return ResponseEntity.ok(response);
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			final SaveOsapResponseApiModel response = new SaveOsapResponseApiModel().status(ResultStatusApiModel.ERROR);
			return ResponseEntity.ok(response);
		}

	}

	@PreAuthorize("hasAuthority('MAJA_OSAP_READ')")
	@Override
	public ResponseEntity<GetWorkitemStatusResponseApiModel> getWorkitemStatus(@NotNull final String authorization,
			@NotNull @Valid final UUID worklistId) {

		final GetWorkitemStatusResponseApiModel response = new GetWorkitemStatusResponseApiModel();

		try {
			final GetWorkitemStatusDto workitemStatusData = this.worklistItemService
					.getWorkitemStatusData(IdgsEntityId.fromUUID(worklistId));
			final GetWorkitemStatusDataApiModel statusData = this.worklistItemMapper
					.getWorkitemStatusModel(workitemStatusData);
			response.setData(statusData);
			response.setStatus(ResultStatusApiModel.OK);
			return ResponseEntity.ok(response);
		} catch (final ServiceException | PermissionDeniedException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
			return ResponseEntity.ok(response);
		}

	}

	@PreAuthorize("hasAuthority('MAJA_SELFLOADER')")
	@Override
	public ResponseEntity<GetWorkitemStatusResponseApiModel> getSelfLoaderWorkitemStatus(
			@NotNull final String authorization, @NotNull @Valid final String formTemplateId,
			@NotNull @Size(max = 20) @Valid final String kshAddressId,
			@NotNull @Size(max = 5) @Valid final String houseHoldId) {

		final GetWorkitemStatusResponseApiModel response = new GetWorkitemStatusResponseApiModel();

		try {
			final GetWorkitemStatusDto workitemStatusData = this.worklistItemService
					.getWorkitemStatusData(formTemplateId, kshAddressId, houseHoldId);
			final GetWorkitemStatusDataApiModel statusData = this.worklistItemMapper
					.getWorkitemStatusModel(workitemStatusData);
			response.setData(statusData);
			response.setStatus(ResultStatusApiModel.OK);
			return ResponseEntity.ok(response);
		} catch (final ServiceException | PermissionDeniedException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
			return ResponseEntity.ok(response);
		}

	}

	@PreAuthorize("hasAuthority('MAJA_ADMIN_WORKLIST')")
	@Override
	public ResponseEntity<WorkItemsExportResponseApiModel> getWorkItemsExport(@NotNull final String authorization,
			@NotNull @Valid final List<UUID> worklistIds) {
		final WorkItemsExportResponseApiModel response = new WorkItemsExportResponseApiModel();
		try {
			final List<WorklistItemDetailedExportDto> worklistItems = this.worklistSendService
					.getWorklistItemsForExport(getIdsFromUuid(worklistIds));
			response.setStatus(ResultStatusApiModel.OK);
			response.setWorklistItems(this.worklistSendMapper.getExportItems(worklistItems));
		} catch (final ServiceException e) {
			WorklistApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
			response.setErrorMessage(e.getMessage());
			return ResponseEntity.ok(response);
		}
		return ResponseEntity.ok(response);
	}

}
