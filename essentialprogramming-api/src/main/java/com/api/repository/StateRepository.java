package com.api.repository;

import com.api.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StateRepository extends JpaRepository<State,Integer> {

    Optional<State> findByStateCode(String stateCode);
}
