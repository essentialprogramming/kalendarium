package com.api.mapper;

import com.api.entities.Address;
import com.api.entities.AddressHistory;
import com.api.input.BusinessInput;

import java.time.LocalDateTime;


public class AddressMapper {

    public static Address inputToAddress(BusinessInput businessInput) { ;
        return Address.builder()
                .city(businessInput.getCity())
                .street(businessInput.getAddress())
                .zip(businessInput.getZip())
                .build();
    }


    public static AddressHistory addressToHistory(Address address, LocalDateTime createDate) {
        return AddressHistory.builder()
                .city(address.getCity())
                .street(address.getStreet())
                .zip(address.getZip())
                .address(address)
                .createdDate(createDate)
                .build();
    }

    public static void setAddressInfo(Address address, BusinessInput businessInput){
        address.setCity(businessInput.getCity());
        address.setStreet(businessInput.getAddress());
        address.setZip(businessInput.getZip());
    }
    

}
