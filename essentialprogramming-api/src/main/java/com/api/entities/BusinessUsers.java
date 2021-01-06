package com.api.entities;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "BusinessUsers")
@Table(name = "businessusers")
public class BusinessUsers {

    @EmbeddedId
    private BusinessUsersKey BusinessUserId;


    @Column(name = "createddate")
    private LocalDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("businessId")
    @JoinColumn(name = "businessid")
    private Business business;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "userid")
    private User user;

    public BusinessUsers(Business business, User user) {
        this.business = business;
        this.user = user;
        this.BusinessUserId = new BusinessUsersKey(business.getId(),user.getId());
        this.createdDate = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessUsers)) return false;
        BusinessUsers that = (BusinessUsers) o;
        return
                createdDate.equals(that.createdDate) &&
                business.equals(that.business) &&
                user.equals(that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdDate, business, user);
    }
}
