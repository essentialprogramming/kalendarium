package com.api.mapper;


import com.api.entities.HistoryUser;
import com.api.output.HistoryUserJSON;

public class HistoryUserMapper {

    public static HistoryUserJSON historyUserToJSON(HistoryUser historyUser) {

        return HistoryUserJSON.builder()
                .userName(historyUser.getUserName())
                .firstName(historyUser.getFirstName())
                .lastName(historyUser.getLastName())
                .email(historyUser.getEmail())
                .phone(historyUser.getPhone())
                .active(historyUser.isActive())
                .defaultLanguageId(historyUser.getLanguage().getId())
                .deleted(historyUser.isDeleted())
                .createdBy(historyUser.getCreatedBy())
                .createdDate(historyUser.getCreatedDate())
                .userKey(historyUser.getUserKey())
                .validated(historyUser.isValidated())
                .build();
    }
}
