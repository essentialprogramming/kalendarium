package com.api.service;

import com.api.entities.*;
import com.api.entities.BusinessService;
import com.api.input.AppointmentInput;
import com.api.mapper.AppointmentMapper;
import com.api.output.AppointmentJSON;
import com.api.repository.*;
import com.crypto.Crypt;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.resources.AppResources.ENCRYPTION_KEY;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    private BusinessRepository businessRepository;
    private BusinessUnitRepository businessUnitRepository;
    private BusinessServiceRepository businessServiceRepository;
    private UserRepository userRepository;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              BusinessRepository businessRepository,
                              BusinessUnitRepository businessUnitRepository,
                              BusinessServiceRepository businessServiceRepository,
                              UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.businessRepository = businessRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.businessServiceRepository = businessServiceRepository;
        this.userRepository = userRepository;
    }

    private String getComplexUUID() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    @Transactional
    public AppointmentJSON save(String email, AppointmentInput appointmentInput, Language language) throws GeneralSecurityException {
        String businessCode = appointmentInput.getBusinessCode();
        businessCode  = Crypt.decrypt(businessCode, ENCRYPTION_KEY.value());

        Business business = businessRepository.findByBusinessCode(businessCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        String businessServiceCode = appointmentInput.getBusinessServiceCode();
        businessServiceCode  = Crypt.decrypt(businessServiceCode, ENCRYPTION_KEY.value());

        BusinessService businessService = businessServiceRepository.findByBusinessServiceCode(businessServiceCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSSERVICE.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        String userKey = appointmentInput.getUserKey();

        User user = userRepository.findByUserKey(userKey).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        String businessUnitCode = "";
        BusinessUnit businessUnit = null;

        // if a business unit is provided we create the link between the unit and the appointment
        // if a business unit is not provided we select a random available unit and we create the link between the unit and the appointment
        if (!appointmentInput.getBusinessUnitCode().equals("")) {
            businessUnitCode = appointmentInput.getBusinessUnitCode();
            businessUnitCode = Crypt.decrypt(businessUnitCode, ENCRYPTION_KEY.value());

            businessUnit = businessUnitRepository.findByBusinessUnitCode(businessUnitCode).orElseThrow(
                    () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
            );

            boolean availableBusinessUnit = businessUnit.getAppointments().stream().allMatch(appointment -> {
                if (businessService.getServiceDetail().getStartTime().isAfter(appointment.getBusinessService().getServiceDetail().getStartTime()) &&
                        businessService.getServiceDetail().getStartTime().isBefore(appointment.getBusinessService().getServiceDetail().getEndTime())) {
                    return false;
                }
                if (businessService.getServiceDetail().getEndTime().isAfter(appointment.getBusinessService().getServiceDetail().getStartTime()) &&
                        businessService.getServiceDetail().getEndTime().isBefore(appointment.getBusinessService().getServiceDetail().getEndTime())) {
                    return false;
                }
                return true;
            });

            // if the business unit that the user selected is not available a random business unit will be chosen
            if (availableBusinessUnit) {
                businessUnit = businessUnitRepository
                        .findAll()
                        .stream()
                        .filter(businessUnit1 -> {
                            return businessUnit1.getAppointments().stream().allMatch(appointment -> {
                                if (businessService.getServiceDetail().getStartTime().isAfter(appointment.getBusinessService().getServiceDetail().getStartTime()) &&
                                        businessService.getServiceDetail().getStartTime().isBefore(appointment.getBusinessService().getServiceDetail().getEndTime())) {
                                    return false;
                                }
                                if (businessService.getServiceDetail().getEndTime().isAfter(appointment.getBusinessService().getServiceDetail().getStartTime()) &&
                                        businessService.getServiceDetail().getEndTime().isBefore(appointment.getBusinessService().getServiceDetail().getEndTime())) {
                                    return false;
                                }
                                return true;
                            });
                        })
                        .findFirst().orElseThrow(
                                () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.AVAILABLE", language), HTTPCustomStatus.UNAUTHORIZED)
                        );
            }

        } else {
            // the search for an available business unit will stop when one available unit is found
            businessUnit = businessUnitRepository
                    .findAll()
                    .stream()
                    .filter(businessUnit1 -> {
                        return businessUnit1.getAppointments().stream().allMatch(appointment -> {
                            if (businessService.getServiceDetail().getStartTime().isAfter(appointment.getBusinessService().getServiceDetail().getStartTime()) &&
                                    businessService.getServiceDetail().getStartTime().isBefore(appointment.getBusinessService().getServiceDetail().getEndTime())) {
                                return false;
                            }
                            if (businessService.getServiceDetail().getEndTime().isAfter(appointment.getBusinessService().getServiceDetail().getStartTime()) &&
                                    businessService.getServiceDetail().getEndTime().isBefore(appointment.getBusinessService().getServiceDetail().getEndTime())) {
                                return false;
                            }
                            return true;
                        });
                    })
                    .findFirst().orElseThrow(
                            () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.AVAILABLE", language), HTTPCustomStatus.UNAUTHORIZED)
                    );
        }


        Appointment appointment = AppointmentMapper.inputToAppointment(appointmentInput);
        appointment.setUser(user);
        appointment.setBusiness(business);
        appointment.setBusinessUnit(businessUnit);
        appointment.setBusinessService(businessService);
        appointment.setAppointmentCode(getComplexUUID());

        businessUnit.getAppointments().add(appointment);

        appointmentRepository.save(appointment);

        return AppointmentMapper.appointmentToOutput(appointment);
    }

    @Transactional
    public void update(String code, AppointmentInput appointmentInput, Language language) throws GeneralSecurityException {
        code  = Crypt.decrypt(code, ENCRYPTION_KEY.value());

        Appointment appointment = appointmentRepository.findByAppointmentCode(code).orElseThrow(
                () -> new ApiException(Messages.get("APPOINTMENT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        appointment.setStatus(appointmentInput.getStatus());

        appointmentRepository.save(appointment);
    }

    @Transactional
    public List<AppointmentJSON> loadByUser(String userKey, Language language) throws GeneralSecurityException {
        User user = userRepository.findByUserKey(userKey).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByUser(user);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment));
        }

        return appointmentJSONS;
    }

    @Transactional
    public List<AppointmentJSON> loadByBusiness(String businessCode, Language language) throws GeneralSecurityException {
        businessCode  = Crypt.decrypt(businessCode, ENCRYPTION_KEY.value());

        Business business = businessRepository.findByBusinessCode(businessCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByBusiness(business);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment));
        }
        return appointmentJSONS;
    }

    @Transactional
    public List<AppointmentJSON> loadByBusinessUnit(String businessUnitCode, Language language) throws GeneralSecurityException {
        businessUnitCode  = Crypt.decrypt(businessUnitCode, ENCRYPTION_KEY.value());

        BusinessUnit businessUnit = businessUnitRepository.findByBusinessUnitCode(businessUnitCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByBusinessUnit(businessUnit);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment));
        }
        return appointmentJSONS;
    }

    @Transactional
    public List<AppointmentJSON> loadByBusinessService(String businessServiceCode, Language language) throws GeneralSecurityException {
        businessServiceCode  = Crypt.decrypt(businessServiceCode, ENCRYPTION_KEY.value());

        BusinessService businessService = businessServiceRepository.findByBusinessServiceCode(businessServiceCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSSERVICE.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByBusinessService(businessService);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        for (Appointment appointment : appointments) {
            appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment));
        }
        return appointmentJSONS;
    }

    @Transactional
    public void delete(String appointmentCode, Language language) throws GeneralSecurityException {
        appointmentCode  = Crypt.decrypt(appointmentCode, ENCRYPTION_KEY.value());

        appointmentRepository.deleteByAppointmentCode(appointmentCode);
    }

}
