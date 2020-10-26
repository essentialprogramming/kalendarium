package com.api.repository;


import com.api.entities.AddressHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressHistoryRepository extends JpaRepository<AddressHistory, Integer> {
}
