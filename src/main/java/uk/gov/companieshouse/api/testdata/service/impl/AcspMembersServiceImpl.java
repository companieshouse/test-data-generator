package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspMembersServiceImpl implements DataService<AcspMembersResponse, AcspMembersRequest> {

    @Autowired
    private AcspMembersRepository repository;

    @Autowired
    private RandomService randomService;


    @Override
    public AcspMembersResponse create(AcspMembersRequest acspMembersRequest) throws DataException {
        var randomId = randomService.generateId();
        var acspMembers = new AcspMembers();
        var currentDate = getCurrentDateTime();

        acspMembers.setAcspMemberId(randomId);
        acspMembers.setAcspNumber(acspMembersRequest.getAcspNumber());
        acspMembers.setUserId(acspMembersRequest.getUserId());
        acspMembers.setUserRole(Objects.requireNonNullElse(acspMembersRequest.getUserRole(),
                "owner"));
        acspMembers.setStatus(Objects.requireNonNullElse(acspMembersRequest.getStatus(),
                "active"));
        acspMembers.setCreatedAt(currentDate);
        acspMembers.setAddedAt(currentDate);
        acspMembers.setEtag(randomService.getEtag());
        acspMembers.setVersion(0);

        repository.save(acspMembers);

        return new AcspMembersResponse(
                acspMembers.getAcspMemberId(),
                acspMembers.getAcspNumber(),
                acspMembers.getUserId(),
                acspMembers.getStatus(),
                acspMembers.getUserRole()
        );
    }

    @Override
    public boolean delete(String acspMemberId) {
        var acspMembers =
                repository.findByAcspMemberId(new ObjectId(acspMemberId));
        acspMembers.ifPresent(repository::delete);
        return acspMembers.isPresent();
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}
