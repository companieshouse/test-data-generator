package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.RegisterItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyMetricsServiceImpl implements DataService<CompanyMetrics, CompanySpec> {

    @Autowired
    private CompanyMetricsRepository repository;
    @Autowired
    private RandomService randomService;

    @Override
    public CompanyMetrics create(CompanySpec spec) {
        CompanyMetrics metrics = new CompanyMetrics();
        metrics.setId(spec.getCompanyNumber());
        metrics.setEtag(randomService.getEtag());
        metrics.setActivePscStatementsCount(1);

        if (CompanyType.REGISTERED_OVERSEAS_ENTITY.equals(spec.getCompanyType())) {
            metrics.setActivePscCount(2);
        } else if (BooleanUtils.isTrue(spec.getHasSuperSecurePscs())) {
            metrics.setActivePscCount(1);
        } else {
            metrics.setActivePscCount(3);
        }

        metrics.setActiveDirectorsCount(1);
        if (spec.getRegisters() != null) {
            metrics.setRegisters(createRegisters(spec.getRegisters()));
        }
        return repository.save(metrics);
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<CompanyMetrics> existingMetric = repository.findById(companyNumber);

        existingMetric.ifPresent(repository::delete);
        return existingMetric.isPresent();
    }

    private Map<String, RegisterItem> createRegisters(List<RegistersSpec> registers) {
        return registers.stream().collect(Collectors.toMap(
                RegistersSpec::getRegisterType,
                reg -> {
                    var item = new RegisterItem();
                    item.setRegisterMovedTo(reg.getRegisterMovedTo());
                    item.setMovedOn(LocalDate.now());
                    return item;
                }
        ));
    }
}
