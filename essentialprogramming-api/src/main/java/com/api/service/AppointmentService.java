package com.api.service;

import com.api.entities.*;
import com.api.entities.BusinessService;
import com.api.input.AppointmentInput;
import com.api.mapper.AppointmentMapper;
import com.api.mapper.BusinessUnitMapper;
import com.api.output.AppointmentJSON;
import com.api.output.BusinessUnitJSON;
import com.api.repository.*;
import com.crypto.Crypt;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.security.GeneralSecurityException;
import java.time.temporal.ChronoUnit;
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
    public void save(String email, AppointmentInput appointmentInput, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        String businessCode = appointmentInput.getBusinessCode();
        businessCode  = encrypted ? Crypt.decrypt(businessCode, ENCRYPTION_KEY.value()) : businessCode;

        Business business = businessRepository.findByBusinessCode(businessCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        String businessServiceCode = appointmentInput.getBusinessServiceCode();
        businessServiceCode  = encrypted ? Crypt.decrypt(businessServiceCode, ENCRYPTION_KEY.value()) : businessServiceCode;

        BusinessService businessService = businessServiceRepository.findByBusinessServiceCode(businessServiceCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSSERVICE.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        String userKey = appointmentInput.getUserKey();
        userKey  = encrypted ? Crypt.decrypt(userKey, ENCRYPTION_KEY.value()) : userKey;

        User user = userRepository.findByUserKey(userKey).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        String businessUnitCode = "";
        BusinessUnit businessUnit = null;

        // if a business unit is provided we create the link between the unit and the appointment
        // if a business unit is not provided we select a random available unit and we create the link between the unit and the appointment
        if (!appointmentInput.getBusinessUnitCode().equals("")) {
            businessUnitCode = appointmentInput.getBusinessUnitCode();
            businessUnitCode = encrypted ? Crypt.decrypt(businessUnitCode, ENCRYPTION_KEY.value()) : businessUnitCode;

            businessUnit = businessUnitRepository.findByBusinessUnitCode(businessUnitCode).orElseThrow(
                    () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
            );
        } else {
            // the search for an available business unit will stop when one available unit is found
            businessUnit = businessUnitRepository
                    .findAll()
                    .stream()
                    .filter(businessUnit1 -> {
                        return businessUnit1.getAppointments().stream().allMatch(appointment -> {
                            if (!appointment.getBusinessService().getServiceDetail().getDate().isEqual(businessService.getServiceDetail().getDate())) {
                                return true;
                            }
                            if (appointment.getBusinessService().getServiceDetail().getStartTime().plus(
                                    appointment.getBusinessService().getServiceDetail().getDuration(),
                                    ChronoUnit.MINUTES).isAfter(businessService.getServiceDetail().getStartTime().plus(
                                    businessService.getServiceDetail().getDuration(),
                                    ChronoUnit.MINUTES
                            ))) {
                                return true;
                            }
                            if (appointment.getBusinessService().getServiceDetail().getStartTime().plus(
                                    appointment.getBusinessService().getServiceDetail().getDuration(),
                                    ChronoUnit.MINUTES).isBefore(businessService.getServiceDetail().getStartTime().plus(
                                    businessService.getServiceDetail().getDuration(),
                                    ChronoUnit.MINUTES
                            ))) {
                                return true;
                            }
                            return false;
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
    }

    @Transactional
    public void update(String code, AppointmentInput appointmentInput, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        code  = encrypted ? Crypt.decrypt(code, ENCRYPTION_KEY.value()) : code;

        Appointment appointment = appointmentRepository.findByAppointmentCode(code).orElseThrow(
                () -> new ApiException(Messages.get("APPOINTMENT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        appointment.setStatus(appointmentInput.getStatus());

        appointmentRepository.save(appointment);
    }

    @Transactional
    public List<AppointmentJSON> loadByUser(String userKey, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        userKey  = encrypted ? Crypt.decrypt(userKey, ENCRYPTION_KEY.value()) : userKey;

        User user = userRepository.findByUserKey(userKey).orElseThrow(
                () -> new ApiException(Messages.get("USER.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByUser(user);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        appointments.forEach(appointment -> appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment)));

        return appointmentJSONS;
    }

    @Transactional
    public List<AppointmentJSON> loadByBusiness(String businessCode, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        businessCode  = encrypted ? Crypt.decrypt(businessCode, ENCRYPTION_KEY.value()) : businessCode;

        Business business = businessRepository.findByBusinessCode(businessCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByBusiness(business);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        appointments.forEach(appointment -> appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment)));

        return appointmentJSONS;
    }

    @Transactional
    public List<AppointmentJSON> loadByBusinessUnit(String businessUnitCode, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        businessUnitCode  = encrypted ? Crypt.decrypt(businessUnitCode, ENCRYPTION_KEY.value()) : businessUnitCode;

        BusinessUnit businessUnit = businessUnitRepository.findByBusinessUnitCode(businessUnitCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByBusinessUnit(businessUnit);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        appointments.forEach(appointment -> appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment)));

        return appointmentJSONS;
    }

    @Transactional
    public List<AppointmentJSON> loadByBusinessService(String businessServiceCode, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        businessServiceCode  = encrypted ? Crypt.decrypt(businessServiceCode, ENCRYPTION_KEY.value()) : businessServiceCode;

        BusinessService businessService = businessServiceRepository.findByBusinessServiceCode(businessServiceCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSSERVICE.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment>  appointments = appointmentRepository.findAllByBusinessService(businessService);
        List<AppointmentJSON> appointmentJSONS = new ArrayList<>();

        appointments.forEach(appointment -> appointmentJSONS.add(AppointmentMapper.appointmentToOutput(appointment)));

        return appointmentJSONS;
    }

    @Transactional
    public void delete(String appointmentCode, Language language, int version) throws GeneralSecurityException {

        final boolean encrypted = (version == 0);

        appointmentCode  = encrypted ? Crypt.decrypt(appointmentCode, ENCRYPTION_KEY.value()) : appointmentCode;

        appointmentRepository.deleteByAppointmentCode(appointmentCode);
    }

}
