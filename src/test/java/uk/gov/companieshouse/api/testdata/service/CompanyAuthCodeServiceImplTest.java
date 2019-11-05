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
import uk.gov.companieshouse.api.testdata.model.account.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.account.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyAuthCodeServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyAuthCodeServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_AUTH_CODE = "123456";

    @Mock
    private CompanyAuthCodeRepository companyAuthCodeRepository;
    @Mock
    RandomService randomService;

    @InjectMocks
    private CompanyAuthCodeServiceImpl companyAuthCodeServiceImpl;

    @Test
    void createNoException() throws DataException {
        when(this.randomService.getRandomInteger(6)).thenReturn(COMPANY_AUTH_CODE);
        CompanyAuthCode createdAuthCode = this.companyAuthCodeServiceImpl.create(COMPANY_NUMBER);

        assertNotNull(createdAuthCode);
        assertEquals(COMPANY_AUTH_CODE, createdAuthCode.getAuthCode());
        assertTrue(createdAuthCode.getIsActive());
        assertEquals(COMPANY_NUMBER, createdAuthCode.getId());
    }

    @Test
    void createDuplicateKeyException() {
        when(this.randomService.getRandomInteger(6)).thenReturn(COMPANY_AUTH_CODE);
        when(companyAuthCodeRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () ->
            this.companyAuthCodeServiceImpl.create(COMPANY_NUMBER)
        );
    }

    @Test
    void createMongoExceptionException() {
        when(this.randomService.getRandomInteger(6)).thenReturn(COMPANY_AUTH_CODE);
        when(companyAuthCodeRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
            this.companyAuthCodeServiceImpl.create(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteNoDataException() {
        when(companyAuthCodeRepository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () ->
            this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteMongoException() {
        when(companyAuthCodeRepository.findById(COMPANY_NUMBER))
                .thenReturn(Optional.of(new CompanyAuthCode()));
        doThrow(MongoException.class).when(companyAuthCodeRepository).delete(any());
        assertThrows(DataException.class, () ->
            this.companyAuthCodeServiceImpl.delete(COMPANY_NUMBER)
        );
    }

}