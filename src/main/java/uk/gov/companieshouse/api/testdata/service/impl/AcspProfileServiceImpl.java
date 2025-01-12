package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.*;
import uk.gov.companieshouse.api.testdata.repository.AcspRepository;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.Optional;

public class AcspProfileServiceImpl implements AcspProfileService {

    private static final String LINK_STEM = "/authorised-corporate-service-providers/";
    private static final String ACSP_PREFIX = "PlaywrightACSP";

    @Autowired
    private AcspRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public AcspProfileData create(AcspProfileSpec acspProfileSpec) throws DataException {
        var randomId = randomService.getNumber(8);
        final var acspProfile = new AcspProfile();

        acspProfile.setId(ACSP_PREFIX + (randomId));
        acspProfile.getVersion(0L);
        acspProfile.setAcspNumber(ACSP_PREFIX + (randomId));
        acspProfile.setStatus("active");
        acspProfile.setType("limited-company");
        acspProfile.setName("Example CompanyLtd");
        acspProfile.setLinksSelf(LINK_STEM + ACSP_PREFIX + randomId);

        repository.save(acspProfile);
        return new AcspProfileData(acspProfile.getId(), acspProfile.getAcspNumber());
    }

    @Override
    public boolean delete(String acspProfileId) {
        var acspProfile = repository.findById(acspProfileId);
        acspProfile.ifPresent(repository::delete);
        return acspProfile.isPresent();
    }

    @Override
    public Optional<AcspProfile> getAcspProfileById(String acspProfileId) {
        return repository.findById(acspProfileId);
    }
}
