package uk.gov.companieshouse.api.testdata.service.impl.workflow;

import java.util.ArrayList;
import java.util.List;
import org.bson.types.ObjectId;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.repository.AcspMemberRepository;
import uk.gov.companieshouse.api.testdata.service.AcspMemberService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AcspWorkflowService;


@Service
public class AcspWorkflowServiceImpl implements AcspWorkflowService {

    private final AcspProfileService acspProfileService;

    private final AcspMemberRepository acspMemberRepository;

    private final AcspMemberService acspMemberService;

    public AcspWorkflowServiceImpl(
            AcspProfileService acspProfileService,
            AcspMemberRepository acspMemberRepository,
            AcspMemberService acspMemberService) {
        this.acspProfileService = acspProfileService;
        this.acspMemberRepository = acspMemberRepository;
        this.acspMemberService = acspMemberService;
    }

    @Override
    public AcspMembersResponse createAcspMember(final AcspMembersRequest spec)
            throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create an ACSP member");
        }

        var acspProfileSpec = new AcspProfileRequest();
        if (spec.getAcspProfile() != null) {
            acspProfileSpec = spec.getAcspProfile();
        }

        try {
            var acspProfileData = createAcspProfileRecord(acspProfileSpec);
            spec.setAcspNumber(acspProfileData.getAcspNumber());

            AcspMembersResponse createdMember = createAcspMemberRecord(spec);

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

    private AcspProfileResponse createAcspProfileRecord(AcspProfileRequest acspProfileRequest)
            throws DataException {
        try {
            return this.acspProfileService.create(acspProfileRequest);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP profile", ex);
        }
    }

    private AcspMembersResponse createAcspMemberRecord(AcspMembersRequest spec) throws DataException {
        try {
            return this.acspMemberService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP member", ex);
        }
    }

    @Override
    public boolean deleteAcspMember(String acspMemberId) throws DataException {
        List<Exception> suppressedExceptions = new ArrayList<>();
        try {
            var maybeMember = acspMemberRepository.findById(acspMemberId);
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

    private void deleteAcspProfile(String acspNumber, List<Exception> suppressedExceptions) {
        try {
            this.acspProfileService.delete(acspNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP profile", ex));
        }
    }

    private void deleteAcspMember(String acspMemberId, List<Exception> suppressedExceptions) {
        try {
            acspMemberService.delete(acspMemberId);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP member", ex));
        }
    }

}
