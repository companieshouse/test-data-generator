package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyMetricsServiceImpl implements DataService<CompanyMetrics> {

    private static final String METRIC_DATA_NOT_FOUND = "metric data not found";

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
    public void delete(String companyNumber) throws NoDataFoundException, DataException {
        CompanyMetrics existingMetric = repository.findById(companyNumber)
                .orElseThrow(() -> new NoDataFoundException(METRIC_DATA_NOT_FOUND));

        try {
            repository.delete(existingMetric);
        } catch (MongoException e) {
            throw new DataException("Failed to delete company metrics", e);
        }
    }

}
