package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.Address;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.AmlDetail;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.Links;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.SensitiveData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspProfileServiceImpl implements AcspProfileService {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final String LINK_STEM = "/authorised-corporate-service-providers/";

    @Autowired
    private RandomService randomService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private AcspProfileRepository repository;

    @Override
    public AcspProfile create(AcspSpec spec) {
        final String acspNumber = String.valueOf(spec.getAcspNumber());
        final Jurisdiction jurisdiction = spec.getJurisdiction();
        final String companyStatus = spec.getStatus();
        final String companyType = spec.getCompanyType();

        // Use current date if needed; here we rely on given dates from the JSON example
        // LocalDate now = LocalDate.now();

        AcspProfile profile = new AcspProfile();

        // Set the ID and version
        profile.setId(acspNumber);
        profile.setVersion(0L);

        // Fields from spec or defaults
        profile.setAcspNumber(acspNumber);
        profile.setCompanyName("Example ACSP Ltd"); // from the given JSON
        profile.setType(Objects.requireNonNullElse(companyType, "limited-company"));
        profile.setStatus(Objects.requireNonNullElse(companyStatus, "active"));
        //profile.setJurisdiction(jurisdiction.toString());

        // Set other data fields from example JSON:
        // notified_from, business_sector, etag
//        profile.getData().setNotifiedFrom(Instant.parse("2024-04-02T00:00:00.000Z"));
//        profile.getData().setBusinessSector("financial-institutions");
        //profile.getData().setEtag("47e85fcf644420129b4388ef9c87496794620893");

        // Registered Office Address from JSON
        Address roa = new Address();
        roa.setCareOf("Jane Smith");
        roa.setAddressLine1("456 Another Street");
        roa.setAddressLine2("Floor 2");
        roa.setCountry("united-kingdom");
        roa.setLocality("Manchester");
        roa.setPoBox("PO Box 123");
        roa.setPostalCode("M1 2AB");
        roa.setPremises("Another Building");
        roa.setRegion("Greater Manchester");
//        profile.setRegisteredOfficeAddress(roa);

        // Service Address from JSON
        Address serviceAddress = new Address();
        serviceAddress.setCareOf("Jane Smith");
        serviceAddress.setAddressLine1("456 Another Street");
        serviceAddress.setAddressLine2("Floor 2");
        serviceAddress.setCountry("united-kingdom");
        serviceAddress.setLocality("Manchester");
        serviceAddress.setPoBox("PO Box 123");
        serviceAddress.setPostalCode("M1 2AB");
        serviceAddress.setPremises("Another Building");
        serviceAddress.setRegion("Greater Manchester");
//        profile.getData().setServiceAddress(serviceAddress);

        // AML Details from JSON
        AmlDetail amlDetail = new AmlDetail();
        amlDetail.setSupervisoryBody("financial-conduct-authority-fca");
        amlDetail.setMembershipDetails("Membership ID: FCA654321");
//        profile.getData().setAmlDetails(Collections.singletonList(amlDetail));

        // Links
        Links links = new Links();
        links.setSelf(LINK_STEM + acspNumber);
        profile.setLinks(links);

        // Sensitive data
        SensitiveData sensitiveData = new SensitiveData();
        sensitiveData.setEmail("john.doe@example.com");
        //profile.setSensitiveData(sensitiveData);

        return repository.save(profile);
    }

    @Override
    public boolean delete(long acspNumber) {
        Optional<AcspProfile> profile = repository.findById(String.valueOf(acspNumber));
        profile.ifPresent(repository::delete);
        return profile.isPresent();
    }

    @Override
    public boolean acspProfileExists(long acspNumber) {
        return repository.findById(String.valueOf(acspNumber)).isPresent();
    }
}
