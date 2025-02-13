package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Appeals;
import uk.gov.companieshouse.api.testdata.repository.AppealsRepository;

@ExtendWith(MockitoExtension.class)
class AppealsServiceImplTest {

    @Mock
    private AppealsRepository repository;

    @InjectMocks
    private AppealsServiceImpl service;

    @Test
    void testDeleteAppealSuccess() {
        String companyNumber = "12345678";
        String penaltyReference = "PR123";

        Appeals appeal = new Appeals();
        when(repository.deleteByCompanyNumberAndPenaltyReference(companyNumber, penaltyReference))
                .thenReturn(Optional.of(appeal));

        boolean result = service.delete(companyNumber, penaltyReference);

        assertTrue(result);
        verify(repository, times(1))
                .deleteByCompanyNumberAndPenaltyReference(companyNumber, penaltyReference);
    }

    @Test
    void testDeleteAppealNotFound() {
        String companyNumber = "12345678";
        String penaltyReference = "PR123";

        when(repository.deleteByCompanyNumberAndPenaltyReference(companyNumber, penaltyReference))
                .thenReturn(Optional.empty());

        boolean result = service.delete(companyNumber, penaltyReference);
        assertFalse(result);
        verify(repository, times(1))
                .deleteByCompanyNumberAndPenaltyReference(companyNumber, penaltyReference);
    }
}
