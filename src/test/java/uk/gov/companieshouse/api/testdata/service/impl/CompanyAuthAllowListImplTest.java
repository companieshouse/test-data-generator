package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthAllowList;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthAllowListRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyAuthAllowListImplTest {
    @Mock
    private RandomService randomService;

    @Mock
    private CompanyAuthAllowListRepository repository;

    @InjectMocks
    private CompanyAuthAllowListImpl companyAuthAllowListImpl;

    protected static String randomId = "random-id";

    @Test
    void createCompanyAuthAllowList() throws DataException {
        CompanyAuthAllowListSpec spec = new CompanyAuthAllowListSpec();
        spec.setEmailAddress("test@example.com");

        when(randomService.getString(24)).thenReturn(randomId);

        CompanyAuthAllowListData result = companyAuthAllowListImpl.create(spec);

        assertNotNull(result);
        assertEquals(randomId, result.getId());

        ArgumentCaptor<CompanyAuthAllowList> captor
                = ArgumentCaptor.forClass(CompanyAuthAllowList.class);
        verify(repository).save(captor.capture());

        CompanyAuthAllowList savedEntity = captor.getValue();
        assertEquals(randomId, savedEntity.getId());
        assertEquals("test@example.com", savedEntity.getEmailAddress());
    }

    @Test
    void deleteCompanyAuthAllowList() {
        CompanyAuthAllowList companyAuthAllowList = new CompanyAuthAllowList();
        companyAuthAllowList.setId(randomId);

        when(repository.findById(randomId)).thenReturn(Optional.of(companyAuthAllowList));

        boolean result = companyAuthAllowListImpl.delete(randomId);

        assertTrue(result);
        verify(repository).delete(companyAuthAllowList);
        verify(repository, times(1)).findById(randomId);
    }

    @Test
    void deleteCompanyAuthAllowListNonExistent() {
        when(repository.findById(randomId)).thenReturn(Optional.empty());

        boolean result = companyAuthAllowListImpl.delete(randomId);

        assertFalse(result);
        verify(repository, never()).delete(any());
    }

    @Test
    void getAuthId() {
        CompanyAuthAllowList companyAuthAllowList = new CompanyAuthAllowList();
        companyAuthAllowList.setId(randomId);
        companyAuthAllowList.setEmailAddress("test@example.com");

        when(repository.findByEmailAddress("test@example.com"))
                .thenReturn(Optional.of(companyAuthAllowList));
        String authId = companyAuthAllowListImpl.getAuthId("test@example.com");
        assertEquals(randomId, authId);
    }

    @Test
    void getAuthIdNonExistent() {
        when(repository.findByEmailAddress("test@example.com")).thenReturn(Optional.empty());

        String authId = companyAuthAllowListImpl.getAuthId("test@example.com");

        assertNull(authId);
    }

    @Test
    void getAuthIdNullEmail() {
        String authId = companyAuthAllowListImpl.getAuthId(null);
        assertNull(authId);
    }
}
