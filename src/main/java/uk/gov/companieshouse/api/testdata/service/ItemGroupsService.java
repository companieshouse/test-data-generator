package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;

public interface ItemGroupsService {
    boolean deleteItemGroups(String orderNumber);
}