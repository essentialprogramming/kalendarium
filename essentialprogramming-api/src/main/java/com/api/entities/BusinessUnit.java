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

    @OneToMany (mappedBy = "businessunit", fetch = FetchType.LAZY)
    private List<Appointment> appointments;


}