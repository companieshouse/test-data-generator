package uk.gov.companieshouse.api.testdata.service;

public interface RandomService {

    /**
     * Generate a random number with {@code digits} amounts of digits
     * 
     * @param digits The number of digits of the returned random number
     * @return A random long
     */
    Long getNumber(int digits);

    /**
     * Generate a random string with of size {@code length}
     * 
     * @param length The size of the returned random string
     * @return A random String
     */
    String getString(int length);

    /**
     * Generate a base64-encoded string formed of a random number of
     * {@code idLength} digits and a salt of size {@code saltLength}
     * 
     * @param idLength   The number of digits of the random number
     * @param saltLength The length of the salt String
     * @return A base64-encoded string
     */
    String getEncodedIdWithSalt(int idLength, int saltLength);
    
    /**
     * Generate a random etag
     * @return A random etag
     */
    String getEtag();
}
