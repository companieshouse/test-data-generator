package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyPscStatementServiceImpl implements DataService<CompanyPscStatement, CompanySpec> {

    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final int ID_LENGTH = 10;
    private static final int SALT_LENGTH = 8;
    private static final String PSC_SUFFIX = "/persons-with-significant-control-statements/";

    @Autowired
    private RandomService randomService;

    @Autowired
    private CompanyPscStatementRepository repository;

    /**
     * creates PSC statement deliberately misspelt as 'signficant'
     * to match api-enumeration and live data
     * can be corrected when api-enumeration is fixed.
     * psc-statement-data-api java services utilises the enum values and
     * correct spelling will cause failures in environments that run the java service
     *
     * @param spec
     * @return
     */
    @Override
    public CompanyPscStatement create(CompanySpec spec) {
        final String companyNumber = spec.getCompanyNumber();
        final String accountsDueStatus = spec.getAccountsDueStatus();

        CompanyPscStatement companyPscStatement = new CompanyPscStatement();

        Instant dateTimeNow = Instant.now();
        Instant dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();

        if (StringUtils.hasText(accountsDueStatus)) {
            var now = randomService.generateAccountsDueDateByStatus(accountsDueStatus);
            dateTimeNow = now.atTime(LocalTime.now()).atZone(ZONE_ID_UTC).toInstant();
            dateNow = now.atStartOfDay(ZONE_ID_UTC).toInstant();
        }

        String id = this.randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        companyPscStatement.setId(id);
        companyPscStatement.setUpdatedAt(dateTimeNow);
        companyPscStatement.setCompanyNumber(companyNumber);
        companyPscStatement.setPscStatementId(id);

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + PSC_SUFFIX + id);

        companyPscStatement.setLinks(links);
        companyPscStatement.setNotifiedOn(dateNow);

        String etag = this.randomService.getEtag();
        companyPscStatement.setEtag(etag);

        companyPscStatement.setKind("persons-with-significant-control-statement");

        if (CompanyType.OVERSEA_COMPANY.equals(spec.getCompanyType())
                && (spec.getNumberOfPsc() == null || spec.getNumberOfPsc() == 0)) {
            companyPscStatement.setStatement(
                    PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
        } else if (spec.getNumberOfPsc() != null && spec.getNumberOfPsc() > 0) {
            if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())) {
                companyPscStatement.setStatement(
                        PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED.getStatement());
            } else if (CompanyType.OVERSEA_COMPANY.equals(spec.getCompanyType())) {
                companyPscStatement.setStatement(
                        PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
            } else {
                companyPscStatement.setStatement(
                        PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
            }
        } else {
            companyPscStatement.setStatement(PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
        }

        companyPscStatement.setCreatedAt(dateTimeNow);

        return repository.save(companyPscStatement);
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<CompanyPscStatement> existingStatement
                = repository.findByCompanyNumber(companyNumber);
        existingStatement.ifPresent(repository::delete);
        return existingStatement.isPresent();
    }

    public enum PscStatement {
        ALL_BENEFICIAL_OWNERS_IDENTIFIED("all-beneficial-owners-identified"),
        NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL(
                "no-individual-or-entity-with-signficant-control"),
        PSC_EXISTS_BUT_NOT_IDENTIFIED("psc-exists-but-not-identified");

        private final String statement;

        PscStatement(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
    }
}
