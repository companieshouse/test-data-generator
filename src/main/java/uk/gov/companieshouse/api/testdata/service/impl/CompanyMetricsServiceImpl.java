package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyMetricsServiceImpl implements DataService<CompanyMetrics,CompanySpec> {

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
        metrics.setActiveDirectorsCount(1);

        return repository.save(metrics);
    }

    @Override
    public boolean delete(String companyNumber) {
        Optional<CompanyMetrics> existingMetric = repository.findById(companyNumber);

        existingMetric.ifPresent(repository::delete);
        return existingMetric.isPresent();
    }

}
