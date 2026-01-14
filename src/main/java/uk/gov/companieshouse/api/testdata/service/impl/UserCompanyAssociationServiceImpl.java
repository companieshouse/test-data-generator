package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.ResponseBodyPost;
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

    private static final Logger LOG = LoggerFactory.getLogger(String.valueOf(UserCompanyAssociationServiceImpl.class));

    @Autowired
    private final ApiClientService apiClientService;

    public UserCompanyAssociationServiceImpl(ApiClientService apiClientService) {
        this.apiClientService = apiClientService;
    }

    @Override
    public UserCompanyAssociationData create(UserCompanyAssociationSpec spec) throws DataException {
        LOG.info("Creating association via SDK for company: {} and user: {}" + spec.getCompanyNumber() +spec.getUserId());
        try {
            var sdkResponse = apiClientService.getInternalApiClientForPrivateAccountApiUrl()
                    .privateAccountsAssociationResourceHandler()
                    .addAssociation("/associations", spec.getCompanyNumber(), spec.getUserId())
                    .execute()
                    .getData();

            String associationId = sdkResponse.getAssociationLink();
            LOG.info("Successfully created association. ID: " + associationId + " for company: " + spec.getCompanyNumber());

            UserCompanyAssociationData response = new UserCompanyAssociationData();
            response.setId(associationId);
            response.setCompanyNumber(spec.getCompanyNumber());
            response.setUserId(spec.getUserId());
            response.setAssociationLink("/associations/" + associationId);

            return response;

        } catch (ApiErrorResponseException | URIValidationException e) {
            LOG.error("Error creating association for company :" + spec.getCompanyNumber() + e.getMessage(), e);
            throw new DataException("Error creating association: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(String id) {
        LOG.info("Deleting association with ID: {}");
        try {
            // Update association status to REMOVED in SDK
            apiClientService.getInternalApiClientForPrivateAccountApiUrl()
                    .privateAccountsAssociationResourceHandler()
                    .updateAssociationStatusForId(
                            "/associations/" + id,
                            uk.gov.companieshouse.api.accounts.associations.model.RequestBodyPut.StatusEnum.REMOVED)
                    .execute();

            LOG.info("Successfully updated association status to REMOVED for ID: {}");
            return true;

        } catch (Exception e) {
            LOG.error("Error deleting association with ID: {}");
            return false;
        }
    }

    /**
     * Search for association by company number and user
     */
    public Association searchAssociation(String companyNumber, String userId, String userEmail)
            throws DataException {
        LOG.info("Searching for association for company: {} and user/email: {}/{}"
        );

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

        } catch (ApiErrorResponseException | URIValidationException e) {
            String errorMessage = String.format(
                    "Error searching for association for company %s", companyNumber);
            LOG.error(errorMessage, e);
            throw new DataException(errorMessage, e);
        }
    }

    // ========== PRIVATE HELPER METHODS ==========

    /**
     * Create simple response with just the association ID
     */
    private UserCompanyAssociationData createSimpleResponse(String associationId, UserCompanyAssociationSpec spec) {
        UserCompanyAssociationData response = new UserCompanyAssociationData();
        response.setId(associationId); // SDK-generated association ID
        response.setCompanyNumber(spec.getCompanyNumber());
        response.setUserId(spec.getUserId());
        response.setUserEmail(spec.getUserEmail());
        // Set the association link as expected
        response.setAssociationLink("/associations/" + associationId);
        return response;
    }

    private String getUserIdForSdk(UserCompanyAssociationSpec spec) {
        if (spec.getUserId() != null && !spec.getUserId().trim().isEmpty()) {
            return spec.getUserId();
        } else if (spec.getUserEmail() != null && !spec.getUserEmail().trim().isEmpty()) {
            return spec.getUserEmail();
        } else {
            throw new IllegalArgumentException("Either userId or userEmail must be provided");
        }
    }
}