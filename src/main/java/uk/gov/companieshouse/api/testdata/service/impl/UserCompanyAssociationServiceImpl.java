package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Invitation;
import uk.gov.companieshouse.api.testdata.model.entity.PreviousState;
import uk.gov.companieshouse.api.testdata.model.entity.UserCompanyAssociation;
import uk.gov.companieshouse.api.testdata.model.rest.InvitationSpec;
import uk.gov.companieshouse.api.testdata.model.rest.PreviousStateSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class UserCompanyAssociationServiceImpl implements
        DataService<UserCompanyAssociationData, UserCompanyAssociationSpec> {

    @Autowired
    private UserCompanyAssociationRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public UserCompanyAssociationData create(UserCompanyAssociationSpec spec) throws DataException {
        var randomId = randomService.generateId();
        var association = new UserCompanyAssociation();
        var currentDate = getCurrentDateTime();

        association.setId(randomId);
        association.setCompanyNumber(spec.getCompanyNumber());
        association.setUserId(spec.getUserId());
        association.setUserEmail(spec.getUserEmail());
        association.setStatus(Objects.requireNonNullElse(spec.getStatus(),
                "confirmed"));
        association.setCreatedAt(currentDate);
        association.setApprovalRoute(Objects.requireNonNullElse(spec.getApprovalRoute(),
                "auth_code"));

        if (spec.getInvitations() != null) {
            List<Invitation> invitationList = new ArrayList<>();
            for (InvitationSpec invite : spec.getInvitations()) {
                var invitation = new Invitation();
                invitation.setInvitedAt(invite.getInvitedAt());
                invitation.setInvitedBy(invite.getInvitedBy());
                invitationList.add(invitation);
            }
            association.setInvitations(invitationList);
            association.setApprovalExpiryAt(Objects.requireNonNullElse(spec.getApprovalExpiryAt(),
                    currentDate.plus(7,
                            ChronoUnit.DAYS)));
        }

        if (spec.getPreviousStates() != null) {
            List<PreviousState> previousStateList = new ArrayList<>();
            for (PreviousStateSpec prevState :
                    spec.getPreviousStates()) {
                var previousState = new PreviousState();
                previousState.setChangedBy(prevState.getChangedBy());
                previousState.setChangedAt(prevState.getChangedAt());
                previousState.setStatus(prevState.getStatus());
                previousStateList.add(previousState);
            }
            association.setPreviousStates(previousStateList);
        }
        repository.save(association);

        return new UserCompanyAssociationData(
                association.getId(),
                association.getCompanyNumber(),
                association.getUserId(),
                association.getUserEmail(),
                association.getStatus(),
                association.getApprovalRoute(),
                association.getInvitations());
    }

    @Override
    public boolean delete(String id) {
        var association =
                repository.findById(id);
        association.ifPresent(repository::delete);
        return association.isPresent();
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}