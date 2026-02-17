package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.RegisterItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanyMetricsServiceImpl implements DataService<CompanyMetrics, CompanySpec> {

    private static final Logger LOG = LoggerFactory.getLogger(String.valueOf(CompanyMetricsServiceImpl.class));

    private final CompanyMetricsRepository repository;

    private final RandomService randomService;

    @Autowired
    public CompanyMetricsServiceImpl(CompanyMetricsRepository repository, RandomService randomService) {
        super();
        this.repository = repository;
        this.randomService = randomService;
    }

    @Override
    public CompanyMetrics create(CompanySpec spec) {
        LOG.info("Starting creation of CompanyMetrics for company number: "
                + spec.getCompanyNumber());
        CompanyMetrics metrics = initializeMetrics(spec);

        setActivePscCount(metrics, spec);
        setPscCount(metrics, spec);
        setCeasedPscCount(metrics, spec);
        setActiveDirectorsCount(metrics, spec);

        if (spec.getRegisters() != null) {
            LOG.debug("Registers are provided. Creating registers for the company.");
            metrics.setRegisters(createRegisters(spec.getRegisters()));
        }
        if (Boolean.TRUE.equals(spec.getCompanyWithPopulatedStructureOnly())) {
            return metrics;
        }
        CompanyMetrics savedMetrics = repository.save(metrics);
        LOG.info("Successfully created and saved CompanyMetrics for company number: "
                + spec.getCompanyNumber());
        return savedMetrics;
    }

    @Override
    public boolean delete(String companyNumber) {
        LOG.info("Attempting to delete CompanyMetrics for company number: " + companyNumber);
        Optional<CompanyMetrics> existingMetric = repository.findById(companyNumber);

        if (existingMetric.isPresent()) {
            LOG.info("CompanyMetrics found for company number: "
                    + companyNumber + ". Proceeding with deletion.");
            repository.delete(existingMetric.get());
            LOG.info("Successfully deleted CompanyMetrics for company number: " + companyNumber);
            return true;
        } else {
            LOG.info("No CompanyMetrics found for company number: " + companyNumber);
            return false;
        }
    }

    private CompanyMetrics initializeMetrics(CompanySpec spec) {
        var metrics = new CompanyMetrics();
        metrics.setId(spec.getCompanyNumber());
        metrics.setEtag(randomService.getEtag());

        if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            metrics.setActivePscStatementsCount(1);
        } else if (spec.getActiveStatements() == null) {
            metrics.setActivePscStatementsCount(spec.getNumberOfPscs()
                    == null ? 0 : spec.getNumberOfPscs());
        } else {
            metrics.setActivePscStatementsCount(spec.getActiveStatements());
        }

        metrics.setWithdrawnPscStatementsCount(
                spec.getWithdrawnStatements() == null ? 0 : spec.getWithdrawnStatements()
        );

        LOG.debug("Initialized CompanyMetrics with ID: "
                + spec.getCompanyNumber() + " and ETag: " + metrics.getEtag());
        return metrics;
    }

    public void setActivePscCount(CompanyMetrics metrics, CompanySpec spec) {
        if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            metrics.setActivePscCount(1);
            LOG.debug("Company has super secure PSCs. Set active PSC count to 1.");
        } else if (spec.getNumberOfPscs() != null) {
            int activeCount = spec.getNumberOfPscs();
            if (Boolean.FALSE.equals(spec.getPscActive())) {
                activeCount = Math.max(0, spec.getNumberOfPscs() - 1);
            }
            metrics.setActivePscCount(activeCount);
            LOG.debug("Set active PSC count to " + activeCount);
        } else {
            metrics.setActivePscCount(0);
            LOG.debug("No PSC count provided. Set active PSC count to 0.");
        }
    }

    private void setActiveDirectorsCount(CompanyMetrics metrics, CompanySpec spec) {
        if (Boolean.TRUE.equals(spec.getNoDefaultOfficer())) {
            metrics.setActiveDirectorsCount(0);
            LOG.debug("No default officer specified. Set active directors count to 0.");
            return;
        }
        var numberOfAppointments = spec.getNumberOfAppointments();
        if (spec.getOfficerRoles() != null && spec.getOfficerRoles().stream()
                .anyMatch(role -> "director".equalsIgnoreCase(role.toString()))) {
            metrics.setActiveDirectorsCount(numberOfAppointments);
            LOG.debug("Set active directors count to " + numberOfAppointments);
        } else {
            metrics.setActiveDirectorsCount(1);
            LOG.debug("No specific officer roles provided. Set active directors count to 1.");
        }
    }

    public void setPscCount(CompanyMetrics metrics, CompanySpec spec) {
        if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            metrics.setPscCount(1);
            LOG.debug("Company has super secure PSCs. Set PSC count to 1.");
        } else if (spec.getNumberOfPscs() != null) {
            metrics.setPscCount(spec.getNumberOfPscs());
            LOG.debug("Set PSC count to " + spec.getNumberOfPscs());
        } else {
            metrics.setPscCount(0);
            LOG.debug("No PSC count provided. Set PSC count to 0.");
        }
    }

    public void setCeasedPscCount(CompanyMetrics metrics, CompanySpec spec) {
        var ceasedCount = 0;
        if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            LOG.debug("Company has super secure PSCs. Ceased PSC count remains 0.");
        } else if (Boolean.FALSE.equals(spec.getPscActive())) {
            ceasedCount = 1;
            LOG.debug("PSC active is false in spec. Set ceased PSC count to 1.");
        }

        metrics.setCeasedPscCount(ceasedCount);
    }

    private Map<String, RegisterItem> createRegisters(List<RegistersSpec> registers) {
        LOG.info("Creating registers for the provided list of " + registers.size() + " registers.");
        Map<String, RegisterItem> registerMap = registers.stream().collect(Collectors.toMap(
                RegistersSpec::getRegisterType,
                reg -> {
                    LOG.debug("Processing register type: " + reg.getRegisterType());
                    var item = new RegisterItem();

                    item.setRegisterMovedTo(reg.getRegisterMovedTo());
                    item.setMovedOn(LocalDate.now());
                    LOG.debug("Created RegisterItem for type: "
                            + reg.getRegisterType() + " with movedTo: " + reg.getRegisterMovedTo());

                    return item;
                }
        ));
        LOG.info("Successfully created " + registerMap.size() + " registers.");
        return registerMap;
    }
}