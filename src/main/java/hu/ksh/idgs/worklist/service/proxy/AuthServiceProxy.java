package hu.ksh.idgs.worklist.service.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import hu.ksh.idgs.auth.api.model.ActiveOsapCodeResponseApiModel;
import hu.ksh.idgs.auth.api.model.GetUserIdByNameResponseApiModel;
import hu.ksh.idgs.auth.api.model.GetUserIdsResponseApiModel;
import hu.ksh.idgs.auth.api.model.UserAddRequestApiModel;
import hu.ksh.idgs.auth.api.model.UserAddResponseApiModel;
import hu.ksh.idgs.auth.api.model.UserModifyRequestApiModel;
import hu.ksh.idgs.auth.api.model.UserModifyResponseApiModel;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@FeignClient(name = "auth")
public interface AuthServiceProxy {

	@GetMapping(value = "/auth/user-list/{userName}", produces = { "application/json" })
	ResponseEntity<GetUserIdByNameResponseApiModel> getUserIdByName(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "", required = true) @PathVariable("userName") String userName);

	@PostMapping(value = "/auth/adduser", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<UserAddResponseApiModel> addUser(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Add a new user", required = true) @Valid @RequestBody UserAddRequestApiModel userAddRequestApiModel);

	@PostMapping(value = "/auth/modifyuser", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<UserModifyResponseApiModel> modifyUser(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) final String authorization,
			@ApiParam(value = "User modify success", required = true) @Valid @RequestBody final UserModifyRequestApiModel userModifyRequestApiModel);

	@GetMapping(value = "/auth/getUserIds", produces = { "application/json" })
	ResponseEntity<GetUserIdsResponseApiModel> getUserIds(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@Size(min = 1, max = 25000) @ApiParam(value = "") @Valid @RequestParam(value = "userNames", required = false) List<String> userNames);

	@GetMapping(value = "/auth/activeOsapCodes", produces = { "application/json" })
	ResponseEntity<ActiveOsapCodeResponseApiModel> getActiveOsapCodes(
			@NotNull @Parameter(name = "Authorization", description = "", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "Authorization", required = true) String authorization,
			@NotNull @Size(max = 36) @Parameter(name = "userId", description = "", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "userId", required = true) String userId);
}
