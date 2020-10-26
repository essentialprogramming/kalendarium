package com.api.mapper;


import com.api.entities.Country;
import com.api.entities.State;
import com.api.output.CountryJSON;

import java.util.ArrayList;
import java.util.stream.Collectors;


public class CountryMapper {

    public static CountryJSON countryToJSON(Country country) {
        return CountryJSON.builder()
                .name(country.getName())
                .defaultLocale(country.getDefaultLocale())
                .displayOrder(country.getDisplayOrder())
                .countryCode(country.getCountryCode())
                .states(country.getStates() != null ? country.getStates().stream().map(State::getName).collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
