package com.api.repository;

import com.api.entities.BusinessHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusinessHistoryRepository extends JpaRepository<BusinessHistory, Integer> {
}
