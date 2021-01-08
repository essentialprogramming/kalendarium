package com.api.service;

import com.api.entities.*;
import com.api.entities.BusinessService;
import com.api.entities.enums.AppointmentStatus;
import com.api.entities.enums.Day;
import com.api.input.AppointmentInput;
import com.api.mapper.AppointmentMapper;
import com.api.output.AppointmentJSON;
import com.api.repository.*;
import com.crypto.Crypt;
import com.email.EmailTemplateService;
import com.email.Template;
import com.internationalization.EmailMessages;
import com.internationalization.Messages;
import com.resources.AppResources;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.resources.AppResources.ENCRYPTION_KEY;

@Service
public class AppointmentService {

    private AppointmentRepository appointmentRepository;
    private BusinessRepository businessRepository;
    private BusinessUnitRepository businessUnitRepository;
    private BusinessServiceRepository businessServiceRepository;
    private UserRepository userRepository;

    private final EmailTemplateService emailTemplateService;


    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              BusinessRepository businessRepository,
                              BusinessUnitRepository businessUnitRepository,
                              BusinessServiceRepository businessServiceRepository,
                              UserRepository userRepository, EmailTemplateService emailTemplateService) {
        this.appointmentRepository = appointmentRepository;
        this.businessRepository = businessRepository;
        this.businessUnitRepository = businessUnitRepository;
        this.businessServiceRepository = businessServiceRepository;
        this.userRepository = userRepository;
        this.emailTemplateService = emailTemplateService;
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

        Appointment app = AppointmentMapper.inputToAppointment(appointmentInput);

        // if the day/start time/end time do not correspond with the ones in the business service then we can't create the appointment
        if (!businessService.getServiceDetail().getDay().contains(appointmentInput.getDay()) ||
                businessService.getServiceDetail().getStartTime().isAfter(app.getStartTime()) ||
                businessService.getServiceDetail().getEndTime().isBefore(app.getEndTime())) {
            throw  new ApiException(Messages.get("BUSINESSSERVICE.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED);
        }

        // if a business unit is provided we create the link between the unit and the appointment
        // if a business unit is not provided we select a random available unit and we create the link between the unit and the appointment
        if (!appointmentInput.getBusinessUnitCode().equals("")) {
            businessUnitCode = appointmentInput.getBusinessUnitCode();
            businessUnitCode = Crypt.decrypt(businessUnitCode, ENCRYPTION_KEY.value());

            businessUnit = businessUnitRepository.findByBusinessUnitCode(businessUnitCode).orElseThrow(
                    () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
            );

//            boolean availableBusinessUnit = businessUnit.getAppointments().stream().allMatch(appointment -> {
//                Day day = appointment.getDay();
//                LocalTime start = appointment.getStartTime();
//                LocalTime end = appointment.getEndTime();
//
//                if (day.equals(app.getDay())) {
//                    if (start.equals(app.getStartTime()) && end.equals(app.getEndTime())) {
//                        return false;
//                    }
//
//                    if (app.getStartTime().isAfter(start) && app.getStartTime().isBefore(end)) {
//                        return false;
//                    }
//
//                    if (app.getEndTime().isAfter(start) && app.getEndTime().isBefore(end)) {
//                        return false;
//                    }
//                }
//
//                return true;
//            });

            // if the business unit that the user selected is not available a random business unit will be chosen
//             if (!availableBusinessUnit) {
//                businessUnit = businessUnitRepository
//                        .findAll()
//                        .stream()
//                        .filter(businessUnit1 -> {
//                            return businessUnit1.getAppointments().stream().allMatch(appointment -> {
//                                Day day = appointment.getDay();
//                                LocalTime start = appointment.getStartTime();
//                                LocalTime end = appointment.getEndTime();
//
//                                if (day.equals(app.getDay())) {
//                                    if (start.equals(app.getStartTime()) && end.equals(app.getEndTime())) {
//                                        return false;
//                                    }
//
//                                    if (app.getStartTime().isAfter(start) && app.getStartTime().isBefore(end)) {
//                                        return false;
//                                    }
//
//                                    if (app.getEndTime().isAfter(start) && app.getEndTime().isBefore(end)) {
//                                        return false;
//                                    }
//                                }
//
//                                return true;
//                            });
//                        })
//                        .findFirst().orElseThrow(
//                                () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.AVAILABLE", language), HTTPCustomStatus.UNAUTHORIZED)
//                        );
//            }

        } else {
            // the search for an available business unit will stop when one available unit is found
            businessUnit = businessUnitRepository
                    .findAll()
                    .stream()
                    .filter(businessUnit1 -> {
                        return businessUnit1.getAppointments().stream().allMatch(appointment -> {
                            Day day = appointment.getDay();
                            LocalTime start = appointment.getStartTime();
                            LocalTime end = appointment.getEndTime();

                            if (day.equals(app.getDay())) {
                                if (start.equals(app.getStartTime()) && end.equals(app.getEndTime())) {
                                    return false;
                                }

                                if (app.getStartTime().isAfter(start) && app.getStartTime().isBefore(end)) {
                                    return false;
                                }

                                if (app.getEndTime().isAfter(start) && app.getEndTime().isBefore(end)) {
                                    return false;
                                }
                            }

                            return true;
                        });
                    })
                    .findFirst().orElseThrow(
                            () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.AVAILABLE", language), HTTPCustomStatus.UNAUTHORIZED)
                    );
        }


        app.setUser(user);
        app.setBusiness(business);
        app.setBusinessUnit(businessUnit);
        app.setBusinessService(businessService);
        app.setAppointmentCode(getComplexUUID());

        boolean anotherAppointmentFound = false;

        List<Appointment> appointments = appointmentRepository.findAllByBusinessAndBusinessServiceAndBusinessUnitAndDayAndStartTimeAndEndTime(
                business,
                businessService,
                businessUnit,
                app.getDay(),
                app.getStartTime(),
                app.getEndTime()
        );

        if (!appointments.isEmpty()) {
            anotherAppointmentFound = true;
        }

        if (!anotherAppointmentFound) {
            app.setStatus(AppointmentStatus.ACCEPTED);
            businessUnit.getAppointments().add(app);
            appointmentRepository.save(app);
        } else {
            app.setStatus(AppointmentStatus.PENDING);
            businessUnit.getAppointments().add(app);
            appointmentRepository.save(app);
        }

        return AppointmentMapper.appointmentToOutput(app);
    }

    @Transactional
    public AppointmentJSON updateStatus(String appointmentCode, Language language) throws GeneralSecurityException {
        String code = Crypt.decrypt(appointmentCode, ENCRYPTION_KEY.value());

        Appointment appointment = appointmentRepository.findByAppointmentCode(code).orElseThrow(
                () -> new ApiException(Messages.get("APPOINTMENT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        appointment.setStatus(AppointmentStatus.ACCEPTED);
        appointmentRepository.save(appointment);

        return AppointmentMapper.appointmentToOutput(appointment);
    }

    @Transactional
    public void update(String code, AppointmentInput appointmentInput, Language language) throws GeneralSecurityException {
        code  = Crypt.decrypt(code, ENCRYPTION_KEY.value());

        Appointment appointment = appointmentRepository.findByAppointmentCode(code).orElseThrow(
                () -> new ApiException(Messages.get("APPOINTMENT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        Appointment app = AppointmentMapper.inputToAppointment(appointmentInput);

        appointment.setDate(app.getDate());
        appointment.setDay(app.getDay());
        appointment.setStartTime(app.getStartTime());
        appointment.setEndTime(app.getEndTime());

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

        Appointment app = appointmentRepository.findByAppointmentCode(appointmentCode).orElseThrow(
                () -> new ApiException(Messages.get("APPOINTMENT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<Appointment> appointments = appointmentRepository.findAllByBusinessAndBusinessServiceAndBusinessUnitAndDayAndStartTimeAndEndTime(
                app.getBusiness(),
                app.getBusinessService(),
                app.getBusinessUnit(),
                app.getDay(),
                app.getStartTime(),
                app.getEndTime()
        ).stream().filter(appointment -> !appointment.getAppointmentCode().equals(app.getAppointmentCode())).collect(Collectors.toList());

        appointments.forEach(appointment -> {
            String validationKey = null;
            try {
                validationKey = Crypt.encrypt(appointment.getUser().getUserKey(), Crypt.encrypt(appointment.getUser().getUserKey(), appointment.getUser().getUserKey()));
                String encryptedUserKey = Crypt.encrypt(appointment.getUser().getUserKey(), AppResources.ENCRYPTION_KEY.value());
                Map<String, Object> templateKeysAndValues = new HashMap<>();
                String url = AppResources.APPOINTMENT_CONFIRMATION_URL.value() + "?code=" + Crypt.encrypt(appointment.getAppointmentCode(), ENCRYPTION_KEY.value());
                templateKeysAndValues.put("fullName", appointment.getUser().getFullName());
                templateKeysAndValues.put("confirmationLink", url);
                emailTemplateService.send(templateKeysAndValues, appointment.getUser().getEmail(), EmailMessages.get("appointment.subject", language.getLocale()), Template.APPOINTMENT_CONFIRMATION, language.getLocale());
            } catch (GeneralSecurityException e) {
                throw new ApiException(Messages.get("ENCRYPTION.FAILED", language), HTTPCustomStatus.UNAUTHORIZED);
            }
        });

        appointmentRepository.deleteByAppointmentCode(appointmentCode);
    }

}
