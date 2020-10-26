package com.api.mapper;


import com.api.entities.State;
import com.api.output.StateJSON;

public class StateMapper {

    public static StateJSON stateToJSON(State state){
        return StateJSON.builder()
                .name(state.getName())
                .abbreviation(state.getAbbreviation())
                .country(state.getCountry().getName())
                .stateCode(state.getStateCode())
                .build();
    }
}
