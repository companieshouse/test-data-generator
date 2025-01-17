package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.AcspMembersService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AcspMembersServiceImpl implements AcspMembersService {

    @Autowired
    private AcspMembersRepository repository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AcspProfileService acspProfileService;

    @Autowired
    private RandomService randomService;

    private static final String ACSP_MEMBER_PREFIX = "ACSPM";

    @Override
    public AcspMembersData create(AcspMembersSpec acspMembersSpec) throws DataException {

        if (acspMembersSpec == null) {
            throw new DataException("AcspMembersSpec cannot be null");
        }

        AcspProfileSpec profileSpec = new AcspProfileSpec();
        final var acspProfile = new AcspProfile();
        final String profileType = profileSpec.getType();
        final String profileStatus = profileSpec.getStatus();
        profileSpec.setType(Objects.requireNonNullElse(profileType, "ltd"));
        profileSpec.setStatus(Objects.requireNonNullElse(profileStatus, "active"));

        AcspProfileData acspProfileData = acspProfileService.create(profileSpec);
        String userId = acspMembersSpec.getUserId();
        if (userId == null || userId.isEmpty()) {
            throw new DataException("User ID must be provided");
        }
        // Confirm user exists
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataException("User ID '" + userId + "' not found in users collection");
        }

        String newAcspNumber = acspProfileData.getAcspNumber();
        var randomId = randomService.getNumber(4); // e.g. 4-digit random
        final var acspMembers = new AcspMembers();

        acspMembers.setAcspMemberId(ACSP_MEMBER_PREFIX + randomId);
        acspMembers.setAcspNumber(newAcspNumber);
        acspMembers.setUserId(userId);

        String role = acspMembersSpec.getUserRole();
        acspMembers.setUserRole((role == null || role.isEmpty()) ? "member" : role);

        String status = acspMembersSpec.getStatus();
        acspMembers.setStatus((status == null || status.isEmpty()) ? "active" : status);
        acspMembers.setCreatedAt(Date.from(Instant.now()));
        acspMembers.setAddedAt(Date.from(Instant.now()));
        acspMembers.setAddedBy(null);
        acspMembers.setRemovedAt(null);
        acspMembers.setRemovedBy(null);

        acspMembers.setEtag(this.randomService.getEtag());
        acspMembers.setVersion(0);

        repository.save(acspMembers);
        return new AcspMembersData(
                acspMembers.getAcspNumber(),
                acspMembers.getUserId(),
                acspMembers.getAcspMemberId(),
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

    @Override
    public Optional<AcspMembers> getAcspMembersById(String acspMemberId) {
        return repository.findById(acspMemberId);
    }
}
