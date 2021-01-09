package com.api.repository;

import com.api.entities.Business;
import com.api.entities.BusinessUnit;
import com.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit,Integer> {

    List<BusinessUnit> findAllByBusiness(Business business);

    Optional<BusinessUnit> findByBusinessUnitCode(String businessUnitCode);

    Optional<BusinessUnit> findByUser(User user);

    void deleteByBusinessUnitCode(String businessUnitCode);

}
