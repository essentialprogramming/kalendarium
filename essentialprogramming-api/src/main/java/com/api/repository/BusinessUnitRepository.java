package com.api.repository;

import com.api.entities.BusinessUnit;
import com.api.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessUnitRepository extends JpaRepository<BusinessUnit,Integer> {
}
