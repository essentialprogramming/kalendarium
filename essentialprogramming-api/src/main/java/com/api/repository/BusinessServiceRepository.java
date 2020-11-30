package com.api.repository;

import com.api.entities.Business;
import com.api.entities.BusinessService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessServiceRepository extends JpaRepository<BusinessService,Integer> {

    List<BusinessService> findAllByBusiness(Business business);

    Optional<BusinessService> findByBusinessServiceCode(String businessServiceCode);

    void deleteByBusinessServiceCode(String businessServiceCode);
}
