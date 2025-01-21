package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspMembersServiceImpl implements DataService<AcspMembersData, AcspMembersSpec> {

    @Autowired
    private AcspMembersRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RandomService randomService;


    @Override
    public AcspMembersData create(AcspMembersSpec acspMembersSpec) throws DataException {
        if (acspMembersSpec == null) {
            throw new DataException("AcspMembersSpec cannot be null");
        }

        String randomId = randomService.getString(12);
        AcspMembers acspMembers = new AcspMembers();

        acspMembers.setAcspMemberId(randomId);
        acspMembers.setAcspNumber(acspMembersSpec.getAcspNumber());
        acspMembers.setUserId(acspMembersSpec.getUserId());
        acspMembers.setUserRole(Objects.requireNonNullElse(acspMembersSpec.getUserRole(),
                "member"));
        acspMembers.setStatus(Objects.requireNonNullElse(acspMembersSpec.getStatus(),
                "active"));
        acspMembers.setCreatedAt(Date.from(Instant.now()));
        acspMembers.setAddedAt(Date.from(Instant.now()));
        acspMembers.setEtag(randomService.getEtag());
        acspMembers.setVersion(0);

        repository.save(acspMembers);

        return new AcspMembersData(
                acspMembers.getAcspMemberId(),
                acspMembers.getAcspNumber(),
                acspMembers.getUserId(),
                acspMembers.getStatus(),
                acspMembers.getUserRole()
        );
    }

    @Override
    public boolean delete(String acspMemberId) {
        var acspMembers = repository.findById(acspMemberId);
        acspMembers.ifPresent(repository::delete);
        return acspMembers.isPresent();
    }
}
