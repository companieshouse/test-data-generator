package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.service.CombinedSicActivitiesService;

@ExtendWith(MockitoExtension.class)
class CombinedSicActivitiesControllerTest {

    private static final String SIC_ACTIVITY_ID = "6242bbbbafaaaa93274b2efd";

    @Mock
    private CombinedSicActivitiesService combinedSicActivitiesService;

    @InjectMocks
    private CombinedSicActivitiesController combinedSicActivitiesController;

    @Test
    void createCombinedSicActivities() throws Exception {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        CombinedSicActivitiesResponse data =
                new CombinedSicActivitiesResponse(
                        new ObjectId().toHexString(),
                        "21017",
                        "Abbau von Braunkohle");

        when(this.combinedSicActivitiesService.create(spec))
                .thenReturn(data);

        ResponseEntity<CombinedSicActivitiesResponse> response =
                this.combinedSicActivitiesController.createCombinedSicActivities(spec);

        assertEquals(data, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(combinedSicActivitiesService, times(1)).create(spec);
    }

    @Test
    void createCombinedSicActivitiesException() throws Exception {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        Throwable exception = new DataException("Error creating combined sic activities");

        when(this.combinedSicActivitiesService.create(spec))
                .thenThrow(exception);

        DataException thrown = assertThrows(DataException.class,
                () -> this.combinedSicActivitiesController.createCombinedSicActivities(spec));

        assertEquals(exception, thrown);
        verify(combinedSicActivitiesService, times(1)).create(spec);
    }

    @Test
    void deleteCombinedSicActivities() {
        when(this.combinedSicActivitiesService.delete(SIC_ACTIVITY_ID))
                .thenReturn(true);

        ResponseEntity<Map<String, Object>> response =
                this.combinedSicActivitiesController.deleteCombinedSicActivities(SIC_ACTIVITY_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesNotFound() {
        when(this.combinedSicActivitiesService.delete(SIC_ACTIVITY_ID))
                .thenReturn(false);

        ResponseEntity<Map<String, Object>> response =
                this.combinedSicActivitiesController.deleteCombinedSicActivities(SIC_ACTIVITY_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(SIC_ACTIVITY_ID,
                Objects.requireNonNull(response.getBody()).get("id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesException() {
        Throwable exception = new RuntimeException("Error deleting combined sic activities");

        when(this.combinedSicActivitiesService.delete(SIC_ACTIVITY_ID))
                .thenThrow(exception);

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> this.combinedSicActivitiesController
                        .deleteCombinedSicActivities(SIC_ACTIVITY_ID));

        assertEquals(exception, thrown);
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }
}
