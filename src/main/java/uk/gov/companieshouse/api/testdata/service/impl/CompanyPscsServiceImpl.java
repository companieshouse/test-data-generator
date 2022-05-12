package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Optional;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyPscsServiceImpl implements DataService<CompanyPscs> {

    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;

    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyPscsRepository repository;


    @Override
    public CompanyPscs create(CompanySpec spec) throws DataException {

        final String companyNumber = spec.getCompanyNumber();
        CompanyPscs companyPsc = new CompanyPscs();

        Instant dateTimeNow = Instant.now();
        Instant dateNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        String id = this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        companyPsc.setId(id);
        companyPsc.setCreatedAt(dateTimeNow);
        companyPsc.setUpdatedAt(dateTimeNow);
        companyPsc.setCompanyNumber(companyNumber);

        companyPsc.setNaturesOfControl(Collections.singletonList("voting-rights-75-to-100-percent-as-trust"));
        companyPsc.setMiddleName("middle");
        companyPsc.setForename("forename");
        companyPsc.setTitle("Mrs");
        companyPsc.setDateOfBirth(Instant.now().minus( 69, ChronoUnit.DAYS));
        companyPsc.setNationality("British");
        companyPsc.setPostalCode("CF14 3UZ");
        companyPsc.setPremises("1");
        companyPsc.setLocality("Cardiff");
        companyPsc.setCountry("Wales");
        companyPsc.setAddressLine1("34 Silver Street");
        companyPsc.setCountryOfResidence("Wales");

        companyPsc.setNotifiedOn(dateNow);
        String etag = this.randomService.getEtag();
        companyPsc.setEtag(etag);
        companyPsc.setPscId(randomService.getString(30));
        companyPsc.setNotificationId(randomService.getString(30));

        return repository.save(differentiatePsc(companyPsc));
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<List<CompanyPscs>> existingPscs = repository.findByCompanyNumber(companyNumber);
        existingPscs.ifPresent(repository::deleteAll);
        return existingPscs.isPresent();
    }

    private CompanyPscs differentiatePsc(CompanyPscs companyPsc) {

        String pscType;
        String linkType;

        switch ((int) repository.count()){
            case 0:
                pscType = "individual-person-with-significant-control";
                linkType = "individual";
                break;
            case 1:
                pscType = "legal-person-person-with-significant-control";
                linkType = "legal-person";
                break;
            default:
                pscType = "corporate-entity-person-with-significant-control";
                linkType = "corporate-entity";
        }

        companyPsc.setKind(pscType);
        companyPsc.setSurname(pscType);
        companyPsc.setName(companyPsc.getTitle() + " " + companyPsc.getForename()
                + " " + companyPsc.getMiddleName() + " " + companyPsc.getSurname());

        Links links = new Links();
        links.setSelf("/company/" + companyPsc.getCompanyNumber() + "/persons-with-significant-control/" + linkType +"/" + companyPsc.getId());
        companyPsc.setSelf(links);
        return companyPsc;
    }
}
