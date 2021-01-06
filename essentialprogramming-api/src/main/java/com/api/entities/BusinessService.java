package com.api.entities;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "businessservice")
@Table(name = "businessservice")
@DynamicUpdate
public class BusinessService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessserviceid", nullable = false, unique = true)
    private int id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "servicedetailid")
    private ServiceDetail serviceDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "businessid")
    private Business business;

    @OneToMany(mappedBy = "businessService" ,fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "businessService" ,fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;

    @Column(name = "businessservicecode")
    private String businessServiceCode;

    @Column(name = "name")
    private String name;

    @ManyToMany(fetch = FetchType.LAZY )
    @JoinTable(
            name = "businessservicebusinessunit",
            joinColumns = @JoinColumn(name = "businessserviceid"),
            inverseJoinColumns = @JoinColumn(name = "businessunitid"))
    List<BusinessUnit> unitsPerformingService;

    public void addBusinessUnit(BusinessUnit businessUnit) {
        this.unitsPerformingService.add(businessUnit);
    }
}
