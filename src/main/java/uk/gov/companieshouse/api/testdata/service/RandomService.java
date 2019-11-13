package uk.gov.companieshouse.api.testdata.service;

public interface RandomService {
    Long getRandomNumber(int digits);

    String getRandomString(int digits);

    String getEncodedIdWithSalt(int idLength, int saltLength);
    
    /**
     * Generate a random etag
     * @return A random etag
     */
    String getEtag();
}
