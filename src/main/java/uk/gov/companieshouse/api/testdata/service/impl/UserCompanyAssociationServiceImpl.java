package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.service.ApiClientService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.model.entity.UserCompanyAssociation;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.Optional;

@Service("userCompanyAssociationServiceImpl")
public class UserCompanyAssociationServiceImpl implements
        DataService<UserCompanyAssociationData, UserCompanyAssociationSpec> {

    private static final Logger LOG
            = LoggerFactory.getLogger(String.valueOf(UserCompanyAssociationServiceImpl.class));

    private final UserCompanyAssociationRepository userCompanyAssociationRepository;
    private final ApiClientService apiClientService;

    public UserCompanyAssociationServiceImpl(ApiClientService apiClientService, UserCompanyAssociationRepository userCompanyAssociationRepository) {
        this.apiClientService = apiClientService;
        this.userCompanyAssociationRepository = userCompanyAssociationRepository;
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

            return new UserCompanyAssociationData(
                    associationId,
                    "/associations/" + associationId
            );


        } catch (ApiErrorResponseException | URIValidationException error) {
            LOG.error("Error creating association for company :"
                    + spec.getCompanyNumber() + error.getMessage(), error);
            throw new DataException("Error creating association: " + error.getMessage(), error);
        }
    }

    @Override
    public boolean delete(String id) {
        LOG.info("Deleting association with ID: " + id);
        Optional<UserCompanyAssociation> optional = userCompanyAssociationRepository.findById(id);
        if (optional.isPresent()) {
            UserCompanyAssociation association = optional.get();
            association.setStatus("REMOVED");
            userCompanyAssociationRepository.save(association);
            LOG.info("Successfully updated association status to REMOVED for ID: " + id);
            return true;
        } else {
            LOG.error("Association not found with ID: " + id);
            return false;
        }
    }

    public Association searchAssociation(String companyNumber, String userId, String userEmail)
            throws DataException {
        LOG.info("Searching for association for company: "
                + companyNumber + "and user/email: " + userEmail);

        try {
            var association = apiClientService.getInternalApiClientForPrivateAccountApiUrl()
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
            var errorMessage = String.format(
                    "Error searching for association for company %s", companyNumber);
            LOG.error(errorMessage, error);
            throw new DataException(errorMessage, error);
        }
    }
}