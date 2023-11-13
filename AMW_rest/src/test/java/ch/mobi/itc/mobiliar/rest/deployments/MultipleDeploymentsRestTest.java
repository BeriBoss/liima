package ch.mobi.itc.mobiliar.rest.deployments;

import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class MultipleDeploymentsRestTest {

    MultipleDeploymentsRest multipleDeploymentsRest = new MultipleDeploymentsRest();
    private MultipleDeploymentsRequest request;

    @Before
    public void setup() {
        request = new MultipleDeploymentsRequest();
    }

    @Test
    public void shouldRejectMissingAppServerName() {
        Response response = multipleDeploymentsRest.addMultipleDeployments(request);
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldRejectEmptyAppServerName() {
        request.setAppServerNames(Collections.emptyList());
        Response response = multipleDeploymentsRest.addMultipleDeployments(request);
        assertNotNull(response);
        assertEquals(400, response.getStatus());
    }

    @Test
    public void shouldCreateMultipleDeployments() {
        request.setAppServerNames(List.of("AppServer1", "AppServer2"));
        Response response = multipleDeploymentsRest.addMultipleDeployments(request);
        assertNotNull(response);
        assertEquals(201, response.getStatus());
    }
}