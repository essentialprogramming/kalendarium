package com.api.entities;

import com.api.entities.enums.AppointmentStatus;
import com.api.entities.enums.Day;
import lombok.*;

import javax.persistence.*;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "appointment")
@Table(name = "appointment")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointmentid", nullable = false, unique = true)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "businessid")
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "businessunitid")
    private BusinessUnit businessUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="businessserviceid")
    private BusinessService businessService;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="userid")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscriptionid")
    private Subscription subscription;

    @Column(name = "status")
    private AppointmentStatus status;

    @Column(name = "appointmentcode")
    private String appointmentCode;

    @Column(name = "endtime")
    private LocalTime endTime;

    @Column(name = "starttime")
    private LocalTime startTime;

    @Column(name = "day")
    private Day day;

}
