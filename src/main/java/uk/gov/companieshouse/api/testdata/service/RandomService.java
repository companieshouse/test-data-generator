package uk.gov.companieshouse.api.testdata.service;

import java.time.LocalDate;
import java.util.OptionalLong;
import org.bson.types.ObjectId;

public interface RandomService {

    /**
     * Generate a random number with {@code digits} amounts of digits
     * 
     * @param digits The number of digits of the returned random number
     * @return A random long
     */
    Long getNumber(int digits);

    /**
     * Generate a random number between a set range
     *
     * @param startInclusive The starting range including the given value.
     * @param endExclusive   The closing range excluding the given value.
     * @return A random long
     */
    OptionalLong getNumberInRange(int startInclusive, int endExclusive);

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

    /**
     * Appends a random salt of {@code saltLength} to {@code baseString}
     * and base-64 encodes the result
     * @param baseString The String to be salted and encoded
     * @param saltLength The length of the salt String
     * @return A base-64-encoded string
     */
    String addSaltAndEncode(String baseString, int saltLength);

    /**
     * Generate a random date based on the accounts due status
     * @param accountsDueStatus The status of the accounts due
     * @return A date generated based on the accounts due status
     */
    LocalDate generateAccountsDueDateByStatus(String accountsDueStatus);

    /**
     * Generate a random ObjectId.
     * @return A random ObjectId.
     */
    ObjectId generateId();
}
