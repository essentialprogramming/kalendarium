package com.api.entities;

import com.api.entities.history.AddressHistory;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "address")
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "addressid", nullable = false, unique = true)
    private int id;

    @Column(name = "street")
    private String street;

    @Column(name = "city")
    private String city;

    @Column(name = "zip")
    private String zip;

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private List<AddressHistory> addressHistories;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historyaddressid")
    private AddressHistory latestAddressHistory;

    @Column(name = "active")
    private boolean active;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "createddate")
    private LocalDateTime createdDate;

    @Column(name = "modifieddate")
    private LocalDateTime modifiedDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "createdby")
    private User createdBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modifiedby")
    private User modifiedBy;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "countryid")
    private Country country;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stateid")
    private State state;

    public void addAddressHistory(AddressHistory addressHistory) {
        if (addressHistories == null) {
            addressHistories = new ArrayList<>();
        }

        addressHistories.add(addressHistory);
    }
}
