package com.api.repository;

import com.api.entities.Business;
import com.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    Optional<Business> findByCreatedBy(User user);
    
    Optional<Business> findByBusinessCode(@Param("businessCode") String businessCode);

    Optional<Business> findByBusinessCodeAndDeleted(@Param("businessCode") String businessCode, @Param("deleted") boolean deleted);

}