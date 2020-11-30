package com.api.repository;

import com.api.entities.ServiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDetailRepository extends JpaRepository<ServiceDetail,Integer> {
}
