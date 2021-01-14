package com.identityprovider.entities;


import com.util.jpa.StringListConverter;
import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "account")
@Table(name = "user", schema = "public")
public class Account {

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
    private LocalDateTime cratedDate;

    @Column(name = "modifiedby")
    private Integer modifiedBy;

    @Column(name = "createdby")
    private Integer createdBy;

    @Column(name = "password")
    private String password;

    @Convert(converter = StringListConverter.class)
    @Column(name = "roles")
    private List<String> roles = new ArrayList<>();


    public Account( String email,  String userKey,String firstName, String lastName,  String phone) {
        this.email = email;
        this.userKey = userKey;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFullName() {
        return Optional.ofNullable(firstName).orElse("") + " " + Optional.ofNullable(lastName).orElse("");
    }
}
