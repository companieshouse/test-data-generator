package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanySearchServiceImpl implements CompanySearchService {

    private static final String COMPANY_SEARCH_URI = "/company-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s";
    private static final String ERROR_MSG_ADD_COMPANY =
            "Error occurred while adding company into ElasticSearch index: ";
    private static final String ERROR_MSG_DELETE_COMPANY =
            "Error occurred while deleting company from ElasticSearch index: ";
    private static final String OVERSEA_COMPANY_TYPE = "oversea-company";

    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private final CompanyProfileService companyProfileService;

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanySearchServiceImpl.class));

    public CompanySearchServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier,
                                    CompanyProfileService companyProfileService) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.companyProfileService = companyProfileService;
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data)
            throws DataException, ApiErrorResponseException, URIValidationException {
        String companyNumber = data.getCompanyNumber();
        String formattedCompanySearchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        String formattedCompanyProfileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);

        try {
            var companyProfileData = internalApiClientSupplier.get()
                    .privateCompanyResourceHandler()
                    .getCompanyFullProfile(formattedCompanyProfileUri)
                    .execute()
                    .getData();

            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .upsertCompanyProfile(formattedCompanySearchUri, companyProfileData)
                    .execute();
            LOG.info(
                    "Company profile upsert is successful with company number: "
                            + data.getCompanyNumber());
        } catch (ApiErrorResponseException ex) {
            LOG.error("API error occurred while upserting company profile for company number: "
                    + data.getCompanyNumber() + ". Error: " + ex.getMessage(), ex);
            throw new DataException(ERROR_MSG_ADD_COMPANY + ex.getMessage(), ex);
        } catch (URIValidationException ex) {
            LOG.error(
                    "URI validation error occurred while upserting profile for company number: "
                    + data.getCompanyNumber() + ". Error: " + ex.getMessage(), ex);
            throw new DataException(ERROR_MSG_ADD_COMPANY + ex.getMessage(), ex);
            LOG.info("Company profile upsert successful for company number: {}" + companyNumber);

            handleUkEstablishmentsForOverseaCompany(companyNumber);

        } catch (ApiErrorResponseException | URIValidationException ex) {
            throw new DataException("Failed to upsert company profile: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) throws DataException {
        try {
            handleUkEstablishmentsDeletion(companyNumber);

            String formattedUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .deleteCompanyProfile(formattedUri)
                    .execute();
            LOG.info("Company profile deleted successfully for company number: {}" + companyNumber);

        } catch (ApiErrorResponseException | URIValidationException ex) {
            throw new DataException("Failed to delete company profile: " + ex.getMessage(), ex);
            throw new DataException(ERROR_MSG_DELETE_COMPANY + ex.getMessage());
        }
    }

    private void handleUkEstablishmentsForOverseaCompany(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        Optional<CompanyProfile> companyProfile =
                companyProfileService.getCompanyProfile(companyNumber);
        if (companyProfile.isPresent()
                && OVERSEA_COMPANY_TYPE.equals(companyProfile.get().getType())) {
            List<String> ukEstablishments =
                    companyProfileService.findUkEstablishmentsByParent(companyNumber);
            for (String establishmentNumber : ukEstablishments) {
                addSingleCompanyToIndex(establishmentNumber);
            }
        }
    }

    private void handleUkEstablishmentsDeletion(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        Optional<CompanyProfile> companyProfile =
                companyProfileService.getCompanyProfile(companyNumber);
        if (companyProfile.isPresent()
                && OVERSEA_COMPANY_TYPE.equals(companyProfile.get().getType())) {
            List<String> ukEstablishments =
                    companyProfileService.findUkEstablishmentsByParent(companyNumber);
            for (String establishmentNumber : ukEstablishments) {
                deleteSingleCompanyFromIndex(establishmentNumber);
            }
        }
    }

    private void addSingleCompanyToIndex(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String searchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        String profileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);

        var profileData = internalApiClientSupplier.get()
                .privateCompanyResourceHandler()
                .getCompanyFullProfile(profileUri)
                .execute()
                .getData();

        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .companySearch()
                .upsertCompanyProfile(searchUri, profileData)
                .execute();
        LOG.info("Company profile upsert successful for company number: {}" + companyNumber);
    }

    private void deleteSingleCompanyFromIndex(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String searchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .companySearch()
                .deleteCompanyProfile(searchUri)
                .execute();
        LOG.info("Company profile deleted successfully for company number: {}" + companyNumber);
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}