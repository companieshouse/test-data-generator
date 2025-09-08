package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CombinedSicActivities;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedSicActivitiesData;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedSicActivitiesSpec;
import uk.gov.companieshouse.api.testdata.repository.CombinedSicActivitiesRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class CombinedSicActivitiesServiceImpl implements DataService<CombinedSicActivitiesData, CombinedSicActivitiesSpec> {

  @Autowired
  private CombinedSicActivitiesRepository repository;

  @Autowired
  public RandomService randomService;

  @Override
  public CombinedSicActivitiesData create(CombinedSicActivitiesSpec spec) throws DataException {
    var sicActivity = new CombinedSicActivities();
    var sicCode = randomService.getNumber(5);

    sicActivity.setId(new ObjectId());
    sicActivity.setSicCode(String.valueOf(sicCode));
    sicActivity.setActivityDescription(spec.getActivityDescription());
    sicActivity.setSicDescription(spec.getSicDescription());
    sicActivity.setIsChActivity(spec.getIsChActivity());
    sicActivity.setActivityDescriptionSearchField(spec.getActivityDescriptionSearchField());

    try {
      repository.save(sicActivity);
      return mapToSicActivitiesData(sicActivity);
    } catch (Exception ex) {
      throw new DataException("Failed to create SIC Code Keyword", ex);
    }
  }

  @Override
  public boolean delete(String id) {
    Optional<CombinedSicActivities> sicActivity = repository.findById(id);
    if (sicActivity.isPresent()) {
      repository.delete(sicActivity.get());
      return true;
    }
    return false;
  }

  private CombinedSicActivitiesData mapToSicActivitiesData(CombinedSicActivities sicActivity) {
    return new CombinedSicActivitiesData(
        sicActivity.getId().toHexString(),
        sicActivity.getSicCode(),
        sicActivity.getSicDescription()
    );
  }
}
