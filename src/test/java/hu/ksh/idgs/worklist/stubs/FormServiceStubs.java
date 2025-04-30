package hu.ksh.idgs.worklist.stubs;

import com.github.tomakehurst.wiremock.client.WireMock;
import hu.ksh.idgs.worklist.utils.TestHelper;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FormServiceStubs {

    public static void stubFor_getFormTemplateId(String _id) {
        String body = String.format(TestHelper.readFromPath("/getFormTemplateId_response.json"), _id.toString());
        stubFor(post("/form/formTemplateId")
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE,  MediaType.APPLICATION_JSON_VALUE)
                        .withBody(body)));
    }

    public static void stubFor_getFormTemplates() {
        String body = TestHelper.readFromPath("/getFormTemplates_response.json");
        stubFor(get("/form/formTemplates?lang=HU") //&active=true&valid=true
                .willReturn(WireMock.aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE,  MediaType.APPLICATION_JSON_VALUE)
                        .withStatus(200)
                        .withBody(body)));
    }
    // /form/formTemplates


}
