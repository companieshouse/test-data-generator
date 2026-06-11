package uk.gov.companieshouse.api.testdata.service.impl.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AcspWorkflowService;
import uk.gov.companieshouse.api.testdata.service.DataService;


@Service
public class AcspWorkflowServiceImpl implements AcspWorkflowService {

    private final AcspProfileService acspProfileService;

    private final AcspMembersRepository acspMembersRepository;

    private final DataService<AcspMembersResponse, AcspMembersRequest> acspMembersService;

    public AcspWorkflowServiceImpl(
            AcspProfileService acspProfileService,
            AcspMembersRepository acspMembersRepository,
            DataService<AcspMembersResponse, AcspMembersRequest> acspMembersService) {
        this.acspProfileService = acspProfileService;
        this.acspMembersRepository = acspMembersRepository;
        this.acspMembersService = acspMembersService;
    }

    @Override
    public AcspMembersResponse createAcspMembersData(final AcspMembersRequest spec)
            throws DataException {
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

    public Optional<AcspProfile> getAcspProfileData(String acspNumber)
            throws NoDataFoundException {

        return acspProfileService.getAcspProfile(acspNumber);
    }

    private void deleteAcspProfile(String acspNumber, List<Exception> suppressedExceptions) {
        try {
            this.acspProfileService.delete(acspNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP profile", ex));
        }
    }

    private void deleteAcspMember(String acspMemberId, List<Exception> suppressedExceptions) {
        try {
            acspMembersService.delete(acspMemberId);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP member", ex));
        }
    }

}
