package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspProfileServiceImpl implements DataService<AcspProfileData, AcspProfileSpec> {
    private static final String LINK_STEM = "/authorised-corporate-service-providers/";

    @Autowired
    private AcspProfileRepository repository;
    @Autowired
    private RandomService randomService;

    public AcspProfileData create(AcspProfileSpec spec) throws DataException {
        var randomId = randomService.getString(8);
        var acspNumber = Objects.requireNonNullElse(spec.getAcspNumber(), randomId);

        var profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setVersion(0L);
        profile.setStatus(Objects.requireNonNullElse(spec.getStatus(), "active"));
        profile.setType(Objects.requireNonNullElse(spec.getType(), "limited-company"));
        profile.setAcspNumber(acspNumber);
        profile.setName("Test Data Generator " + acspNumber + " Company Ltd");
        profile.setLinksSelf(LINK_STEM + acspNumber);
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
