package com.api.entities;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "subscription")
@Table(name = "subscription" , schema = "public")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscriptionid", nullable = false, unique = true)
    private int id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "businessid")
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "businessserviceid")
    private BusinessService businessService;

    @OneToMany(mappedBy = "subscription",fetch = FetchType.LAZY)
    private List<Appointment> appointments;


}
