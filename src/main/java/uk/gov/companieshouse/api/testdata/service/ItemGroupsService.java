package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface ItemGroupsService {
    /**
     * Deletes Item Groups from the DB
     *
     * @param orderNumber the item groups that needs to be deleted
     * @return null on success and error with the HTTP status for failure
     * @throws NoDataFoundException if the item groups for the order number not found
     * @throws DataException if the item groups not deleted successfully
     */
    boolean deleteItemGroups(String orderNumber);
}