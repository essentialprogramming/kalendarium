package com.api.entities;


import com.api.entities.enums.Day;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalTime;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "servicedetail")
@Table(name = "servicedetail")
@DynamicUpdate
public class ServiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "servicedetailid", nullable = false, unique = true)
    private int id;

    @Column(name = "endtime")
    private LocalTime endTime;

    @Column(name = "starttime")
    private LocalTime startTime;

    @Column(name = "duration")
    private long duration;

    @ElementCollection(targetClass = Day.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "businessserviceday",
            joinColumns = @JoinColumn(name = "businessserviceid"))
    private List<Day> day;

}
