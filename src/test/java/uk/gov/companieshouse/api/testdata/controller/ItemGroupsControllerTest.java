package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;

@ExtendWith(MockitoExtension.class)
class ItemGroupsControllerTest {

    @Mock
    private ItemGroupsService itemGroupsService;

    @InjectMocks
    private ItemGroupsController itemGroupsController;

    @Test
    void deleteItemGroups() {
        final String orderNumber = "ORD-1776329853";
        when(this.itemGroupsService.deleteItemGroups(orderNumber)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
                = this.itemGroupsController.deleteItemGroups(orderNumber);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(itemGroupsService).deleteItemGroups(orderNumber);
    }


    @Test
    void deleteItemGroupsNoData() {
        final String orderNumber = "ORD-1776329853";
        when(this.itemGroupsService.deleteItemGroups(orderNumber)).thenReturn(false);
        Map<String, Object> expectedBody = new HashMap<>();
        expectedBody.put("orderNumber", orderNumber);
        expectedBody.put("message", "Item Groups Not Found");
        expectedBody.put("status", HttpStatus.NOT_FOUND);
        ResponseEntity<Map<String, Object>> response
                = this.itemGroupsController.deleteItemGroups(orderNumber);
        assertEquals(expectedBody, response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(itemGroupsService).deleteItemGroups(orderNumber);
    }

    @Test
    void deleteItemGroupsException() {
        final String orderNumber = "ORD-1776329853";
        RuntimeException exception = new RuntimeException("Error deleting item groups");

        when(itemGroupsService.deleteItemGroups(orderNumber)).thenThrow(exception);

        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                itemGroupsController.deleteItemGroups(orderNumber));
        assertEquals(exception, thrown);
        verify(itemGroupsService).deleteItemGroups(orderNumber);
    }
}
