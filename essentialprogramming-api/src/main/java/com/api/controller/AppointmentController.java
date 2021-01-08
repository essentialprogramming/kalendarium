package com.api.controller;

import com.api.input.AppointmentInput;
import com.api.input.BusinessServiceInput;
import com.api.input.BusinessUnitInput;
import com.api.output.UserJSON;
import com.api.service.AppointmentService;
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
public class AppointmentController {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessServiceController.class);
    @Context
    private Language language;

    private final UserService userService;
    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(UserService userService, AppointmentService appointmentService) {
        this.userService = userService;
        this.appointmentService = appointmentService;
    }

    @POST
    @Path("appointment/create")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create Appointment", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void create(@HeaderParam("Authorization") String authorization, AppointmentInput appointmentInput, @Suspended AsyncResponse asyncResponse) {
        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> create(email, appointmentInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable create(String email, AppointmentInput appointmentInput) throws ApiException {
        try {
            UserJSON user = userService.loadUser(email, language);
            return appointmentService.save(email, appointmentInput, language);
        } catch (ApiException e) {
            LOG.error("An error occurred while saving a new appointment.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("An error occurred while saving a new appointment.", e);
            throw new ApiException(Messages.get("APPOINTMENT.NOT.STORED", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
    }

    @POST
    @Path("appointment/updateStatus/{appointmentCode}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update Status for Appointment", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void updateStatus(@PathParam("appointmentCode") String appointmentCode, @Suspended AsyncResponse asyncResponse) throws GeneralSecurityException {
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> updateStatus(appointmentCode), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable updateStatus(String appointmentCode) throws GeneralSecurityException {
        return appointmentService.updateStatus(appointmentCode, language);
    }

    @GET
    @Path("appointment/load/user/{user-key}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Appointments for User", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns appointments.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void loadByUser(@PathParam("user-key") String userKey, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> appointmentService.loadByUser(userKey, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    @GET
    @Path("appointment/load/business/{business-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Appointments for Business", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns appointments.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void loadByBusiness(@PathParam("business-code") String businessCode, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> appointmentService.loadByBusiness(businessCode, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    @GET
    @Path("appointment/load/businessService/{business-service-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Appointments for Business Service", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns appointments.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void loadByBusinessService(@PathParam("business-service-code") String businessServiceCode, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> appointmentService.loadByBusinessService(businessServiceCode, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    @GET
    @Path("appointment/load/businessUnit/{business-unit-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Appointments for Business Unit", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns appointments.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void loadByBusinessUnit(@PathParam("business-unit-code") String businessUnitCode, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> appointmentService.loadByBusinessUnit(businessUnitCode, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    @POST
    @Path("appointment/update/{appointment-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update Appointment", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns appointment if successfully updated.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void update(@PathParam("appointment-code") String appointmentCode, AppointmentInput appointmentInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> update(appointmentCode, appointmentInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable update(String appointmentCode, AppointmentInput appointmentInput) throws ApiException, GeneralSecurityException {
        appointmentService.update(appointmentCode, appointmentInput, language);
        return TRUE;
    }

    @DELETE
    @Path("appointment/delete/{appointment-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete Appointment", tags = {"Appointment",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns if appointment was successfully deleted.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void delete(@PathParam("appointment-code") String appointmentCode,  @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> delete(appointmentCode), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable delete(String appointmentCode) throws ApiException, GeneralSecurityException {
        appointmentService.delete(appointmentCode, language);
        return TRUE;
    }
}
