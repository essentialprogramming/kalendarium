package com.api.service;

import com.api.entities.Country;
import com.api.entities.State;
import com.api.mapper.CountryMapper;
import com.api.mapper.StateMapper;
import com.api.output.CountryJSON;
import com.api.output.StateJSON;
import com.api.repository.CountryRepository;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private CountryRepository countryRepository;

    @Autowired
    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    @Transactional
    public List<CountryJSON> getCountries() {
        return countryRepository.findAll().stream().map(CountryMapper::countryToJSON).collect(Collectors.toList());
    }

    @Transactional
    public List<StateJSON> getStates(String countryCode, Language language){
        Country country = countryRepository.findByCountryCode(countryCode).orElseThrow(
                () -> new ApiException(Messages.get("COUNTRY.NOT.FOUND", language), HTTPCustomStatus.BUSINESS_EXCEPTION)
        );

        List<State> states = country.getStates();

        if(states == null)
            throw  new ApiException(Messages.get("COUNTRY.STATES.NOT.EXIST", language), HTTPCustomStatus.BUSINESS_EXCEPTION);

        return states.stream().map(StateMapper::stateToJSON)
                .collect(Collectors.toList());
    }

}
