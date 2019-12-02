package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyMetricsServiceImpl implements DataService<CompanyMetrics> {

    @Autowired
    private CompanyMetricsRepository repository;
    @Autowired
    private RandomService randomService;
    
    @Override
    public CompanyMetrics create(CompanySpec spec) throws DataException {
        CompanyMetrics metrics = new CompanyMetrics();
        metrics.setId(spec.getCompanyNumber());
        metrics.setEtag(randomService.getEtag());
        metrics.setActivePscStatementsCount(1);
        metrics.setActiveDirectorsCount(1);

        try {
            return repository.save(metrics);
        } catch (MongoException e) {
            throw new DataException("Failed to save company metrics", e);
        }
    }

    @Override
    public boolean delete(String companyNumber) throws DataException {
        Optional<CompanyMetrics> existingMetric = repository.findById(companyNumber);

        try {
            existingMetric.ifPresent(repository::delete);
            return existingMetric.isPresent();
        } catch (MongoException e) {
            throw new DataException("Failed to delete company metrics", e);
        }
    }

}
