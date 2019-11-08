package uk.gov.companieshouse.api.testdata.service;

public interface RandomService {
    String getRandomInteger(int digits);
    String getEncodedIdWithSalt(int idLength, int saltLength);
}
