package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Appeals;
import uk.gov.companieshouse.api.testdata.repository.AppealsRepository;
import uk.gov.companieshouse.api.testdata.service.AppealsService;

@Service
public class AppealsServiceImpl implements AppealsService {

    @Autowired
    private AppealsRepository repository;

    @Override
    public boolean delete(String companyNumber, String penaltyReference) {
        Optional<Appeals> appeal = repository.deleteByCompanyNumberAndPenaltyReference(
                companyNumber, penaltyReference);
        return appeal.isPresent();
    }
}