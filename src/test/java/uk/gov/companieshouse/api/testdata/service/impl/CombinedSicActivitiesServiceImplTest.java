package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CombinedSicActivities;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedSicActivitiesData;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedSicActivitiesSpec;
import uk.gov.companieshouse.api.testdata.repository.CombinedSicActivitiesRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;


class CombinedSicActivitiesServiceImplTest {

  @Mock
  private CombinedSicActivitiesRepository repository;

  @Mock
  private RandomService randomService;

  @InjectMocks
  private CombinedSicActivitiesServiceImpl service;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createSicCodesWithKeyword() throws DataException {
    CombinedSicActivitiesSpec spec = new CombinedSicActivitiesSpec();
    spec.setActivityDescription("Test Activity");
    spec.setSicDescription("Test SIC");
    spec.setIsChActivity(true);
    spec.setActivityDescriptionSearchField("test activity");

    when(randomService.getNumber(5)).thenReturn(12345L);
    CombinedSicActivitiesData result = service.create(spec);

    assertNotNull(result);
    assertEquals("12345", result.getSicCode());
    assertEquals("Test SIC", result.getSicDescription());
    assertNotNull(result.getId());

    verify(repository, times(1)).save(any(CombinedSicActivities.class));
  }

  @Test
  void createSicCodesWithoutMandatoryThrowsException() {
    CombinedSicActivitiesSpec spec = new CombinedSicActivitiesSpec();
    spec.setActivityDescription("Failing Activity");
    spec.setSicDescription("Failing SIC");

    when(randomService.getNumber(5)).thenReturn(99999L);
    doThrow(new RuntimeException("DB error")).when(repository).save(any());

    assertThrows(DataException.class, () -> service.create(spec));
  }

  @Test
  void deleteSicCodeSuccessfully() {
    String id = "507f1f77bcf86cd799439011";
    CombinedSicActivities entity = new CombinedSicActivities();
    entity.setId(new ObjectId(id));

    when(repository.findById(id)).thenReturn(Optional.of(entity));

    boolean result = service.delete(id);

    assertTrue(result);
    verify(repository, times(1)).delete(entity);
  }

  @Test
  void deleteSicCodeNotFound() {
    String id = "nonexistent";
    when(repository.findById(id)).thenReturn(Optional.empty());

    boolean result = service.delete(id);

    assertFalse(result);
    verify(repository, never()).delete(any());
  }
}
