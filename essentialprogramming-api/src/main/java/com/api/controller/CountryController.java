package com.api.controller;


import com.api.config.Anonymous;
import com.api.service.CountryService;
import com.config.spring.ExecutorsProvider;
import com.exceptions.ExceptionHandler;
import com.util.async.Computation;
import com.util.enums.Language;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

@Path("/")
public class CountryController {

    private final CountryService countryService;

    @Context
    private Language language;

    @Autowired
    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GET
    @Path("countries")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "View countries", tags = {"Country",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return list of countries",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = List.class)))
            })
    @Anonymous
    public void getCountries(@Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(this::getCountries, executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable getCountries() {
        return (Serializable) countryService.getCountries();
    }

    @GET
    @Path("country/states/{countryCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "View states of a country", tags = {"State",},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Return list of states for specific country",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = List.class)))
            })
    @Anonymous
    public void getStates(@PathParam("countryCode") String countryCode, @Suspended AsyncResponse asyncResponse) {

        ExecutorService executorService = ExecutorsProvider.getExecutorService();
        Computation.computeAsync(() -> getStates(countryCode, language), executorService)
                .thenApplyAsync(json -> asyncResponse.resume(Response.ok(json).build()), executorService)
                .exceptionally(error -> asyncResponse.resume(ExceptionHandler.handleException((CompletionException) error)));

    }

    private Serializable getStates(String countryCode, Language language) {
        return (Serializable) countryService.getStates(countryCode, language);
    }
}
