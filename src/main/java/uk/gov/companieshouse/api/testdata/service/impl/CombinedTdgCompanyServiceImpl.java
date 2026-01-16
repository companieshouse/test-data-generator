package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedCompanySpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.CombinedTdgCompanyService;

@Service
public class CombinedTdgCompanyServiceImpl implements CombinedTdgCompanyService {

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private CompanyAuthCodeRepository authCodeRepository;

    @Autowired
    FilingHistoryRepository filingHistoryRepository;

    @Override
    public void createCombinedCompany(CombinedCompanySpec companySpec) {

        var companyProfile = companySpec.getCompanyProfile();
        companyProfileRepository.save(companyProfile);

        var companyAuthCode = companySpec.getCompanyAuthCode();
        if (companyAuthCode != null) {
            authCodeRepository.save(companyAuthCode);
        }

    }
}
