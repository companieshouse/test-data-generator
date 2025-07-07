package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DisqualificationsSpec;
import uk.gov.companieshouse.api.testdata.repository.DisqualificationsRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class DisqualificationsServiceImpl implements DataService<Disqualifications, CompanySpec> {

    private static final Logger LOG
            = LoggerFactory.getLogger(String.valueOf(DisqualificationsServiceImpl.class));
    private static final String DEFAULT_NAME = "FIRSTNAME SURNAME";
    private static final String URL_DISQUALIFIED_OFFICERS_PREFIX = "/disqualified-officers/";
    private static final String URL_CORPORATE_SUFFIX = "corporate/";
    private static final String URL_NATURAL_SUFFIX = "natural/";
    private static final String DEFAULT_CASE_IDENTIFIER_PREFIX = "INV";
    private static final String DISQUALIFICATION_COURT_NAME = "Insolvency Service";
    private static final Instant DISQUALIFICATION_DATE = Instant.now();
    private static final Instant LAST_VARIATION_VARIED_ON = LocalDate.of(2021, 2, 17)
            .atStartOfDay(ZoneId.of("UTC")).toInstant();
    private static final String LAST_VARIATION_CASE_IDENTIFIER = "1";
    private static final String LAST_VARIATION_COURT_NAME = "SWINDLERS";
    private static final List<String> PERM_COMPANY_NAMES = Arrays.asList("123 LTD", "321 LTD");
    private static final String PERM_COURT_NAME = "rinder";
    private static final Instant PERM_EXPIRES_ON = LocalDate.of(2016, 4, 12)
            .atStartOfDay(ZoneId.of("UTC")).toInstant();
    private static final Instant PERM_GRANTED_ON = LocalDate.of(2014, 2, 3)
            .atStartOfDay(ZoneId.of("UTC")).toInstant();
    private static final String PERM_PURPOSE = "ALPHABET";

    @Autowired
    private DisqualificationsRepository repository;
    @Autowired
    private RandomService randomService;
    @Autowired
    private AddressService addressService;

    @Override
    public Disqualifications create(CompanySpec spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanySpec cannot be null");
        }

        List<DisqualificationsSpec> disqualificationsSpecs = spec.getDisqualifiedOfficers();
        List<Disqualifications> savedDisqualifications = new ArrayList<>();

        LOG.info("Starting creation of Disqualifications for company number: "
                + spec.getCompanyNumber());

        if (disqualificationsSpecs != null && !disqualificationsSpecs.isEmpty()) {
            for (DisqualificationsSpec disqSpec : disqualificationsSpecs) {
                savedDisqualifications.add(createDisqualificationFromSpec(spec, disqSpec));
            }
        } else {
            var defaultSpec = new DisqualificationsSpec();
            defaultSpec.setDisqualificationType("default-type");
            defaultSpec.setCorporateOfficer(false);
            savedDisqualifications.add(createDisqualificationFromSpec(spec, defaultSpec));
        }

        return savedDisqualifications.get(savedDisqualifications.size() - 1);
    }

    private Disqualifications createDisqualificationFromSpec(
            CompanySpec companySpec, DisqualificationsSpec spec) {
        var disqualifications = new Disqualifications();
        disqualifications.setId(generateId());
        disqualifications.setCompanyNumber(companySpec.getCompanyNumber());
        disqualifications.setPersonNumber(randomService.getNumber(10));
        disqualifications.setCountryOfRegistration(
                addressService.getCountryOfResidence(companySpec.getJurisdiction()));
        disqualifications.setEtag(this.randomService.getEtag());
        disqualifications.setName(DEFAULT_NAME);
        disqualifications.setOfficerDisqId(randomService.getString(10));
        disqualifications.setOfficerDetailId(randomService.getString(10));
        disqualifications.setOfficerIdRaw(randomService.getString(8));
        disqualifications.setIsCorporateOfficer(spec.getCorporateOfficer());
        disqualifications.setDateOfBirth(java.util.Date.from(
                java.time.LocalDate.of(1990, 1, 1)
                        .atStartOfDay(java.time.ZoneId.of("UTC")).toInstant()
        ));

        String officerSuffix = Boolean.TRUE.equals(spec.getCorporateOfficer())
                ? URL_CORPORATE_SUFFIX
                : URL_NATURAL_SUFFIX;

        disqualifications.setLinksSelf(
                URL_DISQUALIFIED_OFFICERS_PREFIX + officerSuffix + disqualifications.getId()
        );

        disqualifications.setAddress(addressService.getAddress(companySpec.getJurisdiction()));

        disqualifications.setDisqCaseIdentifier(DEFAULT_CASE_IDENTIFIER_PREFIX
                + randomService.getString(4));
        disqualifications.setDisqCompanyNames(
                Collections.singletonList("COMPANY " + companySpec.getCompanyNumber() + " LIMITED")
        );
        disqualifications.setDisqCourtName(DISQUALIFICATION_COURT_NAME);
        disqualifications.setDisqDisqualificationType(spec.getDisqualificationType());
        disqualifications.setDisqDisqualifiedFrom(DISQUALIFICATION_DATE);
        disqualifications.setDisqDisqualifiedUntil(DISQUALIFICATION_DATE
                .atZone(ZoneId.of("UTC"))
                .plusYears(15)
                .toInstant());

        disqualifications.setDisqHeardOn(DISQUALIFICATION_DATE
                .atZone(ZoneId.of("UTC"))
                .minusDays(1)
                .toInstant());

        disqualifications.setDisqLastVariationVariedOn(LAST_VARIATION_VARIED_ON);
        disqualifications.setDisqLastVariationCaseIdentifier(LAST_VARIATION_CASE_IDENTIFIER);
        disqualifications.setDisqLastVariationCourtName(LAST_VARIATION_COURT_NAME);

        disqualifications.setDisqReasonAct("default-act");
        disqualifications.setDisqReasonDescriptionIdentifier("default-description");
        disqualifications.setDisqReasonArticle("default-article");

        disqualifications.setPtaCompanyNames(PERM_COMPANY_NAMES);
        disqualifications.setPtaCourtName(PERM_COURT_NAME);
        disqualifications.setPtaExpiresOn(PERM_EXPIRES_ON);
        disqualifications.setPtaGrantedOn(PERM_GRANTED_ON);
        disqualifications.setPtaPurpose(PERM_PURPOSE);
        disqualifications.setDisqDisqualificationType(spec.getDisqualificationType());

        setTimestamps(disqualifications);

        var savedDisqualifications = repository.save(disqualifications);
        LOG.info("Successfully created and saved Disqualifications for company: "
                + companySpec.getCompanyNumber());
        return savedDisqualifications;
    }

    @Override
    public boolean delete(String companyNumber) {
        LOG.info("Attempting to delete Disqualifications with ID: " + companyNumber);

        Optional<List<Disqualifications>> existingDisqualifications
                = repository.findByCompanyNumber(companyNumber);

        if (existingDisqualifications.isPresent() && !existingDisqualifications.get().isEmpty()) {
            LOG.info("Found : " + existingDisqualifications.get().size()
                    + " disqualifications for company: "
                    + companyNumber + ". proceeding with deletion.");

            for (Disqualifications disqualifications : existingDisqualifications.get()) {
                repository.delete(disqualifications);
            }
            LOG.info("Successfully deleted Disqualifications for company: " + companyNumber);
            return true;
        } else {
            LOG.info("No Disqualifications found with ID: " + companyNumber);
            return false;
        }
    }

    private void setTimestamps(Disqualifications disqualifications) {
        disqualifications.setCreatedAt(Instant.now());
        disqualifications.setUpdatedAt(Instant.now());
        disqualifications.setDeltaAt(String.valueOf(System.currentTimeMillis()));
        LOG.debug("Set timestamps for disqualification record");
    }

    private String generateId() {
        return randomService.getString(24);
    }

    public List<Disqualifications> getDisqualifications(String companyNumber) {
        return repository.findByCompanyNumber(companyNumber)
                .orElse(Collections.emptyList());
    }
}