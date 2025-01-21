package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Collections;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspProfileServiceImpl implements DataService<AcspProfileData, AcspProfileSpec> {
    private static final String LINK_STEM = "/authorised-corporate-service-providers/";

    @Autowired
    private AcspRepository repository;
    @Autowired
    private RandomService randomService;

    @Override
    public AcspProfileData create(AcspProfileSpec spec) throws DataException {
        if (spec == null) {
            throw new DataException("AcspProfileSpec cannot be null");
        }

        String randomId = randomService.getString(8);
        AcspProfile profile = new AcspProfile();
        profile.setId(String.valueOf(randomId));
        profile.setVersion(0L);
        profile.setStatus(Objects.requireNonNullElse(spec.getStatus(), "active"));
        profile.setType(Objects.requireNonNullElse(spec.getType(), "ltd"));
        profile.setAcspNumber(randomId);
        profile.setName("Test Data Generator " + randomId + " Company Ltd");
        profile.setAmlDetails(Objects.requireNonNullElse(spec.getAmlDetails(),
                Collections.singletonList(new AcspProfile.AmlDetail() {{
                        setSupervisoryBody("association-of-chartered-certified-accountants-acca");
                        setMembershipDetails("123");
                        }
                })));
        profile.setLinksSelf(LINK_STEM + randomId);
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
