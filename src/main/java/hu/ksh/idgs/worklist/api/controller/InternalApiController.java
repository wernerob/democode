package hu.ksh.idgs.worklist.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import hu.ksh.idgs.core.model.IdgsEntityId;
import hu.ksh.idgs.worklist.api.InternalApi;
import hu.ksh.idgs.worklist.api.model.AddressExistsOnFormResponseApiModel;
import hu.ksh.idgs.worklist.api.model.ResultStatusApiModel;
import hu.ksh.idgs.worklist.api.model.VisitSaveResponseApiModel;
import hu.ksh.idgs.worklist.api.model.VisitSaveWithCodeRequestApiModel;
import hu.ksh.idgs.worklist.service.HouseHoldService;
import hu.ksh.idgs.worklist.service.VisitSavingService;
import hu.ksh.maja.core.dto.VisitType;
import hu.ksh.maja.core.exception.ServiceException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@RestController
public class InternalApiController implements InternalApi {

	private static final Logger LOGGER = LoggerFactory.getLogger(InternalApiController.class);

	@Lazy
	@Autowired
	private VisitSavingService visitSavingService;

	@Lazy
	@Autowired
	private HouseHoldService houseHoldService;

	@PreAuthorize("hasAuthority('MAJA_OSAP_WRITE')")
	@Override
	public ResponseEntity<VisitSaveResponseApiModel> saveVisitWithCode(@NotNull final String authorization,
			@Valid final VisitSaveWithCodeRequestApiModel visitSaveWithCodeRequestApiModel) {

		final VisitSaveResponseApiModel response = new VisitSaveResponseApiModel();
		final IdgsEntityId worklistItemId = IdgsEntityId.fromString(visitSaveWithCodeRequestApiModel.getWorklistId());

		try {

			this.visitSavingService.saveVisitWithCode(//
					authorization, //
					worklistItemId, //
					visitSaveWithCodeRequestApiModel.getVisitCode(), //
					VisitType.valueOf(visitSaveWithCodeRequestApiModel.getVisitType().toString()));

			response.setStatus(ResultStatusApiModel.OK);

		} catch (final Throwable e) {
			InternalApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<AddressExistsOnFormResponseApiModel> addressExistsOnForm(@NotNull final String authorization,
			@NotNull @Size(max = 36) @Valid final String formTemplateId,
			@NotNull @Size(max = 36) @Valid final String kshAddressId,
			@NotNull @Size(max = 6) @Valid final String houseHoldId) {

		final AddressExistsOnFormResponseApiModel response = new AddressExistsOnFormResponseApiModel();

		try {
			final boolean exists = this.houseHoldService.existsOnForm(formTemplateId, kshAddressId, houseHoldId);

			response.setExists(exists);
			response.setStatus(ResultStatusApiModel.OK);
		} catch (final ServiceException e) {
			InternalApiController.LOGGER.error(e.getMessage(), e);
			response.setStatus(ResultStatusApiModel.ERROR);
		}

		return ResponseEntity.ok(response);
	}

}
