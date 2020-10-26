package com.api.entities;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
@NoArgsConstructor
public class BusinessUsersKey implements Serializable {

    private static final long serialVersionUID = -2792203186752317748L;

    @Column(name = "businessid")
    private int businessId;

    @Column(name = "userid")
    private int userId;

    public BusinessUsersKey(int businessId, int userId) {
        this.businessId = businessId;
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BusinessUsersKey)) return false;
        BusinessUsersKey that = (BusinessUsersKey) o;
        return businessId == that.businessId &&
                userId == that.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(businessId, userId);
    }
}
