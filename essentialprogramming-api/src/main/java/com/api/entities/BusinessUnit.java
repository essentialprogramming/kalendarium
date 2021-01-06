package com.api.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "businessunit")
@Table(name = "businessunit")
public class BusinessUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessunitid", nullable = false, unique = true)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "businessid")
    private Business business;

    @OneToMany (mappedBy = "businessUnit", fetch = FetchType.EAGER)
    private List<Appointment> appointments;

    @Column(name = "businessunitcode")
    private String businessUnitCode;

    @Column(name = "name")
    private String name;

    @ManyToMany
    @JoinTable(
            name = "businessservicebusinessunit",
            joinColumns = @JoinColumn(name = "businessunitid"),
            inverseJoinColumns = @JoinColumn(name = "businessserviceid"))
    List<BusinessService> servicesPerformedByUnit;
}
