package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;

public interface DataService<T, S> {
    /**
     * Creates a new entity based on the provided specification.
     *
     * @param spec the specification of the entity to create
     * @return the created entity
     * @throws DataException if an error occurs during creation
     */
    T create(S spec) throws DataException;

    /**
     * Deletes an entity by its ID.
     *
     * @param id the ID of the entity to delete
     * @return true if the entity was deleted, false otherwise
     */
    boolean delete(String id);
}
