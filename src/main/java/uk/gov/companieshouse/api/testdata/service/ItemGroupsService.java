package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface ItemGroupsService {
    /**
     * Deletes Item Groups from the DB
     *
     * @param orderNumber the items groups needs to be deleted
     * @return the null on success and error for failure with the HTTP status
     * @throws NoDataFoundException if the item groups for the order number not found
     * @throws DataException if the item groups not deleted successfully
     */
    boolean deleteItemGroups(String orderNumber);
}