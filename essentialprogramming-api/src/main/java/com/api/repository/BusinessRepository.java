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

    @Query(value = "select b\n" +
            "from business b\n" +
            "where lower(b.name) like lower(concat('%',?1, '%')) or b.id in\n" +
            "    (\n" +
            "        select bu.business\n" +
            "        from businessunit bu\n" +
            "        where lower(bu.name) like lower(concat('%',?1,'%')) \n" +
            "        ) or b.id in \n" +
            "            (\n" +
            "                select bs.business\n" +
            "                from businessservice bs\n" +
            "                where lower(bs.name) like lower(concat('%',?1,'%')) \n" +
            "                ) or b.id in (select u.employer\n" +
            "                        from user u \n" +
            "                        where lower(u.firstName) like lower(concat('%',?1,'%')) or lower(u.lastName) like lower(concat('%',?1,'%')) \n" +
            "                        )")
    List<Business> findAllBusinessByCriteria(String search);
}
