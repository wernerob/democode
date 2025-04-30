package hu.ksh.idgs.worklist.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hu.ksh.idgs.worklist.api.model.*;
import hu.ksh.idgs.worklist.dto.MajaDataCollection;
import hu.ksh.idgs.worklist.model.value.SendStatusType;
import hu.ksh.idgs.worklist.stubs.FormServiceStubs;
import hu.ksh.idgs.worklist.utils.TestHelper;
import hu.ksh.maja.core.dto.MajaAuthenticationUserDto;
import hu.ksh.maja.core.security.MajaWebToken;
import hu.ksh.maja.core.security.MajaWebTokenAuthentication;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

import java.text.MessageFormat;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("build")
@AutoConfigureWireMock(port = 0)
@ExtendWith({ MockitoExtension.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WorklistApiControllerTest {


    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:13.3-alpine")
                    .withReuse(true)
                    .withDatabaseName("idgs")
                    .withInitScript("init_schema.sql")
                    .withUsername("idgs")
					.withPassword("idgs");

    static {
        postgreSQLContainer.start();
    }

    @Autowired
    MajaWebToken majaWebToken;
    @Mock
    private MajaWebTokenAuthentication auth;
    String tokenForTest = null;

    @BeforeEach
    public void initSecurityContext() {
        auth = new MajaWebTokenAuthentication(
                new MajaAuthenticationUserDto(
                        "admin",
                        "password",
                        "ROLE_ADMINISTRATOR",
                        List.of("MAJA_ADMIN_WORKLIST","ADMIN_FUNC_WRITE"),
                        OffsetDateTime.MAX,
                        false),"admin", "system");
        tokenForTest = majaWebToken.createWebToken(auth);
    }

    @LocalServerPort
    private int port;

    @BeforeEach
    void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }


/**
 * 1.Get Data to send by call /workistSend
 * ? items will be selected to process
 *  2. check if status changed to PENDING
 *  3. assume, data processed by data_transmitter ..
 *   call /worklist/setStatusesByTransmitterProcessResult
 *  4. check statuses at SENT and ERROR on items ..
 *
 **/

    @Test
    @Sql("/db-data/init-data.sql")
    @Order(1)
    void getWorkItemInfo() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        String data = TestHelper.readFromPath("forms.json");
        List<MajaDataCollection> majaDataCollectionList = objectMapper.readValue(data, new TypeReference<List<MajaDataCollection>>() { });

        String ids = majaDataCollectionList.stream().filter(ms->ms.getYear()>2023).map(mp->  mp.getId() + "', '").reduce("'", String::concat);
        String worklistIdToCheckOK = "";
        String worklistIdToCheckERR = "";
        List<UUID> uuidList = new ArrayList<>();
        for(MajaDataCollection majaDataCollection1 : majaDataCollectionList) {
            if(majaDataCollection1.getYear()>=2024){
                String query = "/worklist/workistSend?formCode=" + majaDataCollection1.getCode()
                        + "&year=" + majaDataCollection1.getYear()
                        + "&period=" + majaDataCollection1.getPeriod()
                        + "&maxResult=100";
                FormServiceStubs.stubFor_getFormTemplateId(majaDataCollection1.getId());

                var response = RestAssured.given()
                        .port(port)
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + tokenForTest)
                        .when()
                        .get(query)
                        .then()
                        .log().all();

                var responseData = response
                        .statusCode(200)
                        .extract()
                        .body().as(WorklistSendResponseApiModel.class);

                uuidList.addAll( Objects.requireNonNull(responseData).getItems()
                        .stream()
                        .map(WorklistSendItemApiModel::getWorklistId)
                        .collect(Collectors.toList() ));

                if(!responseData.getItems().isEmpty()) {
                    System.out.println(majaDataCollection1.getCode() + " " + responseData.getItems().get(0).getWorklistId().toString());
                    if(majaDataCollection1.getCode().equals("1711") && worklistIdToCheckOK.isEmpty()){
                        worklistIdToCheckOK = responseData.getItems().get(0).getWorklistId().toString();
                        worklistIdToCheckERR = responseData.getItems().get(1).getWorklistId().toString();
                    }
                }
            }

        }

        assertEquals(uuidList.size() , 394, "we must got exact count");

        //check by ID, if items current send_status is 'PENDING'
        // "a95053e0-1604-40fd-aaa4-595e021d9aac" - id for '1711' form template
        var responseDataCheck = getAdminWorklist("a95053e0-1604-40fd-aaa4-595e021d9aac");
        final String finalWorklistIdToCheckOK = worklistIdToCheckOK;
        assertTrue( responseDataCheck.getWorklistItems().stream()
                .filter(f->f.getWorklistId().equals(finalWorklistIdToCheckOK))
                .findAny().get().getSendStatus().toString().equals(SendStatusType.PENDING.toString()), "item must be in PENDING status");

        /**
        * let's assume the data-transmitter finish processing
        * collect some items to 'SUCCESS', SOME items to 'FAILED'  process list *
        */
        final String finalWorklistIdToCheckERR = worklistIdToCheckERR;
        SetStatusByDataTransmitterRequestApiModel setStatusByDataTransmitterRequestApiModel = new SetStatusByDataTransmitterRequestApiModel();
        List<NoteApiModel> noteApiModelListSuccess = uuidList.stream()
                .filter(f->!f.toString().equals(finalWorklistIdToCheckERR)).limit(40).map(uuid -> {
                NoteApiModel noteApiModel = new NoteApiModel();
                noteApiModel.setWorklistId(uuid.toString());
                noteApiModel.setNote("this is SUCCESS processed items of id:: " + uuid.toString());
                return noteApiModel;
        }).collect(Collectors.toList());

        setStatusByDataTransmitterRequestApiModel.setWorklistIdsSuccess( noteApiModelListSuccess);
        List<NoteApiModel> noteApiModelListfailed = uuidList.stream()
                .filter(f->!f.toString().equals(finalWorklistIdToCheckOK)).skip(40).limit(50).map(uuid -> {
                NoteApiModel noteApiModel = new NoteApiModel();
                noteApiModel.setWorklistId(uuid.toString());
                noteApiModel.setNote("this is FAILED processed item of id:: " + uuid.toString());
                return noteApiModel;
        }).collect(Collectors.toList());
        // add error item to
        NoteApiModel noteApiModelErr = new NoteApiModel();
        noteApiModelErr.setWorklistId(finalWorklistIdToCheckERR);
        noteApiModelErr.setNote("this is FAILED processed item of id:: " + finalWorklistIdToCheckERR);
        noteApiModelListfailed.add(noteApiModelErr);

        setStatusByDataTransmitterRequestApiModel.setWorklistIdsFailed( noteApiModelListfailed);

        String queryX = "/worklist/setStatusesByTransmitterProcessResult";

        var response2 = RestAssured.given()
                .port(port)
                .contentType("application/json")
                .header("Authorization", "Bearer " + tokenForTest)
                .body(setStatusByDataTransmitterRequestApiModel)
                .when()
                .post(queryX)
                .then()
                .log().all();

        var responseData = response2
                .statusCode(200)
                .extract()
                .body().as(ResetSendStatusResponseApiModel.class);

        assertEquals(responseData.getStatus().equals(ResultStatusApiModel.OK), true, "expect sucessfull result");

        //TODO check by ID, if successed item send_status 'SEND', failed - 'FAILED' ...

        //check by ID, if items current send_status is 'SENT' and 'ERROR' respectively
        // "a95053e0-1604-40fd-aaa4-595e021d9aac" - id for '1711' form template
        var responseDataCheckAfterReply = getAdminWorklist("a95053e0-1604-40fd-aaa4-595e021d9aac");
        assertTrue( responseDataCheckAfterReply.getWorklistItems().stream()
                .filter(f->f.getWorklistId().equals(finalWorklistIdToCheckOK)).findAny().get().getSendStatus().toString().equals(SendStatusType.SENT.toString()), "item must be in SENT status");
        assertTrue( responseDataCheckAfterReply.getWorklistItems().stream()
                .filter(f->f.getWorklistId().equals(finalWorklistIdToCheckERR)).findAny().get().getSendStatus().toString().equals(SendStatusType.ERROR.toString()), "item must be in ERR status");


    }


    /**
     * call /worklist/getAdminWorklist for list worklist_items related to form id
     * @param formTemplateId - template id to list items for ...
     *
     * */
    private AdminWorklistResponseApiModel getAdminWorklist(final String formTemplateId){

        FormServiceStubs.stubFor_getFormTemplates();
        // a95053e0-1604-40fd-aaa4-595e021d9aac
        String checkQuery = MessageFormat.format( "?lang=HU&formTemplateId={0}&search=", formTemplateId);
        var responseCheck = RestAssured.given()
                .port(port)
                .contentType("application/json")
                .header("Authorization", "Bearer " + tokenForTest)
                .when()
                .get("/worklist/getAdminWorklist"+ checkQuery)
                .then()
                .log().all();
        var responseDataCheck = responseCheck
                .statusCode(200)
                .extract()
                .body().as(AdminWorklistResponseApiModel.class);


        return responseDataCheck;
    }
}