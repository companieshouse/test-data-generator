package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.service.ApiClientService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service("userCompanyAssociationServiceImpl")
public class UserCompanyAssociationServiceImpl implements
        DataService<UserCompanyAssociationData, UserCompanyAssociationSpec> {

    private static final Logger LOG
            = LoggerFactory.getLogger(String.valueOf(UserCompanyAssociationServiceImpl.class));

    @Autowired
    private final ApiClientService apiClientService;

    public UserCompanyAssociationServiceImpl(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    @Override
    public UserCompanyAssociationData create(UserCompanyAssociationSpec spec) throws DataException {
        LOG.info("Creating association via SDK for company: "
                + spec.getCompanyNumber() + "and user: "  + spec.getUserId());
        try {
            var sdkResponse = apiClientService.getInternalApiClientForPrivateAccountApiUrl()
                    .privateAccountsAssociationResourceHandler()
                    .addAssociation("/associations", spec.getCompanyNumber(), spec.getUserId())
                    .execute()
                    .getData();

            var associationLink = sdkResponse.getAssociationLink();
            String associationId;
            if (associationLink != null && associationLink.contains("/")) {
                associationId = associationLink.substring(associationLink.lastIndexOf("/") + 1);
            } else {
                associationId = associationLink;
            }
            LOG.info("Successfully created association. ID: "
                    + associationId + " for company: " + spec.getCompanyNumber());

            // Only return id and associationLink
            var response = new UserCompanyAssociationData(associationId,
                    "/associations/" + associationId);
            return response;

        } catch (ApiErrorResponseException | URIValidationException error) {
            LOG.error("Error creating association for company :"
                    + spec.getCompanyNumber() + error.getMessage(), error);
            throw new DataException("Error creating association: " + error.getMessage(), error);
        }
    }

    @Override
    public boolean delete(String id) {
        LOG.info("Deleting association with ID: " + id);
        try {
            apiClientService.getInternalApiClientForPrivateAccountApiUrl()
                    .privateAccountsAssociationResourceHandler()
                    .updateAssociationStatusForId(
                            "/associations/" + id,
                            uk.gov.companieshouse.api.accounts.associations.model
                                    .RequestBodyPut.StatusEnum.REMOVED)
                    .execute();

            LOG.info("Successfully updated association status to REMOVED for ID: " + id);
            return true;

        } catch (Exception error) {
            LOG.error("Error deleting association with ID: " + error);
            return false;
        }
    }

    public Association searchAssociation(String companyNumber, String userId, String userEmail)
            throws DataException {
        LOG.info("Searching for association for company: "
                + companyNumber + "and user/email: " + userEmail);

        try {
            Association association = apiClientService.getInternalApiClientForPrivateAccountApiUrl()
                    .privateAccountsAssociationResourceHandler()
                    .searchForAssociation(
                            "/associations/companies/" + companyNumber + "/search",
                            userId,
                            userEmail,
                            "confirmed", "awaiting-approval", "migrated", "unauthorised")
                    .execute()
                    .getData();

            if (association != null) {
                LOG.info("Found association with ID: {} for company: {}"
                );
            } else {
                LOG.info("No association found for company: {}");
            }

            return association;

        } catch (ApiErrorResponseException | URIValidationException error) {
            String errorMessage = String.format(
                    "Error searching for association for company %s", companyNumber);
            LOG.error(errorMessage, error);
            throw new DataException(errorMessage, error);
        }
    }
}