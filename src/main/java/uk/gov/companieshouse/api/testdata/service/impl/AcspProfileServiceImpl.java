package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.AmlDetails;
import uk.gov.companieshouse.api.testdata.model.entity.AuditDetails;
import uk.gov.companieshouse.api.testdata.model.entity.SoleTraderDetails;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AmlRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class AcspProfileServiceImpl implements AcspProfileService {
    private static final String LINK_STEM = "/authorised-corporate-service-providers/";
    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AcspProfileServiceImpl.class));

    @Autowired
    private AcspProfileRepository repository;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AddressService addressService;

    public AcspProfileResponse create(AcspProfileRequest spec) throws DataException {
        var soleTraderForename = "Forename ";
        var soleTraderSurname = "Surname ";
        var nationality = "British";
        String randomId;

        do {
            randomId = "AP" + randomService.getNumber(6);
            Optional<AcspProfile> profile = this.getAcspProfile(randomId);
            if (profile.isEmpty()) {
                break;
            }
        } while (true);

        var acspNumber = Objects.requireNonNullElse(spec.getAcspNumber(), randomId);
        var profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setVersion(0L);
        profile.setStatus(Objects.requireNonNullElse(spec.getStatus(), "active"));
        profile.setType(Objects.requireNonNullElse(spec.getType(), "limited-company"));
        profile.setAcspNumber(acspNumber);
        profile.setRegisteredOfficeAddress(addressService.getAddress(JurisdictionType.UNITED_KINGDOM));
        String businessSector = spec.getBusinessSector();

        if (businessSector == null) {
            profile.setBusinessSector("financial-institutions");
        } else if (!businessSector.isBlank() && !"empty".equalsIgnoreCase(businessSector)) {
            profile.setBusinessSector(businessSector);
        }

        Address serviceAddress = spec.getServiceAddress();

        if (serviceAddress == null) {
            profile.setServiceAddress(
                    addressService.getAddress(JurisdictionType.UNITED_KINGDOM)
            );
        } else if (!isEmptyAddress(serviceAddress)) {
            profile.setServiceAddress(serviceAddress);
        }

        profile.setName(Objects.requireNonNullElse(spec.getName(),"Test Data Generator " + acspNumber + " Company Ltd"));
        profile.setLinksSelf(LINK_STEM + acspNumber);
        if (spec.getAmlDetails() != null) {
            List<AmlDetails> amlDetailsList = new ArrayList<>();
            for (AmlRequest amlRequest : spec.getAmlDetails()) {
                var amlDetails = new AmlDetails();
                amlDetails.setSupervisoryBody(amlRequest.getSupervisoryBody());
                amlDetails.setMembershipDetails(amlRequest.getMembershipDetails());
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
                    addressService.getCountryOfResidence(JurisdictionType.ENGLAND));
            profile.setSoleTraderDetails(soleTraderDetails);
        }
        AuditDetails created = new AuditDetails();
        created.setAt(Instant.now());
        created.setBy("TestDataGenerator");
        created.setType("acsp_delta");
        profile.setCreated(created);

        AuditDetails updated = new AuditDetails();
        updated.setAt(Instant.now());
        updated.setBy("TestDataGenerator");
        updated.setType("acsp_delta");
        profile.setUpdated(updated);
        profile.setDeltaAt(String.valueOf(System.currentTimeMillis()));

        AcspProfile savedProfile = repository.save(profile);
        return new AcspProfileResponse(savedProfile);
    }

    @Override
    public boolean delete(String acspNumber) {
        var existingProfile = repository.findById(acspNumber);
        existingProfile.ifPresent(repository::delete);
        return existingProfile.isPresent();
    }

    @Override
    public Optional<AcspProfile> getAcspProfile(String acspNumber) {
        try {
            return repository.findById(acspNumber);
        } catch (Exception ex) {
            LOG.error("Error retrieving ACSP profile for acspNumber: {} " + acspNumber, ex);
            return Optional.empty();
        }
    }

    private boolean isEmptyAddress(Address address) {
        return (address.getPremise() == null || address.getPremise().isBlank())
                && (address.getAddressLine1() == null || address.getAddressLine1().isBlank())
                && (address.getAddressLine2() == null || address.getAddressLine2().isBlank())
                && (address.getCountry() == null || address.getCountry().isBlank())
                && (address.getLocality() == null || address.getLocality().isBlank())
                && (address.getPostalCode() == null || address.getPostalCode().isBlank());
    }
}
