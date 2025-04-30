package hu.ksh.idgs.worklist.service.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import hu.ksh.maja.tokenauth.api.model.CensusItemApiModel;
import hu.ksh.maja.tokenauth.api.model.CommonResponseApiModel;
import hu.ksh.maja.tokenauth.api.model.GetCensusItemResponseApiModel;
import hu.ksh.maja.tokenauth.api.model.SaveValidityRequestApiModel;
import hu.ksh.maja.tokenauth.api.model.TokenReactivateResponseApiModel;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@FeignClient(name = "tokenauth")
public interface TokenAuthServiceProxy {

	@PostMapping(value = "/token-auth/reactivate", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<TokenReactivateResponseApiModel> tokenReactivate(
			@Parameter(name = "Authorization", description = "", required = true, schema = @Schema(description = "")) @RequestHeader(value = "Authorization", required = true) String authorization,
			@Parameter(name = "CensusItemApiModel", description = "", required = true, schema = @Schema(description = "")) @Valid @RequestBody CensusItemApiModel censusItemApiModel);

	@GetMapping(value = "/token-auth/getCensusItem", produces = { "application/json" })
	ResponseEntity<GetCensusItemResponseApiModel> getCensusItem(
			@Parameter(name = "Authorization", description = "", required = true, schema = @Schema(description = "")) @RequestHeader(value = "Authorization", required = true) String authorization);

	@PostMapping(value = "/token-auth/saveValidity", produces = { "application/json" }, consumes = {
			"application/json" })
	ResponseEntity<CommonResponseApiModel> saveValidity(
			@NotNull @Parameter(name = "Authorization", description = "", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "Authorization", required = true) String authorization,
			@Parameter(name = "SaveValidityRequestApiModel", description = "", required = true) @Valid @RequestBody SaveValidityRequestApiModel saveValidityRequestApiModel);
}
