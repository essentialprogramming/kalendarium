package com.api.entities;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "user")
@javax.persistence.Table(name = "user", schema = "public")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid", nullable = false, unique = true)
    private int id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "defaultlanguageid")
    private Language language;

    @Column(name = "validated")
    private boolean validated;

    @Column(name = "userkey")
    private String userKey;

    @Column(name = "modifieddate")
    private LocalDateTime modifiedDate;

    @Column(name = "active")
    private boolean active;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "createddate")
    private LocalDateTime createdDate;

    @Column(name = "modifiedby")
    private Integer modifiedBy;

    @Column(name = "createdby")
    private Integer createdBy;

    @Column(name = "password")
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer")
    private Business employer;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<BusinessUsers> businessUsers;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;



    public String getFullName() {
        return Optional.ofNullable(firstName).orElse("") + " " + Optional.ofNullable(lastName).orElse("");
    }

}
