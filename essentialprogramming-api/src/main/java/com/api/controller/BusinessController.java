package com.api.controller;

import com.api.config.Anonymous;
import com.exceptions.ExceptionHandler;
import com.api.input.BusinessInput;
import com.api.output.UserJSON;
import com.api.service.BusinessService;
import com.api.service.UserService;
import com.config.spring.ExecutorsProvider;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

import static java.lang.Boolean.TRUE;

@Path("/")
public class BusinessController {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessController.class);

    @Context
    private Language language;

    private final BusinessService businessService;
    private final UserService userService;

    @Autowired
    public BusinessController(BusinessService businessService, UserService userService) {
        this.businessService = businessService;
        this.userService = userService;
    }

    @GET
    @Path("business/all")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load all business", tags = {"Business",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns businesses.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void loadAll(@HeaderParam("Authorization") String authorization, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> businessService.loadAll(), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    @GET
    @Path("business/load")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load business", tags = {"Business",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void load(@HeaderParam("Authorization") String authorization, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> businessService.load(email, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }


    @GET
    @Path("business/load/{code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load business details", tags = {"Business",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business details.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    @Anonymous
    public void loadBusinessDetails(@PathParam("code") String code, @DefaultValue("0") @QueryParam("v") int version, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> businessService.loadDetails(code, language, version), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    @POST
    @Path("business/create")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create business", tags = {"Business",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void create(@HeaderParam("Authorization") String authorization, BusinessInput businessInput, @Suspended AsyncResponse asyncResponse) {
        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> create(email, businessInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable create(String email, BusinessInput businessInput) throws ApiException {
        try {
            UserJSON user = userService.loadUser(email, language);
            businessService.save(email, businessInput, language);
            businessService.sendConfirmationEmail(user.getUserKey(), language);
            return TRUE;
        } catch (ApiException e) {
            LOG.error("An error occurred while saving a new business.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("An error occurred while saving a new business.", e);
            throw new ApiException(Messages.get("BUSINESS.NOT.STORED", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
    }

    @POST
    @Path("business/update")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update business", tags = {"Business",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business if successfully updated.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void update(@HeaderParam("Authorization") String authorization, BusinessInput businessInput, @Suspended AsyncResponse asyncResponse) {
        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> update(email, businessInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable update(String email, BusinessInput businessInput) throws ApiException {
        businessService.update(email, businessInput, language);
        return TRUE;
    }


}
