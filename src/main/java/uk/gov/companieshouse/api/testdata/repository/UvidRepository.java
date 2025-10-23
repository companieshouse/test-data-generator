package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;

import java.util.Optional;

public interface UvidRepository extends MongoRepository<Uvid, String> {

    /**
     * Finds a UVID document by its associated identity ID.
     * Spring Data automatically creates the MongoDB query: { "identity_id" : ?0 }
     *
     * @param identityId The identity ID to search for.
     * @return The UVID entity if found, or null otherwise.
     */
    Uvid findByIdentityId(String identityId);

    /**
     * Finds a UVID document by its UVID value.
     * Spring Data automatically creates the MongoDB query: { "uvid" : ?0 }
     *
     * @param uvid The UVID value to search for.
     * @return An Optional containing the UVID if found, or empty otherwise.
     */
    Optional<Uvid> findByUvid(String uvid);

    /**
     * Checks if a UVID exists for the given identity ID.
     * Spring Data automatically creates the MongoDB query: { "identity_id" : ?0 }
     *
     * @param identityId The identity ID to check for existence.
     * @return true if a UVID exists for the identity, false otherwise.
     */
    boolean existsByIdentityId(String identityId);

    /**
     * Checks if a UVID exists with the given UVID value.
     * Spring Data automatically creates the MongoDB query: { "uvid" : ?0 }
     *
     * @param uvid The UVID value to check for existence.
     * @return true if a UVID exists with the given value, false otherwise.
     */
    boolean existsByUvid(String uvid);

    /**
     * Deletes a UVID document by its associated identity ID.
     * Spring Data automatically creates the MongoDB query: { "identity_id" : ?0 }
     *
     * @param identityId The identity ID for which to delete the UVID.
     * @return The number of documents deleted.
     */
    long deleteByIdentityId(String identityId);

    /**
     * Custom query to find UVID by identity ID using explicit field mapping
     * This provides an alternative to the derived query method
     *
     * @param identityId The identity ID to search for.
     * @return An Optional containing the UVID if found, or empty otherwise.
     */
    @Query("{ 'identity_id': ?0 }")
    Optional<Uvid> findUvidByIdentityId(String identityId);
}