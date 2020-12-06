package com.api.repository;

import com.api.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {


    List<Appointment> findAllByUser(User user);

    List<Appointment> findAllByBusiness(Business business);

    List<Appointment> findAllByBusinessUnit(BusinessUnit businessUnit);

    List<Appointment> findAllByBusinessService(BusinessService businessService);

    Optional<Appointment> findByAppointmentCode(String businessUnitCode);

    void deleteByAppointmentCode(String appointmentCode);

}
