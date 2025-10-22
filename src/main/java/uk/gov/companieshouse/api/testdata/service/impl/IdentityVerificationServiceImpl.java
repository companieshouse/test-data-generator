package uk.gov.companieshouse.api.testdata.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.api.client.http.HttpResponseException;
import java.util.Date;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.identityverification.PrivateIdentityVerificationResourceHandler;
import uk.gov.companieshouse.api.identityverification.model.Identity;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.IdentityVerificationService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service("identityVerificationService")
public class IdentityVerificationServiceImpl implements IdentityVerificationService {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String IDENTITY_VERIFICATION_URI = "/verification/identities";
    private static final String GENERATE_UVID_URI = "/verification/generation/generate_uvid";
    private static final String CREATE_UVID_PARAM = "acsp";

    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private final ObjectMapper objectMapper;
    private final UvidRepository uvidRepository;

    public IdentityVerificationServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier,
                                           UvidRepository uvidRepository) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.uvidRepository = uvidRepository;

        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public String createIdentityAndGetUvid(Identity apiIdentity)
            throws ApiErrorResponseException, URIValidationException {

        LOG.info("Attempting to create identity verification and UViD for email: "
                + apiIdentity.getEmail());
        try {
            String identityId = postIdentityAndCreateUvid(apiIdentity);

            String generatedUvid = generateUvid();

            createUvidInMongo(identityId, generatedUvid);

            LOG.info("Successfully created identity ID: "
                    + identityId + " and UViD: " + generatedUvid);

            return identityId;

        } catch (Exception error) {
            LOG.error("Failed to create identity verification for email: "
                    + apiIdentity.getEmail() + ". Error: " + error.getMessage(), error);
            throw error;
        }
    }

    private String postIdentityAndCreateUvid(Identity apiIdentity)
            throws ApiErrorResponseException, URIValidationException {

        try {
            InternalApiClient internalApiClient = internalApiClientSupplier.get();
            PrivateIdentityVerificationResourceHandler handler = internalApiClient
                    .privateIdentityVerificationResourceHandler();

            var response = handler.postIdentityAndCreateUvid(
                    IDENTITY_VERIFICATION_URI, apiIdentity, CREATE_UVID_PARAM)
                    .execute();

            return response.getData().getId();
        } catch (ApiErrorResponseException error) {
            LOG.error("API Error creating identity: Status="
                    + error.getStatusCode()
                    + ", Message=" + error.getStatusMessage());
            throw error;
        } catch (URIValidationException error) {
            LOG.error("URI Validation Error creating identity: "
                    + error.getMessage());
            throw error;
        } catch (Exception error) {
            LOG.error("Unexpected error during identity creation API call: "
                    + error.getMessage(), error);
            HttpResponseException.Builder builder = new HttpResponseException.Builder(
                    500,
                    "Unexpected error during identity creation",
                    new com.google.api.client.http.HttpHeaders()
            );
            throw new ApiErrorResponseException(builder);
        }
    }

    private String generateUvid() throws ApiErrorResponseException, URIValidationException {

        try {
            InternalApiClient internalApiClient = internalApiClientSupplier.get();
            PrivateIdentityVerificationResourceHandler handler = internalApiClient
                    .privateIdentityVerificationResourceHandler();

            var response = handler.getGeneratedUvid(GENERATE_UVID_URI).execute();

            if (response.getStatusCode() == 201) {
                return response.getData().getUnsourcedUvid();
            } else {
                LOG.error("Unexpected status code when generating UViD: "
                        + response.getStatusCode());
                throw new ApiErrorResponseException(new HttpResponseException.Builder(
                        response.getStatusCode(),
                        "Unexpected status when generating UViD",
                        new com.google.api.client.http.HttpHeaders()
                ));
            }

        } catch (ApiErrorResponseException error) {
            LOG.error("API Error generating UViD: Status=" + error.getStatusCode()
                    + ", Message=" + error.getStatusMessage());
            throw error;
        } catch (URIValidationException error) {
            LOG.error("URI Validation Error generating UViD: " + error.getMessage());
            throw error;
        } catch (Exception error) {
            LOG.error("Unexpected error during UViD generation: " + error.getMessage(), error);
            HttpResponseException.Builder builder = new HttpResponseException.Builder(
                    500,
                    "Unexpected error during UViD generation",
                    new com.google.api.client.http.HttpHeaders()
            );
            throw new ApiErrorResponseException(builder);
        }
    }

    void createUvidInMongo(String identityId, String generatedUvid) {

        try {
            // Create UViD entity
            Uvid uvid = new Uvid();
            uvid.setId(UUID.randomUUID().toString());
            uvid.setUvid(generatedUvid);
            uvid.setType("permanent");
            uvid.setIdentityId(identityId);
            uvid.setCreated(new Date());
            uvid.setSchemaVersion("1.0");

            uvidRepository.save(uvid);

        } catch (Exception error) {
            LOG.error("Failed to create UViD entry in MongoDB for identity ID: "
                    + identityId + ". Error: " + error.getMessage(), error);
            throw new RuntimeException("Failed to create UViD entry in MongoDB", error);
        }
    }
}