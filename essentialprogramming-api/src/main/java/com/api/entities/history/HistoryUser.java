package com.api.entities.history;


import com.api.entities.Language;
import com.api.entities.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "HistoryUser")
@Table(name = "historyuser")
public class HistoryUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historyuserid", nullable = false, unique = true)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userid")
    private User user;

    @Column(name = "username")
    private String userName;

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

    @Column(name = "active")
    private boolean active;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "createddate")
    private LocalDateTime createdDate;

    @Column(name = "createdby")
    private int createdBy;


    public HistoryUser(User user, String userName, String firstName, String lastName, String email, String phone, Language language, boolean validated, String userKey, boolean active, boolean deleted, LocalDateTime createdDate, int createdBy) {
        this.user = user;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.language = language;
        this.validated = validated;
        this.userKey = userKey;
        this.active = active;
        this.deleted = deleted;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
    }
}

