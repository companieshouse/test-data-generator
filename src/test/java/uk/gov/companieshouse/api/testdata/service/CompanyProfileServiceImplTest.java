package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyProfileServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyProfileServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    @InjectMocks
    private CompanyProfileServiceImpl companyProfileService;

    @Test
    void createNoException() throws DataException {
        CompanyProfile createdCompany = this.companyProfileService.create(COMPANY_NUMBER);

        assertEquals("Active", createdCompany.getCompanyStatus());
        assertEquals("england-wales", createdCompany.getJurisdiction());
        assertEquals("ltd", createdCompany.getType());
    }

    @Test
    void createDuplicateKeyException() {
        when(companyProfileRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () ->
            this.companyProfileService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void createMongoExceptionException() {
        when(companyProfileRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
            this.companyProfileService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteNoCompany() {
        when(companyProfileRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(null);
        assertThrows(NoDataFoundException.class, () ->
            this.companyProfileService.delete(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteMongoException() {
        when(companyProfileRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(new CompanyProfile());
        doThrow(MongoException.class).when(companyProfileRepository).delete(any());
        assertThrows(DataException.class, () ->
            this.companyProfileService.delete(COMPANY_NUMBER)
        );
    }

}