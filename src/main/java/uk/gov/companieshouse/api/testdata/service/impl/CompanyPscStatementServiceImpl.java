package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;
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

        pscStatement.setKind("persons-with-significant-control-statement");
        Links links = new Links();
        links.setSelf(URL_PREFIX + pscStatement.getCompanyNumber()
                + PSC_SUFFIX
                + pscStatement.getId());
        pscStatement.setLinks(links);

        pscStatement.setNotifiedOn(LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant());

        if (spec.getCompanyType() == CompanyType.REGISTERED_OVERSEAS_ENTITY) {
            pscStatement.setStatement(PscStatement.ALL_BENEFICIAL_OWNERS_IDENTIFIED.getStatement());
        } else if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            pscStatement.setStatement(PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
        } else if (spec.getPscActive() == null || Boolean.TRUE.equals(spec.getPscActive())) {
            pscStatement.setStatement(PscStatement.PSC_EXISTS_BUT_NOT_IDENTIFIED.getStatement());
        } else if (Boolean.FALSE.equals(spec.getPscActive())) {
            pscStatement.setStatement(PscStatement.BENEFICIAL_ACTIVE_OR_CEASED.getStatement());
        } else {
            pscStatement.setStatement(
                    PscStatement.NO_INDIVIDUAL_OR_ENTITY_WITH_SIGNIFICANT_CONTROL.getStatement());
        }

        return repository.save(pscStatement);
    }


    public List<CompanyPscStatement> createPscStatements(CompanySpec spec) {
        List<CompanyPscStatement> createdStatements = new ArrayList<>();

        Integer withdrawnPscStatementsCount = spec.getWithdrawnStatements();
        Integer activePscStatementsCount = spec.getActiveStatements();
        Integer numberOfPsc = spec.getNumberOfPsc();

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


        if (specificWithdrawnRequested || specificActiveOrNumberOfPscRequested) {
            if (specificWithdrawnRequested) {
                addWithdrawnPscStatements(spec, withdrawnPscStatementsCount, createdStatements);
            }
            if (specificActiveOrNumberOfPscRequested) {
                addActivePscStatements(spec, effectiveActivePscCount, createdStatements);
            }
        } else {
            createdStatements.add(this.create(spec));
        }
        return createdStatements;
    }

    private void addWithdrawnPscStatements(
            CompanySpec spec, Integer count, List<CompanyPscStatement> statements) {
        for (int i = 0; i < count; i++) {
            CompanySpec tempSpec = new CompanySpec();
            tempSpec.setCompanyNumber(spec.getCompanyNumber());
            tempSpec.setCompanyType(spec.getCompanyType());
            tempSpec.setWithdrawnStatements(1);
            tempSpec.setNumberOfPsc(0);
            tempSpec.setPscActive(false);
            statements.add(this.create(tempSpec));
        }
    }

    private void addActivePscStatements(
            CompanySpec spec, Integer count, List<CompanyPscStatement> statements) {
        for (int i = 0; i < count; i++) {
            CompanySpec tempSpec = new CompanySpec();
            tempSpec.setCompanyNumber(spec.getCompanyNumber());
            tempSpec.setCompanyType(spec.getCompanyType());
            tempSpec.setWithdrawnStatements(0);
            tempSpec.setNumberOfPsc(1);
            tempSpec.setPscActive(true);
            statements.add(this.create(tempSpec));
        }
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

    private static final String URL_PREFIX = "/company/";

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