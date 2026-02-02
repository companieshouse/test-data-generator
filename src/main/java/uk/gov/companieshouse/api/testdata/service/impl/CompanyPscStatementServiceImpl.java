package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private static final String URL_PREFIX = "/company/";

    @Autowired
    private CompanyPscStatementRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public CompanyPscStatement create(CompanySpec spec) {
        var pscStatement = new CompanyPscStatement();

        String pscStatementId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);
        pscStatement.setId(pscStatementId);
        pscStatement.setPscStatementId(pscStatementId);
        pscStatement.setCompanyNumber(spec.getCompanyNumber());
        pscStatement.setEtag(randomService.getEtag());
        pscStatement.setCreatedAt(Instant.now());
        pscStatement.setUpdatedAt(pscStatement.getCreatedAt());

        pscStatement.setKind("persons-with-significant-control-statement");
        var links = new Links();
        links.setSelf(URL_PREFIX + pscStatement.getCompanyNumber()
                + PSC_SUFFIX
                + pscStatement.getId());
        pscStatement.setLinks(links);

        pscStatement.setNotifiedOn(LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant());

        if (spec.getCompanyType() == CompanyType.REGISTERED_OVERSEAS_ENTITY) {
            pscStatement.setStatement(PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED.getStatement());
        } else if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            pscStatement.setStatement(PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
        } else if (Boolean.TRUE.equals(spec.getPscActive())) {
            pscStatement.setStatement(PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
        } else if (spec.getWithdrawnStatements() > 0) {
            pscStatement.setStatement(PscStatement.BENEFICIAL_ACTIVE_OR_CEASED.getStatement());
            pscStatement.setCeasedOn(LocalDate.now().minusDays(30)
                    .atStartOfDay(ZONE_ID_UTC).toInstant());
        } else {
            pscStatement.setStatement(
                    PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
        }
        if (Boolean.TRUE.equals(spec.getCompanyWithDataStructureOnly())) {
            return pscStatement;
        }

        return repository.save(pscStatement);
    }

    public List<CompanyPscStatement> createPscStatements(CompanySpec spec) {
        List<CompanyPscStatement> generatedStatements = new ArrayList<>();

        Integer withdrawnPscStatementsCount = spec.getWithdrawnStatements();
        Integer activePscStatementsCount = spec.getActiveStatements();
        Integer numberOfPsc = spec.getNumberOfPscs();

        boolean specificWithdrawnRequested = withdrawnPscStatementsCount
                != null && withdrawnPscStatementsCount > 0;

        int effectiveActivePscCount;
        if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            effectiveActivePscCount = 1;
        } else {
            effectiveActivePscCount = Objects.requireNonNullElseGet(activePscStatementsCount,
                    () -> Objects.requireNonNullElse(numberOfPsc, 0));
        }

        boolean specificActiveOrNumberOfPscRequested = effectiveActivePscCount > 0;

        List<CompanyPscStatement> withdrawn = new ArrayList<>();
        List<CompanyPscStatement> active = new ArrayList<>();
        // No need for singleDefault list if it's no longer being conditionally added

        if (specificWithdrawnRequested || specificActiveOrNumberOfPscRequested) {
            if (specificWithdrawnRequested) {
                withdrawn = generateWithdrawnPscStatements(spec, withdrawnPscStatementsCount);
            }
            if (specificActiveOrNumberOfPscRequested) {
                active = generateActivePscStatements(spec, effectiveActivePscCount);
            }
        }
        // The 'else' block that added a default statement has been removed.

        generatedStatements.addAll(withdrawn);
        generatedStatements.addAll(active);
        // singleDefault list and its addAll call is removed.

        return generatedStatements;
    }

    protected List<CompanyPscStatement> generateWithdrawnPscStatements(
            CompanySpec spec, Integer count) {

        List<CompanyPscStatement> generatedList = new ArrayList<>();

        if (count == null || count <= 0) {
            return generatedList;
        }

        for (var i = 0; i < count; i++) {
            var tempSpec = new CompanySpec();
            tempSpec.setCompanyNumber(spec.getCompanyNumber());
            tempSpec.setCompanyType(spec.getCompanyType());
            tempSpec.setWithdrawnStatements(1);
            tempSpec.setNumberOfPscs(0);
            tempSpec.setPscActive(false);
            if (spec.getCompanyWithDataStructureOnly() != null) {
                tempSpec.setCompanyWithDataStructureOnly(spec.getCompanyWithDataStructureOnly());
            }
            generatedList.add(this.create(tempSpec));
        }
        return generatedList;
    }

    protected List<CompanyPscStatement> generateActivePscStatements(
            CompanySpec spec, Integer count) {

        List<CompanyPscStatement> generatedList = new ArrayList<>();

        if (count == null || count <= 0) {
            return generatedList;
        }

        for (var i = 0; i < count; i++) {
            var tempSpec = new CompanySpec();
            tempSpec.setCompanyNumber(spec.getCompanyNumber());
            tempSpec.setCompanyType(spec.getCompanyType());
            tempSpec.setWithdrawnStatements(0);
            tempSpec.setNumberOfPscs(1);
            tempSpec.setPscActive(true);
            if (spec.getCompanyWithDataStructureOnly() != null) {
                tempSpec.setCompanyWithDataStructureOnly(spec.getCompanyWithDataStructureOnly());
            }
            generatedList.add(this.create(tempSpec));
        }
        return generatedList;
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