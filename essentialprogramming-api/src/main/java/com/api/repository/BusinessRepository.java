package com.api.repository;

import com.api.entities.Business;
import com.api.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface BusinessRepository extends JpaRepository<Business, Integer> {

    Optional<Business> findByCreatedBy(User user);
    
    Optional<Business> findByBusinessCode(@Param("businessCode") String businessCode);

    Optional<Business> findByBusinessCodeAndDeleted(@Param("businessCode") String businessCode, @Param("deleted") boolean deleted);

    @Query(value = "select *\n" +
            "from business b\n" +
            "where b.name like %?1% or b.businessid in\n" +
            "    (\n" +
            "        select bu.businessid\n" +
            "        from businessunit bu\n" +
            "        where bu.name like %?1%\n" +
            "        ) or b.businessid in \n" +
            "            (\n" +
            "                select bs.businessid\n" +
            "                from businessservice bs\n" +
            "                where bs.name like %?1%\n" +
            "                ) or b.businessid in \n" +
            "                    (\n" +
            "                        select u.employer\n" +
            "                        from \"user\" u \n" +
            "                        where u.firstname like %?1% or u.lastname like %?1%\n" +
            "                        )", nativeQuery = true)
    List<Business> findAllBusinessByCriteria(String search);
}
