package com.api.repository;

import com.api.entities.BusinessUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessUsersRepository extends JpaRepository<BusinessUsers,Integer> {
}
