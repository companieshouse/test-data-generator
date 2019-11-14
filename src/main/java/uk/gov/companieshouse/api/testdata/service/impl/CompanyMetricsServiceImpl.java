package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
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
    public CompanyMetrics create(String companyNumber) throws DataException {
        CompanyMetrics metrics = new CompanyMetrics();
        metrics.setId(companyNumber);
        metrics.setEtag(randomService.getEtag());
        metrics.setActivePscStatementsCount(1);
        metrics.setActiveDirectorsCount(1);

        try {
            return repository.save(metrics);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }
    }

    @Override
    public void delete(String companyNumber) throws NoDataFoundException, DataException {
        // TODO Auto-generated method stub
    }

}
