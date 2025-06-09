package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AmlDetails;
import uk.gov.companieshouse.api.testdata.model.entity.SoleTraderDetails;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AmlSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspProfileServiceImpl implements DataService<AcspProfileData, AcspProfileSpec> {
    private static final String LINK_STEM = "/authorised-corporate-service-providers/";

    @Autowired
    private AcspProfileRepository repository;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AddressService addressService;

    public AcspProfileData create(AcspProfileSpec spec) throws DataException {
        var soleTraderForename = "Forename ";
        var soleTraderSurname = "Surname ";
        var nationality = "British";
        var randomId = randomService.getString(8);
        var acspNumber = Objects.requireNonNullElse(spec.getAcspNumber(), randomId);

        var profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setVersion(0L);
        profile.setStatus(Objects.requireNonNullElse(spec.getStatus(), "active"));
        profile.setType(Objects.requireNonNullElse(spec.getType(), "limited-company"));
        profile.setAcspNumber(acspNumber);
        profile.setBusinessSector(Objects.requireNonNullElse(spec.getBusinessSector(), "financial-institutions"));
        profile.setRegisteredOfficeAddress(addressService.getAddress(Jurisdiction.UNITED_KINGDOM));
        profile.setServiceAddress(addressService.getAddress(Jurisdiction.UNITED_KINGDOM));
        profile.setName(Objects.requireNonNullElse(spec.getName(),"Test Data Generator " + acspNumber + " Company Ltd"));
        profile.setLinksSelf(LINK_STEM + acspNumber);
        if (spec.getAmlDetails() != null) {
            List<AmlDetails> amlDetailsList = new ArrayList<>();
            for (AmlSpec amlSpec : spec.getAmlDetails()) {
                var amlDetails = new AmlDetails();
                amlDetails.setSupervisoryBody(amlSpec.getSupervisoryBody());
                amlDetails.setMembershipDetails(amlSpec.getMembershipDetails());
                amlDetailsList.add(amlDetails);
            }
            profile.setAmlDetails(amlDetailsList);
        }
        if (spec.getEmail() != null) {
            profile.setEmail(spec.getEmail());
        }
        if (Objects.equals("sole-trader", spec.getType())) {
            var soleTraderDetails = new SoleTraderDetails();
            soleTraderDetails.setForename(soleTraderForename + acspNumber);
            soleTraderDetails.setSurname(soleTraderSurname + acspNumber);
            soleTraderDetails.setNationality(nationality);
            soleTraderDetails.setUsualResidentialCountry(
                    addressService.getCountryOfResidence(Jurisdiction.ENGLAND));
            profile.setSoleTraderDetails(soleTraderDetails);
        }
        AcspProfile savedProfile = repository.save(profile);
        return new AcspProfileData(savedProfile.getAcspNumber());
    }

    @Override
    public boolean delete(String acspNumber) {
        var existingProfile = repository.findById(acspNumber);
        existingProfile.ifPresent(repository::delete);
        return existingProfile.isPresent();
    }

}
