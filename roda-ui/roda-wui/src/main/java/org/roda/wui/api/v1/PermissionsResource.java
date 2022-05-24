/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.api.v1;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.server.JSONP;
import org.roda.core.common.UserUtility;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.RODAException;
import org.roda.core.data.v2.common.ObjectPermissionResult;
import org.roda.core.data.v2.user.User;
import org.roda.wui.api.controllers.Browser;
import org.roda.wui.api.v1.utils.ApiResponseMessage;
import org.roda.wui.api.v1.utils.ExtraMediaType;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Path(PermissionsResource.ENDPOINT)
@Tag(name = PermissionsResource.SWAGGER_ENDPOINT)
public class PermissionsResource {
  public static final String ENDPOINT = "/v1/permissions";
  public static final String SWAGGER_ENDPOINT = "v1 permissions";

  @Context
  private HttpServletRequest request;

  @GET
  @Path("/{" + RodaConstants.API_PATH_PARAM_USERNAME + "}/{" + RodaConstants.API_PATH_PARAM_PERMISSION_TYPE + "}")
  @Produces({MediaType.APPLICATION_JSON, ExtraMediaType.APPLICATION_JAVASCRIPT})
  @JSONP(callback = RodaConstants.API_QUERY_DEFAULT_JSONP_CALLBACK, queryParam = RodaConstants.API_QUERY_KEY_JSONP_CALLBACK)
  @Operation(summary = "Test permissions", description = "Test if user has permissions", responses = {
    @ApiResponse(responseCode = "200", description = "OK", content = @Content(schema = @Schema(implementation = ObjectPermissionResult.class))),
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ApiResponseMessage.class)))})
  public Response getPermissions(
    @Parameter(description = "The username to test permission.", required = true) @PathParam(RodaConstants.API_PATH_PARAM_USERNAME) String username,
    @Parameter(description = "The permission type to test.", required = true) @PathParam(RodaConstants.API_PATH_PARAM_PERMISSION_TYPE) String permissionType,
    @Context UriInfo uriInfo,
    @Parameter(description = "JSONP callback name", required = false, schema = @Schema(defaultValue = RodaConstants.API_QUERY_DEFAULT_JSONP_CALLBACK)) @QueryParam(RodaConstants.API_QUERY_KEY_JSONP_CALLBACK) String jsonpCallbackName)
    throws RODAException {
    String mediaType = MediaType.APPLICATION_JSON + "; charset=UTF-8";
    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();

    // get user
    User user = UserUtility.getApiUser(request);

    // delegate action to controller
    ObjectPermissionResult result = Browser.verifyPermissions(user, username, permissionType, queryParams);
    return Response.ok(result, mediaType).build();
  }
}
