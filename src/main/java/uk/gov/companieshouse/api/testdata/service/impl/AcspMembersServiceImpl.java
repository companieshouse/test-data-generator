package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.AcspMembersService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspMembersServiceImpl implements AcspMembersService {

    @Autowired
    private AcspMembersRepository repository;

    @Autowired
    private RandomService randomService;


    @Override
    public AcspMembersData create(AcspMembersSpec acspMembersSpec) throws DataException {
        var randomId = randomService.getString(12);
        var acspMembers = new AcspMembers();
        var currentDate = getCurrentDateTime();

        acspMembers.setAcspMemberId(randomId);
        acspMembers.setAcspNumber(acspMembersSpec.getAcspNumber());
        acspMembers.setUserId(acspMembersSpec.getUserId());
        acspMembers.setUserRole(Objects.requireNonNullElse(acspMembersSpec.getUserRole(),
                "owner"));
        acspMembers.setStatus(Objects.requireNonNullElse(acspMembersSpec.getStatus(),
                "active"));
        acspMembers.setCreatedAt(currentDate);
        acspMembers.setAddedAt(currentDate);
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

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }

    @Override
    public List<AcspMembers> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    @Override
    public boolean deleteByUserId(String userId) {
        var acspMemberships = repository.findAllByUserId(userId);
        if (!acspMemberships.isEmpty()) {
            repository.deleteAll(acspMemberships);
            return true;
        }
        return false;
    }
}
