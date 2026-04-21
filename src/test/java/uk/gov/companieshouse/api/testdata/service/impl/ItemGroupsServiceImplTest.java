package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.ItemGroups;
import uk.gov.companieshouse.api.testdata.repository.ItemGroupsRepository;

@ExtendWith(MockitoExtension.class)
class ItemGroupsServiceImplTest {

    @Mock
    private ItemGroupsRepository repository;

    @InjectMocks
    private ItemGroupsServiceImpl service;

    private static final String ORDER_NUMBER = "ORD-123456-789012";

    @Test
    void deleteItemGroupsFound() {
        ItemGroups itemGroups = new ItemGroups();
        when(repository.findByDataOrderNumber(ORDER_NUMBER)).thenReturn(Optional.of(itemGroups));

        boolean result = service.deleteItemGroups(ORDER_NUMBER);

        assertTrue(result);
        verify(repository).deleteByDataOrderNumber(ORDER_NUMBER);
    }

    @Test
    void deleteItemGroupsNotFound() {
        when(repository.findByDataOrderNumber(ORDER_NUMBER)).thenReturn(Optional.empty());

        boolean result = service.deleteItemGroups(ORDER_NUMBER);

        assertFalse(result);
        verify(repository, never()).deleteByDataOrderNumber(anyString());
    }

    @Test
    void deleteItemGroupsThrowsException() {
        when(repository.findByDataOrderNumber(ORDER_NUMBER))
                .thenThrow(new RuntimeException("Database connection failure"));

        try {
            service.deleteItemGroups(ORDER_NUMBER);
        } catch (Exception e) {
            assertTrue(e instanceof RuntimeException);
        }

        verify(repository, never()).deleteByDataOrderNumber(anyString());
    }
}