package com.api.entities;


import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "businessunit")
@Table(name = "businessunit")
public class ServiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicedetailid", nullable = false, unique = true)
    private int id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "starttime")
    private LocalTime startTime;

    @Column(name = "duration")
    private long duration;


}
