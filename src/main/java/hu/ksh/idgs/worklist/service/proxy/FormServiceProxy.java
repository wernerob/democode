package hu.ksh.idgs.worklist.service.proxy;

import java.util.UUID;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import hu.ksh.idgs.form.api.model.CodeTableItemApiModel;
import hu.ksh.idgs.form.api.model.FormDataArrivalRequestApiModel;
import hu.ksh.idgs.form.api.model.FormDataArrivalResponseApiModel;
import hu.ksh.idgs.form.api.model.FormLoadRequestApiModel;
import hu.ksh.idgs.form.api.model.FormLoadResponseApiModel;
import hu.ksh.idgs.form.api.model.FormSettingResponseApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateIdRequestApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateIdResponseApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateInfoApiModel;
import hu.ksh.idgs.form.api.model.FormTemplateInfoRequestApiModel;
import hu.ksh.idgs.form.api.model.FormTemplatesResponseApiModel;
import hu.ksh.idgs.form.api.model.GetCodeTableItemRequestApiModel;
import hu.ksh.idgs.form.api.model.GetGeoLocationsRequestApiModel;
import hu.ksh.idgs.form.api.model.GetGeoLocationsResponseApiModel;
import hu.ksh.idgs.form.api.model.LanguageApiModel;
import hu.ksh.idgs.form.api.model.PrefillSaveRequestApiModel;
import hu.ksh.idgs.form.api.model.PrefillSaveResponseApiModel;
import hu.ksh.idgs.form.api.model.SaveGeoLocationRequestApiModel;
import hu.ksh.idgs.form.api.model.SaveGeoLocationResponseApiModel;
import hu.ksh.idgs.form.api.model.SettingTypeApiModel;
import hu.ksh.idgs.form.api.model.VisitCodeItemsByFormTemlateRequestApiModel;
import hu.ksh.idgs.form.api.model.VisitCodeItemsByFormTemlateResponseApiModel;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@FeignClient(name = "form", url = "${feign.client.form.baseUrl}")
public interface FormServiceProxy {

	@PostMapping(value = "/form/formTemplateId", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<FormTemplateIdResponseApiModel> getFormTemplateId(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Form template id filter", required = true) @Valid @RequestBody FormTemplateIdRequestApiModel formTemplateIdRequestApiModel);

	@PostMapping(value = "/form/templateInfo", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<FormTemplateInfoApiModel> getFormTemplateInfo(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) final String authorization,
			@ApiParam(value = "Form template info request", required = true) @Valid @RequestBody final FormTemplateInfoRequestApiModel formTemplateInfoRequestApiModel);

	@PostMapping(value = "/form/visitCodeItemsByFormTemlate", produces = { "application/json" }, consumes = {
			"application/json" })
	ResponseEntity<VisitCodeItemsByFormTemlateResponseApiModel> getVisitCodeTableItemsByFormTemplate(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Visit codetable item request", required = true) @Valid @RequestBody VisitCodeItemsByFormTemlateRequestApiModel visitCodeItemsByFormTemlateRequestApiModel);

	@PostMapping(value = "/form/saveGeoLocation", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<SaveGeoLocationResponseApiModel> saveGeolocation(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) final String authorization,
			@ApiParam(value = "Save GeoLocation request", required = true) @Valid @RequestBody final SaveGeoLocationRequestApiModel saveGeoLocationRequestApiModel);

	@PostMapping(value = "/form/getCodeTableItem", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<CodeTableItemApiModel> getCodeTableItem(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Get codetable item request", required = true) @Valid @RequestBody GetCodeTableItemRequestApiModel getCodeTableItemRequestApiModel);

	@PostMapping(value = "/form/dataArrival", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<FormDataArrivalResponseApiModel> getFormDataArrival(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Form statistics request", required = true) @Valid @RequestBody FormDataArrivalRequestApiModel formDataArrivalRequestApiModel);

	@GetMapping(value = "/form/getFormSetting", produces = { "application/json" })
	ResponseEntity<FormSettingResponseApiModel> getFormSetting(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@NotNull @ApiParam(value = "", required = true) @Valid @RequestParam(value = "formTemplateId", required = true) UUID formTemplateId,
			@NotNull @ApiParam(value = "", required = true, allowableValues = "ENABLE_ADD_ADDRESS, ENABLE_ADD_HOUSEHOLD") @Valid @RequestParam(value = "settingType", required = true) SettingTypeApiModel settingType);

	@PostMapping(value = "/form/load", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<FormLoadResponseApiModel> loadForm(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Retrieve saved form", required = true) @Valid @RequestBody FormLoadRequestApiModel formLoadRequestApiModel);

	@PostMapping(value = "/form/getGeoLocations", produces = { "application/json" }, consumes = { "application/json" })
	ResponseEntity<GetGeoLocationsResponseApiModel> getGeoLocations(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "", required = true) @Valid @RequestBody GetGeoLocationsRequestApiModel getGeoLocationsRequestApiModel);

	@GetMapping(value = "/form/formTemplates", produces = { "application/json" })
	ResponseEntity<FormTemplatesResponseApiModel> getFormTemplates(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@NotNull @ApiParam(value = "", required = true, allowableValues = "HU, EN") @Valid @RequestParam(value = "lang", required = true) LanguageApiModel lang,
			@ApiParam(value = "") @Valid @RequestParam(value = "active", required = false) Boolean active,
			@ApiParam(value = "") @Valid @RequestParam(value = "valid", required = false) Boolean valid);

	@GetMapping(value = "/form/templatePeriodInfo", produces = { "application/json" })
	ResponseEntity<FormTemplateInfoApiModel> templatePeriodInfo(
			@NotNull @Parameter(name = "Authorization", description = "", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "Authorization", required = true) String authorization,
			@NotNull @Size(min = 4, max = 4) @Parameter(name = "osap", description = "", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "osap", required = true) String osap,
			@NotNull @Min(2023) @Max(2100) @Parameter(name = "year", description = "", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "year", required = true) Integer year,
			@NotNull @Size(min = 1, max = 2) @Parameter(name = "period", description = "", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "period", required = true) String period,
			@NotNull @Parameter(name = "lang", description = "", required = true, in = ParameterIn.QUERY) @Valid @RequestParam(value = "lang", required = true) LanguageApiModel lang,
			@Size(max = 36) @Parameter(name = "role", description = "", in = ParameterIn.QUERY) @Valid @RequestParam(value = "role", required = false) String role);

	@PostMapping(value = "/form/independentPrefillSave", produces = { "application/json" }, consumes = {
			"application/json" })
	ResponseEntity<PrefillSaveResponseApiModel> independentPrefillSave(
			@NotNull @Parameter(name = "Authorization", description = "", required = true, in = ParameterIn.HEADER) @RequestHeader(value = "Authorization", required = true) final String authorization,
			@Parameter(name = "PrefillSaveRequestApiModel", description = "Prefill save data request", required = true) @Valid @RequestBody final PrefillSaveRequestApiModel prefillSaveRequestApiModel);

}
