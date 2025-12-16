package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthAllowList;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthAllowListRepository;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CompanyAuthAllowListImpl implements CompanyAuthAllowListService {

    private final CompanyAuthAllowListRepository repository;

    private final RandomService randomService;

    @Autowired
    public CompanyAuthAllowListImpl(CompanyAuthAllowListRepository repository, RandomService randomService) {
        super();
        this.repository = repository;
        this.randomService = randomService;
    }

    @Override
    public CompanyAuthAllowListData create(CompanyAuthAllowListSpec spec) throws DataException {
        var randomId = randomService.getString(24).toLowerCase();
        var companyAuthAllowList = new CompanyAuthAllowList();
        companyAuthAllowList.setId(randomId);
        companyAuthAllowList.setEmailAddress(spec.getEmailAddress());
        repository.save(companyAuthAllowList);
        return new CompanyAuthAllowListData(companyAuthAllowList.getId());
    }

    @Override
    public boolean delete(String id) {
        var companyAuthAllowList = repository.findById(id);
        companyAuthAllowList.ifPresent(repository::delete);
        return companyAuthAllowList.isPresent();
    }

    @Override
    public String getAuthId(String emailAddress) {
        if (emailAddress != null) {
            Optional<CompanyAuthAllowList> companyAuthAllowList
                    = repository.findByEmailAddress(emailAddress);
            return companyAuthAllowList.map(CompanyAuthAllowList::getId).orElse(null);
        } else {
            return null;
        }
    }
}
