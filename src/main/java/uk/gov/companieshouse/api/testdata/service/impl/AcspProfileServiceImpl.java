package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.ZoneId;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile.Links;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;
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
        final String  acspNumber = String.valueOf(spec.getAcspNumber());
        final String companyStatus = spec.getStatus();
        final String companyType = spec.getCompanyType();

        AcspProfile profile = new AcspProfile();

        // Set the ID and version
        profile.setId(String.valueOf(acspNumber));
        profile.setVersion(0L);

        // Fields from spec
        profile.setAcspNumber(Long.parseLong(acspNumber));
        profile.setCompanyName("Example ACSP Ltd"); // from the given JSON
        profile.setType(Objects.requireNonNullElse(companyType, "limited-company"));
        profile.setStatus(Objects.requireNonNullElse(companyStatus, "active"));

        // Links
        Links links = new Links();
        links.setSelf(LINK_STEM + acspNumber);
        profile.setLinks(links);

        repository.save(profile);
        return new AcspProfile();
    }

    /**
     * @param id the ID of the entity to delete
     * @return
     */
    @Override
    public boolean delete(String id) {
        Optional<AcspProfile> profile = repository.findById(String.valueOf(id));
        profile.ifPresent(repository::delete);
        return profile.isPresent();
    }

    @Override
    public boolean acspProfileExists(long acspNumber) {
        return repository.findById(String.valueOf(acspNumber)).isPresent();
    }
}
