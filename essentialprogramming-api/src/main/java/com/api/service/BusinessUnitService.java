package com.api.service;

import com.api.entities.Business;
import com.api.entities.BusinessService;
import com.api.entities.BusinessUnit;
import com.api.entities.ServiceDetail;
import com.api.input.BusinessServiceInput;
import com.api.input.BusinessUnitInput;
import com.api.mapper.BusinessUnitMapper;
import com.api.output.BusinessServiceJSON;
import com.api.output.BusinessUnitJSON;
import com.api.repository.BusinessRepository;
import com.api.repository.BusinessUnitRepository;
import com.crypto.Crypt;
import com.internationalization.Messages;
import com.util.enums.HTTPCustomStatus;
import com.util.enums.Language;
import com.util.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.resources.AppResources.ENCRYPTION_KEY;

@Service
public class BusinessUnitService {

    private BusinessUnitRepository unitRepository;
    private BusinessRepository businessRepository;


    @Autowired
    public BusinessUnitService(BusinessUnitRepository initRepo,
                               BusinessRepository initBusinessRepo
    ){
        unitRepository = initRepo;
        businessRepository = initBusinessRepo;

    }

    private String getComplexUUID() {
        return UUID.randomUUID().toString() + UUID.randomUUID().toString();
    }

    @Transactional
    public void save(String email, BusinessUnitInput businessUnitInput, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        String businessCode =businessUnitInput.getBusinessCode();
        businessCode  = encrypted ? Crypt.decrypt(businessCode, ENCRYPTION_KEY.value()) : businessCode;

        Business business = businessRepository.findByBusinessCode(businessCode).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );



        BusinessUnit businessUnit = BusinessUnitMapper.inputToBusinessUnit(businessUnitInput);
        businessUnit.setBusiness(business);
        businessUnit.setBusinessUnitCode(getComplexUUID());

        unitRepository.save(businessUnit);
    }

    @Transactional
    public void update(String code, BusinessUnitInput businessUnitInput, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        code  = encrypted ? Crypt.decrypt(code, ENCRYPTION_KEY.value()) : code;

        BusinessUnit businessUnit = unitRepository.findByBusinessUnitCode(code).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESSUNIT.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        businessUnit.setName(businessUnitInput.getName());

        unitRepository.save(businessUnit);
    }

    @Transactional
    public List<BusinessUnitJSON> load(String code, Language language, int version) throws GeneralSecurityException {
        final boolean encrypted = (version == 0);

        code  = encrypted ? Crypt.decrypt(code, ENCRYPTION_KEY.value()) : code;

        Business business = businessRepository.findByBusinessCode(code).orElseThrow(
                () -> new ApiException(Messages.get("BUSINESS.NOT.EXIST", language), HTTPCustomStatus.UNAUTHORIZED)
        );

        List<BusinessUnit>  businessUnits = unitRepository.findAllByBusiness(business);
        List<BusinessUnitJSON> businessUnitJSONS = new ArrayList<>();
        for(BusinessUnit businessUnit : businessUnits)
        {
            businessUnitJSONS.add(BusinessUnitMapper.businessUnitToOutput(businessUnit));
        }

        return businessUnitJSONS;
    }

    @Transactional
    public void delete(String businessUnitCode, Language language, int version) throws GeneralSecurityException {

        final boolean encrypted = (version == 0);

        businessUnitCode  = encrypted ? Crypt.decrypt(businessUnitCode, ENCRYPTION_KEY.value()) : businessUnitCode;

        unitRepository.deleteByBusinessUnitCode(businessUnitCode);
    }

}
