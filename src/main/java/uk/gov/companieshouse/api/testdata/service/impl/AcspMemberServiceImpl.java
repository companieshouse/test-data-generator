package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.repository.AcspMemberRepository;
import uk.gov.companieshouse.api.testdata.service.AcspMemberService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspMemberServiceImpl implements
        AcspMemberService {

    private final AcspMemberRepository acspMemberRepository;

    private final RandomService randomService;

    public AcspMemberServiceImpl(AcspMemberRepository acspMemberRepository, RandomService randomService) {
        this.acspMemberRepository = acspMemberRepository;
        this.randomService = randomService;
    }

    @Override
    public AcspMembersResponse create(AcspMembersRequest request) throws DataException {
        var randomId = randomService.generateId();
        var acspMember = new AcspMembers();
        var currentUtcInstant = getCurrentUtcInstant();

        acspMember.setAcspMemberId(randomId);
        acspMember.setAcspNumber(request.getAcspNumber());
        acspMember.setUserId(request.getUserId());
        acspMember.setUserRole(Objects.requireNonNullElse(request.getUserRole(),
                "owner"));
        acspMember.setStatus(Objects.requireNonNullElse(request.getStatus(),
                "active"));
        acspMember.setCreatedAt(currentUtcInstant);
        acspMember.setAddedAt(currentUtcInstant);
        acspMember.setEtag(randomService.getEtag());
        acspMember.setVersion(0);

        acspMemberRepository.save(acspMember);

        return new AcspMembersResponse(
                acspMember.getAcspMemberId(),
                acspMember.getAcspNumber(),
                acspMember.getUserId(),
                acspMember.getStatus(),
                acspMember.getUserRole()
        );
    }

    @Override
    public boolean delete(String acspMemberId) {
        var acspMember = acspMemberRepository.findByAcspMemberId(new ObjectId(acspMemberId));
        acspMember.ifPresent(acspMemberRepository::delete);
        return acspMember.isPresent();
    }

    protected Instant getCurrentUtcInstant() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}
