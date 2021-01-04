package com.api.service;

import com.api.entities.*;
import com.api.entities.BusinessService;
import com.api.input.BusinessServiceInput;
import com.api.input.BusinessServiceUpdateInput;
import com.api.input.UserInput;
import com.api.mapper.*;
import com.api.output.BusinessServiceJSON;
import com.api.output.UserJSON;
import com.api.repository.*;
import com.crypto.Crypt;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.resources.AppResources.ENCRYPTION_KEY;

@Service
public class BusinessServiceService {
    private BusinessRepository businessRepository;
    private UserRepository userRepository;
    private ServiceDetailRepository serviceDetailRepository;
    private BusinessServiceRepository businessServiceRepository;
    private BusinessUsersRepository businessUsersRepository;

    @Autowired
    public BusinessServiceService(BusinessRepository businessRepository,
                                  UserRepository userRepository,
                                  ServiceDetailRepository serviceDetailRepository,
                                  BusinessServiceRepository businessServiceRepository,
                                  BusinessUsersRepository businessUsersRepository) {
        this.businessRepository = businessRepository;
        this.userRepository = userRepository;
        this.serviceDetailRepository = serviceDetailRepository;
        this.businessServiceRepository = businessServiceRepository;
        this.businessUsersRepository = businessUsersRepository;
    }

    @Transactional
    public void save(String email, BusinessServiceInput businessServiceInput, Language language) throws GeneralSecurityException {

        String businessCode = businessServiceInput.getBusinessCode();
        businessCode = Crypt.decrypt(businessCode, ENCRYPTION_KEY.value());

        Business business = businessRepository.findByBusinessCode(businessCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        ServiceDetail serviceDetail = ServiceDetailMapper.inputToServiceDetail(businessServiceInput);

        ServiceDetail savedServiceDetail = serviceDetailRepository.save(serviceDetail);

        BusinessService businessService = BusinessServiceMapper.inputToBusinessService(businessServiceInput);
        businessService.setBusiness(business);
        businessService.setServiceDetail(serviceDetail);
        businessService.setBusinessServiceCode(getComplexUUID());

        businessServiceRepository.save(businessService);
    }

    private String getComplexUUID() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    @Transactional
    public void update(String code, BusinessServiceUpdateInput businessServiceInput, Language language) throws GeneralSecurityException {
        code = Crypt.decrypt(code, ENCRYPTION_KEY.value());

        BusinessService businessService = businessServiceRepository.findByBusinessServiceCode(code).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSSERVICE.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        ServiceDetail newServiceDetail = ServiceDetailMapper.updateInputToServiceDetail(businessServiceInput);
        ServiceDetail serviceDetail = businessService.getServiceDetail();
        serviceDetail.setDuration(newServiceDetail.getDuration());

        if (newServiceDetail.getDay() != null)
            serviceDetail.setDay(newServiceDetail.getDay());
        if (newServiceDetail.getEndTime() != null)
            serviceDetail.setEndTime(newServiceDetail.getEndTime());
        if (newServiceDetail.getStartTime() != null)
            serviceDetail.setStartTime(newServiceDetail.getStartTime());

        serviceDetailRepository.save(serviceDetail);

        businessService.setName(businessServiceInput.getName());
        businessService.setServiceDetail(serviceDetail);

        businessServiceRepository.save(businessService);
    }

    @Transactional
    public List<BusinessServiceJSON> load(String code, Language language) throws GeneralSecurityException {
        code = Crypt.decrypt(code, ENCRYPTION_KEY.value());

        Business business = businessRepository.findByBusinessCode(code).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<BusinessService> businessServices = businessServiceRepository.findAllByBusiness(business);
        List<BusinessServiceJSON> businessServiceJSONS = new ArrayList<>();
        for (BusinessService businessService : businessServices) {
            businessServiceJSONS.add(makeBusinessServiceJson(businessService));
        }

        return businessServiceJSONS;
    }

    private BusinessServiceJSON makeBusinessServiceJson(BusinessService business) throws GeneralSecurityException {
        BusinessServiceJSON result = BusinessServiceMapper.businessServiceToOutput(business);
        return result;
    }


    @Transactional
    public void delete(String businessServiceCode, Language language) throws GeneralSecurityException {

        businessServiceCode = Crypt.decrypt(businessServiceCode, ENCRYPTION_KEY.value());

        businessServiceRepository.deleteByBusinessServiceCode(businessServiceCode);
    }

    @Transactional
    public void addEmployee(String email, String employeeEmail) {

        Optional<User> loggedUser = userRepository.findByEmail(email);

        Optional<User> foundUser = userRepository.findByEmail(employeeEmail);
        if (foundUser.isPresent()) {
            List<BusinessUsers> businessUsers = loggedUser.get().getBusinessUsers();
            Business business = businessUsers.size() > 0 ? loggedUser.get().getBusinessUsers().get(0).getBusiness() : null;
            BusinessUsers businessUsersNew = new BusinessUsers(business, foundUser.get());
            businessUsersRepository.save(businessUsersNew);
        }

    }

    @Transactional
    public List<UserJSON> getAllEmployeesForBusiness(String email) {

        Optional<User> loggedUser = userRepository.findByEmail(email);
        List<BusinessUsers> businessUserss = loggedUser.get().getBusinessUsers();
        Business business = businessUserss.size() > 0 ? loggedUser.get().getBusinessUsers().get(0).getBusiness() : null;

        return businessUsersRepository.findAll().stream()
                .filter(businessUsers -> businessUsers.getBusiness().equals(business))
                .map(BusinessUsers::getUser)
                .map(UserMapper::userToJson)
                .collect(Collectors.toList());
    }
}
