package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.AcspProfileRepository;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class AcspProfileServiceImpl implements AcspProfileService {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private static final String LINK_STEM = "/authorised-corporate-service-providers/";

    @Autowired
    private AcspProfileRepository repository;

    @Override
    public AcspProfile create(AcspProfileSpec spec) {
        final String acspNumber = spec.getAcspNumber();

        Instant now = Instant.now();

        AcspProfile profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setVersion(0L);

        AcspProfile.Data data = new AcspProfile.Data();
        data.setAcspNumber(acspNumber);
        data.setName(spec.getName());
        data.setEtag(spec.getEtag());
        data.setLinks(createLinks(acspNumber));
        profile.setData(data);

        AcspProfile.Audit created = new AcspProfile.Audit();
        created.setAt(now);
        created.setBy("system"); // Default creator
        created.setType("acsp_profile_creation");
        profile.setCreated(created);

        AcspProfile.Audit updated = new AcspProfile.Audit();
        updated.setAt(now);
        updated.setBy("system"); // Default updater
        updated.setType("acsp_profile_update");
        profile.setUpdated(updated);

        profile.setDeltaAt(generateDeltaAt(now));

        return repository.save(profile);
    }

    /**
     * @param companySpec
     * @return
     * @throws DataException
     */
    @Override
    public AcspProfile create(CompanySpec companySpec) throws DataException {
        return null;
    }

    @Override
    public boolean delete(String acspNumber) {
        Optional<AcspProfile> profile = repository.findById(acspNumber);
        profile.ifPresent(repository::delete);
        return profile.isPresent();
    }

    @Override
    public boolean profileExists(String acspNumber) {
        return repository.findById(acspNumber).isPresent();
    }

    @Override
    public boolean acspExists(String acspNumber) {
        return repository.findById(acspNumber).isPresent();
    }

    /**
     * @param acspNumber the ACSP number to delete
     */
    @Override
    public void deleteAcspData(String acspNumber) {

    }

    /**
     * @param spec specification for the new ACSP profile
     * @return
     */
    @Override
    public AcspProfileData createAcspData(AcspProfileSpec spec) {
        return null;
    }

    private AcspProfile.Links createLinks(String acspNumber) {
        AcspProfile.Links links = new AcspProfile.Links();
        links.setSelf(LINK_STEM + acspNumber);
        return links;
    }

    private String generateDeltaAt(Instant now) {
        return String.valueOf(now.toEpochMilli());
    }
}
