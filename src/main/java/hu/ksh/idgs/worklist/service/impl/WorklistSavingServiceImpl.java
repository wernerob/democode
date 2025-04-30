package hu.ksh.idgs.worklist.service.impl;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.configuration.WorklistProperties;
import hu.ksh.idgs.worklist.dao.AddressRepository;
import hu.ksh.idgs.worklist.dao.HouseHoldRepository;
import hu.ksh.idgs.worklist.dao.WorklistAssociationRepository;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dao.WorklistUserRepository;
import hu.ksh.idgs.worklist.dao.ZoneRepository;
import hu.ksh.idgs.worklist.dto.AddNewHouseHoldErrorValue;
import hu.ksh.idgs.worklist.dto.AddressDto;
import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.WorklistAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.dto.WorklistSaveAddressDto;
import hu.ksh.idgs.worklist.dto.WorklistUserDto;
import hu.ksh.idgs.worklist.dto.WorklistZoneDto;
import hu.ksh.idgs.worklist.model.AddressEntity;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.WorklistUserEntity;
import hu.ksh.idgs.worklist.model.ZoneEntity;
import hu.ksh.idgs.worklist.model.value.ExternalSystem;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.model.value.WorklistCreateType;
import hu.ksh.idgs.worklist.service.NoteService;
import hu.ksh.idgs.worklist.service.VisitSavingService;
import hu.ksh.idgs.worklist.service.WorklistSavingService;
import hu.ksh.idgs.worklist.service.impl.saveaddress.osap.SaveAddressOsapHandlerComponent;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallVisitFormTemplate;
import hu.ksh.idgs.worklist.service.proxy.call.TokenAuthProxyCallSaveValidity;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ValidationException;

//TODO Help me, I need a refactor
@Service("worklistSavingService")
public class WorklistSavingServiceImpl implements WorklistSavingService {

	private static final Logger LOGGER = LoggerFactory.getLogger(WorklistSavingServiceImpl.class);

	@Lazy
	@Autowired
	private ZoneRepository zoneRepository;

	@Lazy
	@Autowired
	private WorklistAssociationRepository worklistAssociationRepository;

	@Lazy
	@Autowired
	private AddressRepository addressRepository;

	@Lazy
	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Lazy
	@Autowired
	private WorklistUserRepository worklistUserRepository;

	@Lazy
	@Autowired
	private HouseHoldRepository houseHoldRepository;

	@Lazy
	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Lazy
	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	@Lazy
	@Autowired
	private WorklistSavingUserComponent worklistSavingUserComponent;

	@Lazy
	@Autowired
	private NoteService noteService;

	@Lazy
	@Autowired
	private VisitSavingService visitSavingService;

	@Lazy
	@Autowired
	private FormServiceProxyCallVisitFormTemplate formServiceProxyCallVisitFormTemplate;

	@Lazy
	@Autowired
	private WorklistProperties worklistProperties;

	@Lazy
	@Autowired
	private TokenAuthProxyCallSaveValidity tokenAuthProxyCallSaveValidity;

	@Lazy
	@Autowired
	private SaveAddressOsapHandlerComponent saveAddressOsapHandlerComponent;

	@TransactionRequired
	@Override
	public void saveAddresses(final WorklistSaveAddressDto worklistSaveAddress, final String fixedFormTemplateId)
			throws ServiceException {

		try {

			final String authorization = worklistSaveAddress.getAuthorization();
			final String roleIdInterviewer = getInterviewerRoleId();
//			final String roleIdSelfLoader = getSelfLoaderRoleId();

			Assert.notNull(worklistSaveAddress, "Worklist object must be not null");
			final WorklistZoneDto zoneData = worklistSaveAddress.getZone();
			Assert.notNull(zoneData, "Zone must be not null");

			final String interviewerUserId = getUserIdWithRegistering(worklistSaveAddress.getInterviewerUser(),
					authorization, roleIdInterviewer);

			final String externalZoneId = zoneData.getZoneId();

			final ZoneEntity zone = getZoneWithSaving(externalZoneId, zoneData.getSettlementCode(),
					zoneData.getZoneSeq());
			final String formCode = zoneData.getFormCode();
			final String formTemplateId = getFormTemplateId(fixedFormTemplateId, formCode, zoneData.getYear(),
					zoneData.getPeriod(), authorization);
			Assert.notNull(formTemplateId, String.format("Unknown form code: %s", formCode));

			final List<String> perItemValidityOsaps = getPerItemValidityOsaps();
			final boolean osapLevelValidity = perItemValidityOsaps.contains(formCode);

			for (final WorklistAddressDto addressInfo : zoneData.getAddresses()) {

				Assert.notNull(addressInfo, "Address information object must be not null");
				final String externalAddressId = addressInfo.getExternalAddressId();
				Assert.notNull(externalAddressId, "External address identifier must be not null");

				List<HouseHoldDto> houseHoldDataList = addressInfo.getAddress().getHouseholds();
				if (houseHoldDataList == null || houseHoldDataList.isEmpty()) {
					houseHoldDataList = getDefaultHouseHoldList();
				}

				for (final HouseHoldDto houseHoldData : houseHoldDataList) {

					WorklistAssociationEntity worklistAssociation = null;
					AddressEntity address = null;

					// TODO check if it is already matched
					final IdgsEntityId worklistAssociationId = this.worklistAssociationRepository
							.findIdByExternalId(externalAddressId);
					if (worklistAssociationId == null) {

						address = setAddress(new AddressEntity(), addressInfo.getAddress(), zone);
						address = saveAddress(address);

						worklistAssociation = createNewWorklistAssociation(externalAddressId, address);
						worklistAssociation = saveWorklistAssociation(worklistAssociation);

					} else {
						worklistAssociation = this.worklistAssociationRepository.getReferenceById(worklistAssociationId)
								.get();
						address = worklistAssociation.getAddress();
						updateAddress(address, addressInfo.getAddress(), zone);
						address = saveAddress(address);
					}

					final String houseHoldExternalId = houseHoldData.getId();
					final HouseHoldEntity houseHold = getHouseHold(address, houseHoldExternalId);

					final String worklistUserName = getWorklistUserName(worklistSaveAddress.getInterviewerUser());
					handleWorklistItem(authorization, worklistAssociation, formTemplateId, houseHold,
							houseHoldData.getWorklistInfo(), houseHoldData.getOspec(), addressInfo.getOrder(),
							houseHoldData.getInitVisitCode(), interviewerUserId, worklistUserName,
							addressInfo.getInterviewerCensusValidFrom(), addressInfo.getInterviewerCensusValidTo(),
							osapLevelValidity, houseHoldData.getAddressType(),
							houseHoldData.getSuccesfulPreviousVisit());
					handleTokenValidity(authorization, formCode, zoneData.getYear(), zoneData.getPeriod(),
							externalAddressId, houseHoldExternalId, addressInfo.getSelfLoaderCensusValidFrom(),
							addressInfo.getSelfLoaderCensusValidTo(), osapLevelValidity);
					this.saveAddressOsapHandlerComponent.handle(authorization, houseHoldData, addressInfo, zoneData);
				}

			}
		} catch (IllegalArgumentException | ValidationException | PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	private void handleTokenValidity(final String authorization, final String osapCode, final Integer year,
			final String period, final String kshAddressId, final String houseHoldId, final OffsetDateTime validFrom,
			final OffsetDateTime validTo, final boolean osapLevelValidity) throws ServiceException {

		if (!osapLevelValidity) {
			return;
		}

		if (!this.tokenAuthProxyCallSaveValidity.saveValidity(authorization, osapCode, year, period, kshAddressId,
				houseHoldId, validFrom, validTo)) {
			WorklistSavingServiceImpl.LOGGER.warn(
					"Token validity settings failed (osap: {}, year: {}, period: {}, address: {}, hh: {}", osapCode,
					year, period, kshAddressId, houseHoldId);
		}

	}

	private String getFormTemplateId(final String fixedFormTemplateId, final String formCode, final Integer year,
			final String period, final String authorization) throws ServiceException {

		if (fixedFormTemplateId != null) {
			return fixedFormTemplateId;
		}
		return this.formServiceProxyCallFormTemplate.getFormTemplateId(formCode, year, period, authorization);
	}

	private List<HouseHoldDto> getDefaultHouseHoldList() {
		final HouseHoldDto houseHoldData = new HouseHoldDto();
		houseHoldData.setId("1");
		return List.of(houseHoldData);
	}

	private AddressEntity setAddress(final AddressEntity address, final AddressDto addressData, final ZoneEntity zone) {

		address.setZipCode(addressData.getZipCode());
		address.setCountyCode(addressData.getCountyCode());
		address.setSettlementCode(addressData.getSettlementCode());
		address.setSettlement(addressData.getSettlement());
		address.setSettlementPart(addressData.getSettlementPart());
		address.setPlaceName(addressData.getPlaceName());
		address.setPlaceType(addressData.getPlaceType());
		address.setHouseNumber(addressData.getHouseNumber());
		address.setParcelNumber(addressData.getParcelNumber());
		address.setBuilding(addressData.getBuilding());
		address.setStairway(addressData.getStairway());
		address.setFloor(addressData.getFloor());
		address.setDoor(addressData.getDoor());
		address.setFormattedAddress(addressData.getFormattedAddress());
		if (zone != null) {
			address.getZones().add(zone);
		}

		return address;
	}

	private AddressEntity updateAddress(final AddressEntity address, final AddressDto addressData,
			final ZoneEntity zone) {

		address.setZipCode(addressData.getZipCode());
		address.setCountyCode(addressData.getCountyCode());
		address.setSettlementCode(addressData.getSettlementCode());
		address.setSettlement(addressData.getSettlement());
		address.setSettlementPart(addressData.getSettlementPart());
		address.setPlaceName(addressData.getPlaceName());
		address.setPlaceType(addressData.getPlaceType());
		address.setHouseNumber(addressData.getHouseNumber());
		address.setParcelNumber(addressData.getParcelNumber());
		address.setBuilding(addressData.getBuilding());
		address.setStairway(addressData.getStairway());
		address.setFloor(addressData.getFloor());
		address.setDoor(addressData.getDoor());
		address.setFormattedAddress(addressData.getFormattedAddress());
		if (zone != null) {
			address.getZones().clear();
			address.getZones().add(zone);
		}

		return address;
	}

	private HouseHoldEntity getHouseHold(final AddressEntity address, final String houseHoldInternalId)
			throws ServiceException {

		final List<HouseHoldEntity> houseHolds = address.getHouseHolds();
		if (houseHolds != null) {
			for (final HouseHoldEntity currentHouseHold : houseHolds) {
				if (currentHouseHold.getInternalId().equals(houseHoldInternalId)) {
					return currentHouseHold;
				}

			}
		}

		final HouseHoldEntity houseHold = new HouseHoldEntity();
		houseHold.setInternalId(houseHoldInternalId);
		houseHold.setAddress(address);

		return saveHouseHold(houseHold);
	}

	private ZoneEntity getZoneWithSaving(final String externalZoneId, final String settlementCode, final String zoneSeq)
			throws ServiceException {

		if (externalZoneId == null && settlementCode == null && zoneSeq == null) {
			return null;
		}

		final IdgsEntityId zoneId = getZoneId(externalZoneId, settlementCode, zoneSeq);

		ZoneEntity zone = null;
		if (zoneId == null) {
			zone = createNewZone(externalZoneId, settlementCode, zoneSeq);
			return saveZone(zone);
		}
		zone = this.zoneRepository.findById(zoneId).get();
		if (externalZoneId != null) {
			zone.setExternalId(externalZoneId);
		}
		if (settlementCode != null) {
			zone.setSettlementCode(settlementCode);
		}
		if (zoneSeq != null) {
			zone.setExternalSeq(zoneSeq);
		}

		return saveZone(zone);
	}

	private ZoneEntity createNewZone(final String zoneExternalId, final String settlementCode, final String zoneSeq) {

		final ZoneEntity zone = new ZoneEntity();
		zone.setExternalId(zoneExternalId);
		zone.setSettlementCode(settlementCode);
		zone.setExternalSeq(zoneSeq);

		return zone;
	}

	private WorklistAssociationEntity createNewWorklistAssociation(final String externalId,
			final AddressEntity address) {

		final WorklistAssociationEntity worklistAssociation = new WorklistAssociationEntity();
		worklistAssociation.setExternalId(externalId);
		worklistAssociation.setExternalSystem(ExternalSystem.LAKOS);
		worklistAssociation.setAddress(address);
		return worklistAssociation;
	}

	private WorklistItemEntity handleWorklistItem(final String authorization,
			final WorklistAssociationEntity worklistAssociation, final String formTemplateId,
			final HouseHoldEntity houseHold, final String worklistInfo, final String ospec, final Integer order,
			final String initVisitCode, final String interviewerUserId, final String interviewerName,
			final OffsetDateTime startDate, final OffsetDateTime endDate, final boolean osapLevelValidity,
			final String addressType, final String successfullPreviousVisit) throws ServiceException {

		WorklistItemEntity worklistItem = getWorklistItem(houseHold.getId(), formTemplateId);

		if (worklistItem == null) {
			worklistItem = createNewWorklistItem(houseHold, worklistAssociation, formTemplateId, worklistInfo, ospec,
					WorklistCreateType.MIGRATED, order, null, addressType);
		} else {
			worklistItem.setWorklistInfo(worklistInfo);
			worklistItem.setOspec(ospec);
			worklistItem.setSuccesfulPreviousVisit(successfullPreviousVisit);
			worklistItem.getWorklistAssociations().add(worklistAssociation);
			worklistItem.setOrder(order);
			worklistItem.setAddressType(addressType);
		}

		final boolean loadableVisitCode = initVisitCode != null && !initVisitCode.isBlank()
				&& worklistItem.getLatestVisitCode() == null;
		final FormStatusType origFormStatus = worklistItem.getFormStatus();
		if (loadableVisitCode) {
			worklistItem.setFormStatus(FormStatusType.FAILED);
		}
		worklistItem = saveWorklistItem(worklistItem);

		if (loadableVisitCode) {
			this.visitSavingService.saveVisitWithCode(authorization, worklistItem.getId(), initVisitCode,
					VisitType.FINAL);
		}

		final OffsetDateTime validFrom = osapLevelValidity ? startDate : null;
		final OffsetDateTime validTo = osapLevelValidity ? endDate : null;

		handleWorklistUser(interviewerUserId, interviewerName, RoleHelper.ROLE_INTERVIEWER, formTemplateId,
				houseHold.getId(), worklistItem, origFormStatus, validFrom, validTo);

		return worklistItem;
	}

	private WorklistItemEntity createNewWorklistItem(final HouseHoldEntity houseHold,
			final WorklistAssociationEntity worklistAssociation, final String formTemplateId, final String worklistInfo,
			final String ospec, final WorklistCreateType createType, final Integer order, final UUID newWorklistId,
			final String addressType) {

		final WorklistItemEntity worklistItem = new WorklistItemEntity();
		worklistItem.setHouseHold(houseHold);
		worklistItem.setFormTemplateId(formTemplateId);
		worklistItem.getWorklistAssociations().add(worklistAssociation);
		worklistItem.setFormStatus(FormStatusType.NOT_STARTED);
		worklistItem.setSendStatus(SendStatusType.NOT_SENT);
		worklistItem.setWorklistInfo(worklistInfo);

		worklistItem.setOspec(ospec);
		worklistItem.setCreateType(createType);
		worklistItem.setOrder(order);
		if (newWorklistId != null) {
			worklistItem.setId(IdgsEntityId.fromUUID(newWorklistId));
		}
		worklistItem.setAddressType(addressType);
		return worklistItem;

	}

	private String getUserIdWithRegistering(final WorklistUserDto userData, final String authorization,
			final String roleId) throws ServiceException {

		return this.worklistSavingUserComponent.getUserIdWithRegistering(userData, authorization, roleId);
	}

	private void handleWorklistUser(final String userId, final String userName, final String roleName,
			final String formTemplateId, final IdgsEntityId houseHoldId, final WorklistItemEntity worklistItem,
			final FormStatusType formStatus, final OffsetDateTime validFrom, final OffsetDateTime validTo)
			throws ServiceException {

		if (userId == null) {
			return;
		}

		if (FormStatusType.CLOSED.equals(formStatus) || FormStatusType.FAILED.equals(formStatus)) {
			return;
		}

		WorklistUserEntity worklistUser = this.worklistUserRepository.getWorklistUserForHouseHold(formTemplateId,
				houseHoldId, roleName);
		if (worklistUser == null) {
			worklistUser = createNewWorklistUser(worklistItem, roleName);
		}

		worklistUser.setUserId(userId);
		worklistUser.setUserName(userName);
		worklistUser.setValidFrom(validFrom);
		worklistUser.setValidTo(validTo);

		saveWorklistUser(worklistUser);

	}

	private WorklistUserEntity createNewWorklistUser(final WorklistItemEntity worklistItem, final String roleName) {

		final WorklistUserEntity worklistUser = new WorklistUserEntity();
		worklistUser.setWorklistItem(worklistItem);
		worklistUser.setRoleName(roleName);

		return worklistUser;
	}

	private IdgsEntityId getZoneId(final String externalZoneId, final String settlementCode, final String zoneSeq) {
		return this.zoneRepository.findIdByExternalId(externalZoneId, settlementCode, zoneSeq);
	}

	private ZoneEntity saveZone(final ZoneEntity zone) throws ServiceException {

		try {
			return this.zoneRepository.save(zone);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private WorklistAssociationEntity saveWorklistAssociation(final WorklistAssociationEntity worklistAssociation)
			throws ServiceException {

		try {
			return this.worklistAssociationRepository.save(worklistAssociation);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private AddressEntity saveAddress(final AddressEntity address) throws ServiceException {

		try {
			return this.addressRepository.save(address);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private WorklistItemEntity saveWorklistItem(final WorklistItemEntity worklistItem) throws ServiceException {

		try {
			return this.worklistItemRepository.save(worklistItem);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private WorklistUserEntity saveWorklistUser(final WorklistUserEntity worklistUser) throws ServiceException {

		try {
			return this.worklistUserRepository.save(worklistUser);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private HouseHoldEntity saveHouseHold(final HouseHoldEntity houseHold) throws ServiceException {

		try {
			return this.houseHoldRepository.save(houseHold);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private String getInterviewerRoleId() {
		return "f105cd2e-c129-47f0-825e-d2ea397fdeec";
	}

	private WorklistItemEntity getWorklistItem(final IdgsEntityId houseHoldId, final String formTemplateId) {

		return this.worklistItemRepository.getWorklistItemForHouseHold(houseHoldId, formTemplateId);
	}

	private WorklistItemEntity getWorklistItem(final IdgsEntityId worklistItemId) {
		final Optional<WorklistItemEntity> worklistItemOptional = this.worklistItemRepository.findById(worklistItemId);
		if (worklistItemOptional.isPresent()) {
			return worklistItemOptional.get();
		}

		return null;
	}

	@TransactionRequired
	@Override
	public void updateWorklistItem(final WorklistItemDetailedDto worklistItemDetailedData) throws ServiceException {

		try {

			Assert.notNull(worklistItemDetailedData, "Worklist item detailed object must be not null");
			final IdgsEntityId worklistItemId = worklistItemDetailedData.getWorklistItemId();
			Assert.notNull(worklistItemId, "Worklist item id must be not null");

			final WorklistItemEntity worklistItem = getWorklistItem(worklistItemId);
			Assert.notNull(worklistItemId, "Worklist item was not found");

			// worklistItem.setNote(worklistItemDetailedData.getWorklistNote());

//			final HouseHoldEntity houseHold = worklistItem.getHouseHold();
//			AddressEntity address = houseHold.getAddress();
//			final AddressDto addressData = worklistItemDetailedData.getAddress();
//			if (addressData != null) {
//				address = setAddress(address, addressData, null);
//				saveAddress(address);
//			}

			saveWorklistItem(worklistItem);

		} catch (final IllegalArgumentException | PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}

	}

	@TransactionRequired
	@Override
	public WorklistItemDetailedDto addNewHouseHold(final String authorization, final String externalAddressId,
			final String formTemplateId, final String newHouseHold, final UUID newWorklistId, final String addressType)
			throws ServiceException {

		try {

			Assert.notNull(externalAddressId, "External address id must be not null");
			Assert.notNull(formTemplateId, "formTemplateId must be not null");

			final FormTemplateDetailedInfoDto formTemplateInfoData = this.formServiceProxyCallFormTemplate
					.getFormTemplateInfo(authorization, Language.getDefault(), formTemplateId,
							RoleHelper.ROLE_INTERVIEWER);
			if (formTemplateInfoData == null) {
				throw new ServiceException(AddNewHouseHoldErrorValue.FORM_TEMPLATE_NOT_FOUND);
			}

			if (!currentTimeIsWithinRange(formTemplateInfoData.getCensusStartDate(),
					formTemplateInfoData.getCensusEndDate())) {

				throw new ServiceException(AddNewHouseHoldErrorValue.FORM_TEMPLATE_NOT_FOUND);
			}

			final String userId = this.majaAuthenticationService.getUserId();
			if (!this.worklistUserRepository.existsFormTemplateIdsForUser(userId, formTemplateId)) {
				throw new ServiceException(AddNewHouseHoldErrorValue.FORM_TEMPLATE_NOT_FOUND);
			}

			final String newHouseHoldId = getNewHouseHoldId(externalAddressId, newHouseHold, formTemplateId);

			final AddressEntity address = this.addressRepository.getAddressByExternalId(externalAddressId);
			final HouseHoldEntity houseHold = getHouseHold(address, newHouseHoldId);

			WorklistItemEntity worklistItem = createNewWorklistItem(houseHold, address.getWorklistAssociation(),
					formTemplateId, "", null, WorklistCreateType.NEW_HOUSEHOLD, null, newWorklistId, addressType);
			worklistItem = saveWorklistItem(worklistItem);

			final WorklistUserEntity worklistUser = createNewWorklistUser(worklistItem, RoleHelper.ROLE_INTERVIEWER);
			worklistUser.setUserId(userId);
			worklistUser.setUserName(this.majaAuthenticationService.getUserName());
			saveWorklistUser(worklistUser);

			return getWorklistItemData(worklistUser, address, externalAddressId, newHouseHoldId, formTemplateInfoData,
					addressType);

		} catch (IllegalArgumentException | ValidationException | PersistenceException e) {
			throw new ServiceException(AddNewHouseHoldErrorValue.GENERAL_ERROR);
		}

	}

	private String getNewHouseHoldId(final String externalAddressId, final String newHouseHold,
			final String formTemplateId) throws ServiceException {

		if (newHouseHold != null && !newHouseHold.isBlank()) {
			return newHouseHold;
		}

		Integer maxHouseHold = this.houseHoldRepository.getMaxHouseHold(externalAddressId, formTemplateId);
		if (maxHouseHold == null) {
			throw new ServiceException(AddNewHouseHoldErrorValue.EXTERNAL_ADDRESS_NOT_FOUND);
		}

		return String.valueOf(++maxHouseHold);
	}

	private boolean currentTimeIsWithinRange(final OffsetDateTime startDate, final OffsetDateTime endDate) {

		final OffsetDateTime startDateNullSafe = startDate == null ? OffsetDateTime.MAX : startDate;
		final OffsetDateTime endDateNullSafe = endDate == null ? OffsetDateTime.MIN : endDate;
		final OffsetDateTime now = OffsetDateTime.now();

		return now.isAfter(startDateNullSafe) && now.isBefore(endDateNullSafe);
	}

	private WorklistItemDetailedDto getWorklistItemData(final WorklistUserEntity worklistUser,
			final AddressEntity address, final String externalAddressId, final String houseHoldId,
			final FormTemplateDetailedInfoDto formTemplateInfoData, final String addressType) {

		final WorklistItemEntity worklistItem = worklistUser.getWorklistItem();

		final WorklistItemDetailedDto worklistItemData = new WorklistItemDetailedDto(//
				worklistItem.getId(), //
				String.valueOf(formTemplateInfoData.getId()), //
				externalAddressId, //
				address.getZipCode(), //
				address.getSettlementCode(), //
				address.getSettlement(), //
				address.getPlaceName(), //
				address.getPlaceType(), //
				address.getHouseNumber(), //
				address.getParcelNumber(), //
				address.getBuilding(), //
				address.getStairway(), //
				address.getFloor(), //
				address.getDoor(), //
				addressType, //
				worklistItem.getSuccesfulPreviousVisit() //
		);
		worklistItemData.setWorklistAssociationExternalId(externalAddressId);
		worklistItemData.setHouseHoldId(houseHoldId);
		worklistItemData.setFormTemplateInfo(formTemplateInfoData);
		worklistItemData.setFormStatus(worklistItem.getFormStatus());
		worklistItemData.setLastVisitStatusCode(worklistItem.getLatestVisitCode());
		worklistItemData.setAddressType(worklistItem.getAddressType());

		return worklistItemData;

	}

	private String getWorklistUserName(final WorklistUserDto worklistUserData) {

		return worklistUserData == null ? null : worklistUserData.getUserName();
	}

	private List<String> getPerItemValidityOsaps() {
		final String[] perItemOsaps = this.worklistProperties.getPerItemValidityOsaps();
		if (perItemOsaps == null) {
			return Collections.emptyList();
		}

		return Arrays.asList(perItemOsaps);
	}

}
