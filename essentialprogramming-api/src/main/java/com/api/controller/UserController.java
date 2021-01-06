package com.api.controller;

import com.api.config.Anonymous;
import com.api.output.UserJSON;
import com.api.service.UserService;
import com.api.input.*;
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

@Path("/")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Context
    private Language language;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @POST
    @Path("user/create")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create user", tags = {"User",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return user if successfully added",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserJSON.class)))
            })
    @Anonymous
    public void createUser(UserInput userInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> createUser(userInput, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable createUser(UserInput userInput, Language language) throws GeneralSecurityException, ApiException {
        boolean isValid = userService.checkAvailabilityByEmail(userInput.getEmail());
        if (isValid) {
            return userService.save(userInput, language);

        } else
            throw new ApiException(Messages.get("EMAIL.ALREADY.TAKEN", language), HTTPCustomStatus.BUSINESS_VALIDATION_ERROR);
    }

    @POST
    @Path("user/update")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update user", tags = {"User",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return user if successfully updated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserJSON.class)))
            })
    @Anonymous
    public void updateUser(UserInput userInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> updateUser(userInput, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable updateUser(UserInput userInput, Language language) throws GeneralSecurityException, ApiException {
            return userService.update(userInput, language);
    }



    @GET
    @Path("user/load")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Load user", tags = {"User",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return user if it was successfully found",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = UserJSON.class)))
            })
    public void load(@HeaderParam("Authorization") String authorization, @Suspended AsyncResponse asyncResponse) {

        final String bearer = AuthUtils.extractBearerToken(authorization);
        final String email = AuthUtils.getClaim(bearer, "email");

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> loadUser(email), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable loadUser(String email) throws ApiException {
        return userService.loadUser(email, language);
    }



}