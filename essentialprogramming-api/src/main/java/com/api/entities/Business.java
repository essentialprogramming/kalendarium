package com.api.entities;

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
@Entity(name = "business")
@Table(name = "business")
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "businessid", nullable = false, unique = true)
    private int id;

    @Column(name = "name")
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "addressid")
    private Address address;

    @Column(name = "phone")
    private String phone;

    @Column(name = "email")
    private String email;


    @Column(name = "businesscode")
    private String businessCode;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "business")
    private List<BusinessHistory> businessHistories;
    
    @OneToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "historybusinessid")
    private BusinessHistory latestBusinessHistory;

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

    @Column(name = "validated")
    private boolean validated;

    @OneToMany(mappedBy = "business", fetch = FetchType.LAZY)
    private List<BusinessUsers> managers;




    public void addBusinessHistory(BusinessHistory businessHistory) {
        if (businessHistories == null) {
            businessHistories = new ArrayList<>();
        }

        businessHistories.add(businessHistory);
    }

}
