package com.authentication.controller;

import com.authentication.channel.AccessChannel;
import com.identityprovider.model.PasswordInput;
import com.authentication.request.AuthRequest;
import com.authentication.response.AccessToken;
import com.authentication.service.AuthenticationService;
import com.config.spring.ExecutorsProvider;
import com.exceptions.ExceptionHandler;
import com.exceptions.PasswordException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.util.async.Computation;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import com.web.json.JsonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
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
public class AuthenticationController {

    @Context
    private HttpServletRequest httpRequest;

    @Context
    private Language language;

    private final AuthenticationService authenticationService;


    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @POST
    @Path("token")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Authenticate user, return JWT token.", tags = {"Authorization",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns JWT token if user successfully authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccessToken.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JsonResponse.class))),
            })
    public void authenticate(AuthRequest tokenRequest, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> authenticate(tokenRequest), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private AccessToken authenticate(AuthRequest authRequest) throws GeneralSecurityException, JsonProcessingException, ApiException {
        return authenticationService.authenticate(authRequest, AccessChannel.PASSWORD, language);
    }


    @POST
    @Path("set_password")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Set password", tags = {"Authorization",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns ok if password was set successfully. ",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JsonResponse.class)))
            })
    public void setPassword(PasswordInput passwordInput, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> setPassword(passwordInput), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }


    private Serializable setPassword(PasswordInput passwordInput) throws GeneralSecurityException, ApiException, PasswordException {
        return authenticationService.setPassword(passwordInput, language);
    }


    @POST
    @Path("otp")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Authenticate with otp, return JWT token.", tags = {"Authorization",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns JWT token if user successfully authenticated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccessToken.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JsonResponse.class))),
            })
    public void otpLogin(AuthRequest tokenRequest, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> otpLogin(tokenRequest), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private AccessToken otpLogin(AuthRequest authRequest) throws GeneralSecurityException, JsonProcessingException, ApiException {
        return authenticationService.authenticate(authRequest, AccessChannel.OTP, language);
    }

    @POST
    @Path("otp/generate")
    @Consumes("application/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Generate OTP", tags = {"Authorization",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returns 'status ok' if successfully generated",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = AccessToken.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = JsonResponse.class))),
            })
    public void generateOtp(@QueryParam("email") String email, @QueryParam("platform") String platform, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> generateOtp(email, language, platform), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable generateOtp(String email, Language language, String platform) throws ApiException {
        return authenticationService.generateOtp(email, language, platform);
    }
}
