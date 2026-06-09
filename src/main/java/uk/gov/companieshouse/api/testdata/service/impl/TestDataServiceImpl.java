package uk.gov.companieshouse.api.testdata.service.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class TestDataServiceImpl implements TestDataService {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private DataService<AcspMembersResponse, AcspMembersRequest> acspMembersService;
    @Autowired
    private DataService<CertificatesResponse, CertificatesRequest> certificatesService;
    @Autowired
    private DataService<CertificatesResponse, CertifiedCopiesRequest> certifiedCopiesService;
    @Autowired
    private DataService<CombinedSicActivitiesResponse,
            CombinedSicActivitiesRequest> combinedSicActivitiesService;
    @Autowired
    private DataService<CertificatesResponse, MissingImageDeliveriesRequest> missingImageDeliveriesService;
    @Autowired
    private AcspMembersRepository acspMembersRepository;
    @Autowired
    private DataService<TransactionsResponse, TransactionsRequest> transactionService;
    @Autowired
    private AcspProfileService acspProfileService;
    @Autowired
    AppealsService appealsService;
    @Autowired
    private AccountPenaltiesService accountPenaltiesService;
    @Autowired
    private PostcodeService postcodeService;
    private DataService<AdminPermissionsResponse, AdminPermissionsRequest> adminPermissionsService;
    @Autowired
    private ItemGroupsService itemGroupsService;

    @Override
    public AcspMembersResponse createAcspMembersData(final AcspMembersRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create an ACSP member");
        }

        var acspProfileSpec = new AcspProfileRequest();
        if (spec.getAcspProfile() != null) {
            acspProfileSpec = spec.getAcspProfile();
        }

        try {
            var acspProfileData = createAcspProfile(acspProfileSpec);
            spec.setAcspNumber(acspProfileData.getAcspNumber());

            AcspMembersResponse createdMember = createAcspMember(spec);

            return new AcspMembersResponse(
                    new ObjectId(createdMember.getAcspMemberId()),
                    createdMember.getAcspNumber(),
                    createdMember.getUserId(),
                    createdMember.getStatus(),
                    createdMember.getUserRole()
            );
        } catch (Exception ex) {
            throw new DataException(ex);
        }
    }

    private AcspProfileResponse createAcspProfile(AcspProfileRequest acspProfileRequest)
            throws DataException {
        try {
            return this.acspProfileService.create(acspProfileRequest);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP profile", ex);
        }
    }

    private AcspMembersResponse createAcspMember(AcspMembersRequest spec) throws DataException {
        try {
            return this.acspMembersService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP member", ex);
        }
    }

    @Override
    public boolean deleteAcspMembersData(String acspMemberId) throws DataException {
        List<Exception> suppressedExceptions = new ArrayList<>();
        try {
            var maybeMember = acspMembersRepository.findById(acspMemberId);
            if (maybeMember.isPresent()) {
                var member = maybeMember.get();
                String acspNumber = member.getAcspNumber();

                deleteAcspMember(acspMemberId, suppressedExceptions);
                deleteAcspProfile(acspNumber, suppressedExceptions);
            } else {
                return false;
            }
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }

        if (!suppressedExceptions.isEmpty()) {
            var ex = new DataException("Error deleting acsp member's data");
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }
        return true;
    }

    @Override
    public CertificatesResponse createCertificatesData(
            final CertificatesRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create certificates");
        }

        try {
            return certificatesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating certificates", ex);
        }
    }

    @Override
    public CertificatesResponse createCertifiedCopiesData(
            final CertifiedCopiesRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create certified copies");
        }
        try {
            return certifiedCopiesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating certified copies", ex);
        }
    }

    @Override
    public CertificatesResponse createMissingImageDeliveriesData(
            final MissingImageDeliveriesRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create missing image deliveries");
        }

        try {
            return missingImageDeliveriesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating missing image deliveries", ex);
        }
    }

    @Override
    public CombinedSicActivitiesResponse createCombinedSicActivitiesData(
            final CombinedSicActivitiesRequest spec) throws DataException {
        try {
            return combinedSicActivitiesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating Sic code and keyword", ex);
        }
    }


    @Override
    public boolean deleteCertificatesData(String id) throws DataException {
        try {
            return certificatesService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting certificates", ex);
        }
    }

    @Override
    public boolean deleteCertifiedCopiesData(String id) throws DataException {
        try {
            return certifiedCopiesService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting certified copies", ex);
        }
    }

    @Override
    public boolean deleteMissingImageDeliveriesData(String id) throws DataException {
        try {
            return missingImageDeliveriesService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting missing image deliveries", ex);
        }
    }

    @Override
    public boolean deleteAppealsData(String companyNumber, String penaltyReference)
            throws DataException {
        try {
            return appealsService.delete(companyNumber, penaltyReference);
        } catch (Exception ex) {
            throw new DataException("Error deleting appeals data", ex);
        }
    }

    @Override
    public boolean deleteCombinedSicActivitiesData(String id)
            throws DataException {
        try {
            return combinedSicActivitiesService.delete(String.valueOf(id));
        } catch (Exception ex) {
            throw new DataException("Error deleting appeals data", ex);
        }
    }

    @Override
    public AccountPenaltiesResponse getAccountPenaltiesData(String id)
            throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalties(id);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalties - not found");
        }
    }

    @Override
    public AccountPenaltiesResponse getAccountPenaltiesData(String customerCode, String companyCode)
            throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalties(customerCode, companyCode);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalties - not found");
        }
    }

    @Override
    public AccountPenaltiesResponse updateAccountPenaltiesData(
            String penaltyRef, UpdateAccountPenaltiesRequest request)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.updateAccountPenalties(penaltyRef, request);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error updating account penalties - not found");
        } catch (Exception ex) {
            throw new DataException("Error updating account penalties", ex);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenaltiesData(String id)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.deleteAccountPenalties(id);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error deleting account penalties - not found");
        } catch (Exception ex) {
            throw new DataException("Error deleting account penalties", ex);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenaltyByReference(
            String id, String transactionReference)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.deleteAccountPenaltyByReference(
                    id, transactionReference);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error deleting account penalty - not found");
        } catch (Exception ex) {
            throw new DataException("Error deleting account penalty", ex);
        }
    }

    @Override
    public AccountPenaltiesResponse createPenaltyData(PenaltyRequest penaltyRequest) throws DataException {
        try {
            LOG.info("Creating account penalties for company code: " + penaltyRequest.getCompanyCode()
                    + " and customer code: " + penaltyRequest.getCustomerCode());
            return accountPenaltiesService.createAccountPenalties(penaltyRequest);
        } catch (Exception ex) {
            LOG.error("Failed to create account penalties for company code: "
                    + penaltyRequest.getCompanyCode()
                    + " and customer code: " + penaltyRequest.getCustomerCode(), ex);
            throw new DataException("Error creating account penalties", ex);
        }
    }

    @Override
    public PostcodesResponse getPostcodes(String country) throws DataException {
        try {
            List<Postcodes> postcodes = postcodeService.getPostcodeByCountry(country);
            if (postcodes == null || postcodes.isEmpty()) {
                LOG.info("No postcodes found for country: " + country);
                return null;
            }
            var secureRandom = new SecureRandom();
            var randomPostcode = secureRandom.nextInt(postcodes.size());
            return getPostCodesData(postcodes).get(randomPostcode);
        } catch (Exception ex) {
            throw new DataException("Error retrieving postcodes", ex);
        }
    }

    private static List<PostcodesResponse> getPostCodesData(List<Postcodes> postcodes) {
        List<PostcodesResponse> postcodesResponseList = new ArrayList<>();
        for (Postcodes postcode : postcodes) {
            var postcodeData = new PostcodesResponse(
                    postcode.getBuildingNumber() != null ? postcode
                            .getBuildingNumber().intValue() : null,
                    postcode.getThoroughfare().getName() + " "
                            + (postcode.getThoroughfare().getDescriptor()
                            != null ? postcode.getThoroughfare().getDescriptor() : ""),
                    postcode.getLocality().getDependentLocality(),
                    postcode.getLocality().getPostTown(),
                    postcode.getPostcode().getPretty()
            );
            postcodesResponseList.add(postcodeData);
        }
        return postcodesResponseList;
    }

    public Optional<AcspProfile> getAcspProfileData(String acspNumber)
            throws NoDataFoundException {

        return acspProfileService.getAcspProfile(acspNumber);
    }

    private void deleteAcspMember(String acspMemberId, List<Exception> suppressedExceptions) {
        try {
            acspMembersService.delete(acspMemberId);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP member", ex));
        }
    }

    private void deleteAcspProfile(String acspNumber, List<Exception> suppressedExceptions) {
        try {
            this.acspProfileService.delete(acspNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP profile", ex));
        }
    }

    public TransactionsResponse createTransactionData(TransactionsRequest transactionsRequest)
            throws DataException {
        try {
            LOG.info("Creating Txn for User Id: " + transactionsRequest.getUserId());
            return transactionService.create(transactionsRequest);
        } catch (Exception ex) {
            LOG.error("Failed to create Transaction for User Id: "
                    + transactionsRequest.getUserId());
            throw new DataException("Error creating transaction", ex);
        }
    }

    @Override
    public AdminPermissionsResponse createAdminPermissionsData(
            AdminPermissionsRequest spec) throws DataException {
        return adminPermissionsService.create(spec);
    }

    @Override
    public boolean deleteAdminPermissionsData(String id) throws DataException {
        try {
            return adminPermissionsService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting admin permissions", ex);
        }
    }

    @Override
    public boolean deleteTransaction(String transactionId) throws DataException {
        try {
            return transactionService.delete(transactionId);
        } catch (Exception ex) {
            throw new DataException("Error deleting transaction", ex);
        }
    }

    @Override
    public boolean deleteItemGroupsData(String orderNumber) throws DataException {
        try {
            return itemGroupsService.deleteItemGroups(orderNumber);
        } catch (Exception ex) {
            throw new DataException("Error deleting Item Groups", ex);
        }
    }
}
