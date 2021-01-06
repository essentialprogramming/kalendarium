package com.api.controller;

import com.api.input.BusinessServiceInput;
import com.api.input.BusinessServiceScheduleInput;
import com.api.input.BusinessServiceUpdateInput;
import com.api.input.BusinessUnitServiceInput;
import com.api.service.BusinessServiceService;
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
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static java.lang.Boolean.TRUE;

@Path("/")
public class BusinessServiceController {
    private static final Logger LOG = LoggerFactory.getLogger(BusinessServiceController.class);

    @Context
    private Language language;

    private final UserService userService;
    private final BusinessServiceService businessServiceService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("h:mm a");


    @Autowired
    public BusinessServiceController(BusinessServiceService businessServiceService, UserService userService) {
        this.businessServiceService = businessServiceService;
        this.userService = userService;
    }

    @POST
    @Path("business-service/schedule")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create Business Service", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void getSchedule(BusinessServiceScheduleInput businessServiceInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> getSchedule(businessServiceInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }


    private List<String> getSchedule(BusinessServiceScheduleInput businessServiceInput) throws ApiException {
        try {
            Set<LocalTime> times = businessServiceService.getSchedule(businessServiceInput, language);
            List<String> timeFormatted = times.stream()
                    .map(time -> time.format(FORMATTER))
                    .collect(Collectors.toList());

            return timeFormatted;
        } catch (ApiException e) {
            LOG.error("An error occurred while retrieving business service schedule.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("An error occurred while retrieving business service schedule.", e);
            throw new ApiException(Messages.get("", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
    }


    @POST
    @Path("business-service/create")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create Business Service", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void create(@HeaderParam("Authorization") String authorization, BusinessServiceInput businessServiceInput, @Suspended AsyncResponse asyncResponse) {
        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> create(email, businessServiceInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable create(String email, BusinessServiceInput businessServiceInput) throws ApiException {
        try {
            businessServiceService.save(email, businessServiceInput, language);
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
    @Path("business-service/add/business-service")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add Business Unit to Business Service", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void addBusinessUnit(BusinessUnitServiceInput businessServiceInput, @Suspended AsyncResponse asyncResponse) {
        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> addBusinessUnit(businessServiceInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable addBusinessUnit(BusinessUnitServiceInput businessServiceInput) throws ApiException {
        try {
            businessServiceService.addBusinessUnit(businessServiceInput, language);
            return TRUE;
        } catch (ApiException e) {
            LOG.error("An error occurred while saving a new business.", e);
            throw e;
        } catch (Exception e) {
            LOG.error("An error occurred while saving a new business.", e);
            throw new ApiException(Messages.get("BUSINESS.NOT.STORED", language), HTTPCustomStatus.BUSINESS_EXCEPTION);
        }
    }

    @GET
    @Path("business-service/load/{business-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Business Service for business", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business services.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void load(@PathParam("business-code") String businessCode, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> businessServiceService.load(businessCode, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }



    @POST
    @Path("business-service/update/{business-service-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update Business Service", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business service if successfully updated.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void update(@PathParam("business-service-code") String businessServiceCode, BusinessServiceUpdateInput businessServiceInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> update(businessServiceCode, businessServiceInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable update(String businessServiceCode, BusinessServiceUpdateInput businessServiceInput) throws ApiException, GeneralSecurityException {
        businessServiceService.update(businessServiceCode, businessServiceInput, language);
        return TRUE;
    }

    @DELETE
    @Path("business-service/delete/{business-service-code}")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Delete Business Service", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns if business service was successfully deleted.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void delete(@PathParam("business-service-code") String businessServiceCode,  @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> delete(businessServiceCode), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable delete(String businessServiceCode) throws ApiException, GeneralSecurityException {
        businessServiceService.delete(businessServiceCode, language);
        return TRUE;
    }

    @POST
    @Path("business-service/addEmployee")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Add employee", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns auth key if successfully added.",
                            content = @Content(mediaType = "application/json"
                            ))
            })
    public void addEmployee(@HeaderParam("Authorization") String authorization, @RequestBody String employeeEmail, @Suspended AsyncResponse asyncResponse) {
        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> createEmployee(email, employeeEmail), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }

    private Serializable createEmployee(String email, String employeeEmail) {
        businessServiceService.addEmployee(email, employeeEmail);
        return TRUE;
    }

    @GET
    @Path("business-service/loadAllEmployees")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load Users by business", tags = {"BusinessService",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns business services.",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)))
            })
    public void loadUsersByBusiness(@HeaderParam("Authorization") String authorization, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> businessServiceService.getAllEmployeesForBusiness(email), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));
    }
}
