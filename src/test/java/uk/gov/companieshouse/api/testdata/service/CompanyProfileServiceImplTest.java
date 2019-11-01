package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import uk.gov.companieshouse.api.testdata.repository.companyprofile.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyProfileServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class CompanyProfileServiceImplTest {

    @Mock
    private CompanyProfileRepository companyProfileRepository;

    private ICompanyProfileService ICompanyProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.ICompanyProfileService = new CompanyProfileServiceImpl(companyProfileRepository);
    }

    @Test
    void testCreateNoException() throws DataException {
        Company createdCompany = this.ICompanyProfileService.create();

        assertEquals("Active", createdCompany.getCompanyStatus());
        assertEquals("england-wales", createdCompany.getJurisdiction());
        assertEquals("ltd", createdCompany.getType());
    }

    @Test
    void testCreateDuplicateKeyException() {
        when(companyProfileRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.ICompanyProfileService.create();
        });
    }

    @Test
    void testCreateMongoExceptionException() {
        when(companyProfileRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.ICompanyProfileService.create();
        });
    }

    @Test
    void testDeleteMongoException() {
        when(companyProfileRepository.findByCompanyNumber("12345678"))
                .thenReturn(new Company());
        doThrow(MongoException.class).when(companyProfileRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.ICompanyProfileService.delete("12345678");
        });
    }

}