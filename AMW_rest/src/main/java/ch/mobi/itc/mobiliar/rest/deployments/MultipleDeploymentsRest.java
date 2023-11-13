package ch.mobi.itc.mobiliar.rest.deployments;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Stateless
@Path("/multiple-deployments")
@Api(value = "/multiple-deployments", description = "Managing multiple deployments")
public class MultipleDeploymentsRest {

    @POST
    @ApiOperation(value = "adds multiple deployments")
    public Response addMultipleDeployments(MultipleDeploymentsRequest request) {
        return null;
    }
}
