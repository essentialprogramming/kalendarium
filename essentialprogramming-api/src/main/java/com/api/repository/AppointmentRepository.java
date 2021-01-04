package com.api.repository;

import com.api.entities.*;
import com.api.entities.enums.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {


    List<Appointment> findAllByUser(User user);

    List<Appointment> findAllByBusiness(Business business);

    List<Appointment> findAllByBusinessUnit(BusinessUnit businessUnit);

    List<Appointment> findAllByBusinessService(BusinessService businessService);

    Optional<Appointment> findByAppointmentCode(String businessUnitCode);

    Optional<Appointment> findByBusinessAndBusinessServiceAndDayAndStartTimeAndEndTime(Business business, BusinessService businessService, Day day, LocalTime startTime, LocalTime endTime);

    void deleteByAppointmentCode(String appointmentCode);

}
