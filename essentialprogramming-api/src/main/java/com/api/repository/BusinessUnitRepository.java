package com.api.repository;

import com.api.entities.Business;
import com.api.entities.BusinessUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit,Integer> {

    List<BusinessUnit> findAllByBusiness(Business business);

    Optional<BusinessUnit> findByBusinessUnitCode(String businessUnitCode);

    void deleteByBusinessUnitCode(String businessUnitCode);

}
