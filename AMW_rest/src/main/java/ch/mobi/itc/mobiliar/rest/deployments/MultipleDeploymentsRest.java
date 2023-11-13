package ch.mobi.itc.mobiliar.rest.deployments;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.ejb.Stateless;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Stateless
@Path("/multiple-deployments")
@Api(value = "/multiple-deployments", description = "Managing multiple deployments")
public class MultipleDeploymentsRest {

    @POST
    @ApiOperation(value = "adds multiple deployments")
    public Response addMultipleDeployments(MultipleDeploymentsRequest request) {
        if (request.getAppServerNames() == null) return Response.status(Status.BAD_REQUEST).build();
        if (request.getAppServerNames().isEmpty()) return Response.status(Status.BAD_REQUEST).build();
        return Response.status(Status.CREATED).build();
    }
}
