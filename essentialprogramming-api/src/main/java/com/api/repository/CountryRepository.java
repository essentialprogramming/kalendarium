package com.api.repository;

import com.api.entities.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country,Integer> {

    Optional<Country> findByCountryCode(String countryCode);
}
