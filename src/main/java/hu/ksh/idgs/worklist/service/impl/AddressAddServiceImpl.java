package hu.ksh.idgs.worklist.service.impl;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import hu.ksh.idgs.core.dto.form.FormTemplateDetailedInfoDto;
import hu.ksh.idgs.core.exception.DaoExeption;
import hu.ksh.idgs.core.value.FormStatusType;
import hu.ksh.idgs.worklist.dao.AddressRepository;
import hu.ksh.idgs.worklist.dao.HouseHoldRepository;
import hu.ksh.idgs.worklist.dao.NoteRepository;
import hu.ksh.idgs.worklist.dao.WorklistAssociationRepository;
import hu.ksh.idgs.worklist.dao.WorklistItemRepository;
import hu.ksh.idgs.worklist.dao.WorklistUserRepository;
import hu.ksh.idgs.worklist.dto.AddressAddDto;
import hu.ksh.idgs.worklist.dto.AddressDto;
import hu.ksh.idgs.worklist.dto.HouseHoldDto;
import hu.ksh.idgs.worklist.dto.NoteDto;
import hu.ksh.idgs.worklist.dto.WorklistItemDetailedDto;
import hu.ksh.idgs.worklist.model.AddressEntity;
import hu.ksh.idgs.worklist.model.HouseHoldEntity;
import hu.ksh.idgs.worklist.model.WorklistAssociationEntity;
import hu.ksh.idgs.worklist.model.WorklistItemEntity;
import hu.ksh.idgs.worklist.model.WorklistUserEntity;
import hu.ksh.idgs.worklist.model.value.ExternalSystem;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.model.value.WorklistCreateType;
import hu.ksh.idgs.worklist.service.AddressAddService;
import hu.ksh.idgs.worklist.service.proxy.call.FormServiceProxyCallFormTemplate;
import hu.ksh.maja.core.annotation.TransactionRequired;
import hu.ksh.maja.core.exception.ServiceException;
import hu.ksh.maja.core.helper.RoleHelper;
import hu.ksh.maja.core.service.MajaAuthenticationService;
import hu.ksh.maja.core.value.Language;
import jakarta.persistence.PersistenceException;
import jakarta.validation.ValidationException;

@Service("addressAddService")
public class AddressAddServiceImpl implements AddressAddService {

	@Autowired
	private AddressRepository addressRepository;

	@Autowired
	private HouseHoldRepository houseHoldRepository;

	@Autowired
	private WorklistAssociationRepository worklistAssociationRepository;

	@Autowired
	private WorklistItemRepository worklistItemRepository;

	@Autowired
	private WorklistUserRepository worklistUserRepository;

	@Autowired
	private FormServiceProxyCallFormTemplate formServiceProxyCallFormTemplate;

	@Autowired
	private MajaAuthenticationService majaAuthenticationService;

	@Autowired
	private NoteRepository noteRepository;

	@TransactionRequired
	@Override
	public WorklistItemDetailedDto saveAddress(final AddressAddDto addressAddData) throws ServiceException {
		try {

			Assert.notNull(addressAddData, "addressAddData must be not null");
			final String formTemplateId = addressAddData.getFormTemplateId();
			Assert.notNull(formTemplateId, "formTemplateId must be not null");

			AddressEntity address = createNewAddress(addressAddData.getAddress());
			address = saveAddress(address);

			HouseHoldEntity houseHold = createNewHouseHold(address);
			houseHold = saveHouseHold(houseHold);

			WorklistAssociationEntity worklistAssociation = createNewWorklistAssociation(getAddressId(),
					ExternalSystem.MAJA, address);
			worklistAssociation = saveWorklistAssociation(worklistAssociation);

			WorklistItemEntity worklistItem = createNewWorklistItem(formTemplateId, houseHold, worklistAssociation);
			worklistItem = saveWorklistItem(worklistItem);

			final FormTemplateDetailedInfoDto formTemplateInfo = getFormTemplateInfo(addressAddData.getAuthorization(),
					formTemplateId);
			final WorklistUserEntity worklistUser = createNewInterviewerWorklistUser(worklistItem);
			saveWorklistUser(worklistUser);

			return getWorklistItemData(worklistItem, formTemplateInfo, worklistAssociation);

		} catch (IllegalArgumentException | ValidationException | PersistenceException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private AddressEntity createNewAddress(final AddressDto addressData) {
		final AddressEntity address = new AddressEntity();
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

		return address;
	}

	private AddressEntity saveAddress(final AddressEntity address) throws ServiceException {
		try {
			return this.addressRepository.save(address);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private HouseHoldEntity createNewHouseHold(final AddressEntity address) throws ServiceException {
		final HouseHoldEntity houseHold = new HouseHoldEntity();
		houseHold.setAddress(address);
		houseHold.setInternalId("1");

		return houseHold;
	}

	private HouseHoldEntity saveHouseHold(final HouseHoldEntity houseHold) throws ServiceException {
		try {
			return this.houseHoldRepository.save(houseHold);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private WorklistAssociationEntity createNewWorklistAssociation(final String externalAddressId,
			final ExternalSystem externalSystem, final AddressEntity address) {
		final WorklistAssociationEntity worklistAssociation = new WorklistAssociationEntity();
		worklistAssociation.setExternalId(externalAddressId);
		worklistAssociation.setExternalSystem(externalSystem);
		worklistAssociation.setAddress(address);

		return worklistAssociation;
	}

	private WorklistAssociationEntity saveWorklistAssociation(final WorklistAssociationEntity worklistAssociation)
			throws ServiceException {
		try {
			return this.worklistAssociationRepository.save(worklistAssociation);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private WorklistItemEntity createNewWorklistItem(final String formTemplateId, final HouseHoldEntity houseHold,
			final WorklistAssociationEntity worklistAssociation) {

		final WorklistItemEntity worklistItem = new WorklistItemEntity();
		worklistItem.setFormTemplateId(formTemplateId);
		worklistItem.setHouseHold(houseHold);
		worklistItem.getWorklistAssociations().add(worklistAssociation);
		worklistItem.setFormStatus(FormStatusType.NOT_STARTED);
		worklistItem.setSendStatus(SendStatusType.NOT_SENT);
		worklistItem.setCreateType(WorklistCreateType.NEW_ADDRESS);

		return worklistItem;
	}

	private WorklistItemEntity saveWorklistItem(final WorklistItemEntity worklistItem) throws ServiceException {
		try {
			return this.worklistItemRepository.save(worklistItem);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private WorklistUserEntity createNewWorklistUser(final WorklistItemEntity worklistItem, final String userRoleName,
			final String userId, final String userName) throws ServiceException {

		final WorklistUserEntity worklistUser = new WorklistUserEntity();
		worklistUser.setWorklistItem(worklistItem);
		worklistUser.setRoleName(userRoleName);
		worklistUser.setUserId(userId);
		worklistUser.setUserName(userName);

		return worklistUser;
	}

	private WorklistUserEntity createNewInterviewerWorklistUser(final WorklistItemEntity worklistItem)
			throws ServiceException {

		final String userName = this.majaAuthenticationService.getUserName();
		final String userId = this.majaAuthenticationService.getUserId();

		return createNewWorklistUser(worklistItem, RoleHelper.ROLE_INTERVIEWER, userId, userName);
	}

	private WorklistUserEntity saveWorklistUser(final WorklistUserEntity worklistUser) throws ServiceException {
		try {
			return this.worklistUserRepository.save(worklistUser);
		} catch (final DaoExeption e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	private FormTemplateDetailedInfoDto getFormTemplateInfo(final String authorization, final String formTemplateId)
			throws ServiceException {

		return this.formServiceProxyCallFormTemplate.getFormTemplateInfo(authorization, Language.getDefault(),
				formTemplateId);

	}

	private String getAddressId() {
		final long addressSeq = getAddressSeq();
		return String.format("MAJA%011d", addressSeq);
	}

	private long getAddressSeq() {
		return this.worklistAssociationRepository.getNextAddressSequence();
	}

	private WorklistItemDetailedDto getWorklistItemData(final WorklistItemEntity worklistItem,
			final FormTemplateDetailedInfoDto formTemplateInfo, final WorklistAssociationEntity worklistAssociation) {

		final HouseHoldEntity houseHold = worklistItem.getHouseHold();

		final List<NoteDto> notes = this.noteRepository.getNotes(worklistItem.getId());

		final WorklistItemDetailedDto worklistItemData = new WorklistItemDetailedDto();
		worklistItemData.setWorklistItemId(worklistItem.getId());
		worklistItemData.setFormTemplateId(worklistItem.getFormTemplateId());
		worklistItemData.setWorklistAssociationExternalId(worklistAssociation.getExternalId());
		worklistItemData.setWorklistNote(notes);
		worklistItemData.setAddress(getAddressData(houseHold));
		worklistItemData.setHouseHoldId(houseHold.getInternalId());
		worklistItemData.setFormTemplateInfo(formTemplateInfo);
		worklistItemData.setFormStatus(worklistItem.getFormStatus());
		worklistItemData.setLastVisitStatusCode(worklistItem.getLatestVisitCode());
		worklistItemData.setCloseDate(worklistItem.getCloseDate());
		worklistItemData.setWorklistInfo(worklistItem.getWorklistInfo());
		worklistItemData.setAddressType(worklistItem.getAddressType());

		return worklistItemData;
	}

	private AddressDto getAddressData(final HouseHoldEntity houseHold) {

		final AddressEntity address = houseHold.getAddress();

		final AddressDto addressData = new AddressDto();
		addressData.setZipCode(address.getZipCode());
		addressData.setCountyCode(address.getCountyCode());
		addressData.setSettlementCode(address.getSettlementCode());
		addressData.setSettlement(address.getSettlement());
		addressData.setSettlementPart(address.getSettlementPart());
		addressData.setPlaceName(address.getPlaceName());
		addressData.setPlaceType(address.getPlaceType());
		addressData.setHouseNumber(address.getHouseNumber());
		addressData.setParcelNumber(address.getParcelNumber());
		addressData.setBuilding(address.getBuilding());
		addressData.setStairway(address.getStairway());
		addressData.setFloor(address.getFloor());
		addressData.setDoor(address.getDoor());
		addressData.setHouseholds(getHouseHoldDataList(houseHold));
		addressData.setFormattedAddress(address.getFormattedAddress());

		return addressData;
	}

	private List<HouseHoldDto> getHouseHoldDataList(final HouseHoldEntity houseHold) {

		if (houseHold == null) {
			return Collections.emptyList();
		}

		final HouseHoldDto houseHoldData = new HouseHoldDto();
		houseHoldData.setId(houseHold.getInternalId());

		return List.of(houseHoldData);
	}

}
