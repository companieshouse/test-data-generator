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
import uk.gov.companieshouse.api.testdata.model.companyauthcode.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyAuthCodeServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyAuthCodeServiceImplTest {

    @Mock
    private CompanyAuthCodeRepository companyAuthCodeRepository;
    @Mock
    RandomService randomService;

    @InjectMocks
    private CompanyAuthCodeServiceImpl companyAuthCodeServiceImpl;

    @Test
    void createNoException() throws DataException {
        when(this.randomService.getRandomInteger(6)).thenReturn("123456");
        CompanyAuthCode createdAuthCode = this.companyAuthCodeServiceImpl.create("12345678");

        assertNotNull(createdAuthCode);
        assertEquals(6, createdAuthCode.getAuthCode().length());
    }

    @Test
    void createDuplicateKeyException() {
        when(this.randomService.getRandomInteger(6)).thenReturn("123456");
        when(companyAuthCodeRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.companyAuthCodeServiceImpl.create("12345678");
        });
    }

    @Test
    void createMongoExceptionException() {
        when(this.randomService.getRandomInteger(6)).thenReturn("123456");
        when(companyAuthCodeRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.companyAuthCodeServiceImpl.create("12345678");
        });
    }

    @Test
    void deleteNoDataException() {
        when(companyAuthCodeRepository.findById("12345678")).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> {
            this.companyAuthCodeServiceImpl.delete("12345678");
        });
    }

    @Test
    void deleteMongoException() {
        when(companyAuthCodeRepository.findById("12345678"))
                .thenReturn(Optional.of(new CompanyAuthCode()));
        doThrow(MongoException.class).when(companyAuthCodeRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.companyAuthCodeServiceImpl.delete("12345678");
        });
    }

}