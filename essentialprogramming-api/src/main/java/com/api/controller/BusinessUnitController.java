package com.api.controller;


import com.api.entities.User;
import com.api.input.BusinessServiceInput;
import com.api.input.BusinessUnitInput;
import com.api.output.UserJSON;
import com.api.service.BusinessUnitService;
import com.api.service.UserService;
import com.config.spring.ExecutorsProvider;
import com.exceptions.ExceptionHandler;
import com.internationalization.Messages;
import com.token.validation.auth.AuthUtils;
import com.util.async.Computation;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import static java.lang.Boolean.TRUE;

@Path("/")
public class BusinessUnitController {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessServiceController.class);
    @Context
    private Language language;

    private final UserService userService;
    private final BusinessUnitService businessUnitService;

    @Autowired
    public BusinessUnitController(UserService userService, BusinessUnitService businessUnitService) {
        this.userService = userService;
        this.businessUnitService = businessUnitService;
    }


    @POST
    @Path("business-unit/create")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create Business Unit", tags = {"BusinessUnit",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void create(@HeaderParam("Authorization") String authorization, BusinessUnitInput businessUnitInput, @DefaultValue("0") @QueryParam("v") int version, @Suspended AsyncResponse asyncResponse) {
        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> create(email, businessUnitInput, version), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable create(String email, BusinessUnitInput businessUnitInput, int version) throws ApiException {
        try {
            UserJSON user = userService.loadUser(email, language);
            businessUnitService.save(user.getEmail(),businessUnitInput,language,version);
            return TRUE;
        } catch (ApiException e) {
            LOG.error("An error occurred while saving a new business unit.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("An error occurred while saving a new business unit.", e);
            throw new ApiException(Messages.get("BUSINESS.UNIT.NOT.STORED", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
    }

    @GET
    @Path("business-unit/load/{business-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Business Unit for business", tags = {"BusinessUnit",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business units.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void load(@PathParam("business-code") String businessCode, @DefaultValue("0") @QueryParam("v") int version, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> businessUnitService.load(businessCode, language, version), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }


    @POST
    @Path("business-unit/update/{business-unit-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update Business Unit", tags = {"BusinessUnit",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business unit if successfully updated.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void update(@PathParam("business-unit-code") String businessUnitCode, @DefaultValue("0") @QueryParam("v") int version, BusinessUnitInput businessUnitInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> update(businessUnitCode, businessUnitInput, version), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable update(String businessServiceCode, BusinessUnitInput businessUnitInput, int version) throws ApiException, GeneralSecurityException {
        businessUnitService.update(businessServiceCode, businessUnitInput, language, version);
        return TRUE;
    }

    @DELETE
    @Path("business-unit/delete/{business-unit-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete Business Unit", tags = {"BusinessUnit",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns if business unit was successfully deleted.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void delete(@PathParam("business-unit-code") String businessUnitCode,  @DefaultValue("0") @QueryParam("v") int version,  @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> delete(businessUnitCode, version), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable delete(String businessUnitCode, int version) throws ApiException, GeneralSecurityException {
        businessUnitService.delete(businessUnitCode, language, version);
        return TRUE;
    }




}
