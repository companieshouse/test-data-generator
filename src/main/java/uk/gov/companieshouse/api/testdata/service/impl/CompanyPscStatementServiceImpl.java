package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
public class CompanyPscStatementServiceImpl implements
        DataService<CompanyPscStatement, CompanySpec> {

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
     * @param spec the specification of the company for which the PSC statement is created
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

        List<PscStatement> availableStatements;

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())) {
            availableStatements = Arrays.asList(
                    PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED,
                    PscStatement.BENEFICIAL_ACTIVE_OR_CEASED,
                    PscStatement.NO_ACTIVE_BENEFICIAL_OWNER,
                    PscStatement.AT_LEAST_ONE_BENEFICIAL_OWNER);
        } else {
            availableStatements = Arrays.asList(PscStatement.values());
            availableStatements = availableStatements.stream()
                    .filter(s -> !s.getStatement().contains("beneficial-owner"))
                    .collect(Collectors.toList());
        }

        if (spec.getWithdrawnPscStatements() != null && spec.getWithdrawnPscStatements() > 0) {
            int randomIndex = ThreadLocalRandom.current().nextInt(availableStatements.size());
            companyPscStatement.setStatement(availableStatements.get(randomIndex).getStatement());

            LocalDate ceasedDate = LocalDate.now().minusDays(3);
            companyPscStatement.setCeasedOn(ceasedDate.atStartOfDay(ZONE_ID_UTC).toInstant());
        } else if (CompanyType.OVERSEA_COMPANY.equals(spec.getCompanyType())
                && (spec.getNumberOfPsc() == null || spec.getNumberOfPsc() == 0)) {
            companyPscStatement.setStatement(
                    PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
        } else if (spec.getNumberOfPsc() != null && spec.getNumberOfPsc() > 0) {
            if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())) {
                companyPscStatement.setStatement(
                        PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED.getStatement());
            } else if (CompanyType.OVERSEA_COMPANY.equals(spec.getCompanyType())) {
                companyPscStatement.setStatement(
                        PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL
                                .getStatement());
            } else {
                companyPscStatement.setStatement(
                        PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
            }
        } else {
            companyPscStatement.setStatement(
                    PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
        }

        companyPscStatement.setCreatedAt(dateTimeNow);

        return repository.save(companyPscStatement);
    }

    /**
     * Creates multiple PSC statements based on the active
     * and withdrawn counts specified in the CompanySpec.
     * This method orchestrates calls to the single-statement create method.
     *
     * @param spec The CompanySpec containing the desired counts
     *             for active and withdrawn PSC statements.
     * @return A list of created CompanyPscStatement objects.
     */
    public List<CompanyPscStatement> createPscStatements(CompanySpec spec) {
        List<CompanyPscStatement> createdStatements = new ArrayList<>();

        Integer withdrawnPscStatements = spec.getWithdrawnPscStatements();
        Integer activePscStatements = spec.getActivePscStatements();

        boolean specificPscStatementsRequested = (withdrawnPscStatements != null
                && withdrawnPscStatements > 0)
                || (activePscStatements != null && activePscStatements > 0);

        if (specificPscStatementsRequested) {
            if (withdrawnPscStatements != null && withdrawnPscStatements > 0) {
                for (int i = 0; i < withdrawnPscStatements; i++) {
                    CompanySpec tempSpec = new CompanySpec();
                    tempSpec.setCompanyNumber(spec.getCompanyNumber());
                    tempSpec.setCompanyType(spec.getCompanyType());
                    tempSpec.setAccountsDueStatus(spec.getAccountsDueStatus());
                    tempSpec.setWithdrawnPscStatements(1);
                    tempSpec.setNumberOfPsc(0);
                    createdStatements.add(this.create(tempSpec));
                }
            }

            if (activePscStatements != null && activePscStatements > 0) {
                for (int i = 0; i < activePscStatements; i++) {
                    CompanySpec tempSpec = new CompanySpec();
                    tempSpec.setCompanyNumber(spec.getCompanyNumber());
                    tempSpec.setCompanyType(spec.getCompanyType());
                    tempSpec.setAccountsDueStatus(spec.getAccountsDueStatus());
                    tempSpec.setWithdrawnPscStatements(0);
                    tempSpec.setNumberOfPsc(1);
                    createdStatements.add(this.create(tempSpec));
                }
            }
        } else {
            createdStatements.add(this.create(spec));
        }
        return createdStatements;
    }

    @Override
    public boolean delete(String companyNumber) {
        List<CompanyPscStatement> statementsToDelete
                = repository.findAllByCompanyNumber(companyNumber);

        if (!statementsToDelete.isEmpty()) {
            repository.deleteAll(statementsToDelete);
            return true;
        }
        return false;
    }

    public enum PscStatement {
        NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL(
                "no-individual-or-entity-with-signficant-control"),
        PSC_EXISTS_BUT_NOT_IDENTIFIED("psc-exists-but-not-identified"),
        ALL_BENEFICIAL_OWNERS_IDENTIFIED("all-beneficial-owners-identified"),
        BENEFICIAL_ACTIVE_OR_CEASED("somebody-has-become-or-ceased-to-be-a-beneficial-owner"),
        NO_ACTIVE_BENEFICIAL_OWNER("nobody-has-become-or-ceased-to-be-a-beneficial-owner"),
        AT_LEAST_ONE_BENEFICIAL_OWNER("at-least-one-beneficial-owner-unidentified");
        private final String statement;

        PscStatement(String statement) {
            this.statement = statement;
        }

        public String getStatement() {
            return statement;
        }
    }
}