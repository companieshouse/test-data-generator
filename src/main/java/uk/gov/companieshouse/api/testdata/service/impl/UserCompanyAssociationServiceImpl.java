package uk.gov.companieshouse.api.testdata.service.impl;

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
    private static final String AUTH_CODE = "auth_code";
    private static final String CONFIRMED_STATUS = "confirmed";

    private final UserCompanyAssociationRepository repository;

    private final RandomService randomService;

    @Autowired
    public UserCompanyAssociationServiceImpl(UserCompanyAssociationRepository repository, RandomService randomService) {
        super();
        this.repository = repository;
        this.randomService = randomService;
    }

    @Override
    public UserCompanyAssociationData create(UserCompanyAssociationSpec spec) throws DataException {
        var randomId = randomService.generateId();
        var association = new UserCompanyAssociation();
        var currentDate = randomService.getCurrentDateTime();

        association.setId(randomId);
        association.setCompanyNumber(spec.getCompanyNumber());
        association.setUserId(spec.getUserId());
        association.setUserEmail(spec.getUserEmail());
        association.setStatus(Objects.requireNonNullElse(spec.getStatus(),
                CONFIRMED_STATUS));
        association.setCreatedAt(currentDate);
        association.setApprovalRoute(Objects.requireNonNullElse(spec.getApprovalRoute(),
                AUTH_CODE));
        association.setInvitations(spec.getInvitations() != null ? createInvitations(spec) : null);
        association.setApprovalExpiryAt(spec.getInvitations() != null
                ? Objects.requireNonNullElse(spec.getApprovalExpiryAt(),
                    currentDate.plus(7,
                            ChronoUnit.DAYS)) : null);
        association.setPreviousStates(spec.getPreviousStates() != null
                ? createPreviousStates(spec) : null);

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

    private List<Invitation> createInvitations(UserCompanyAssociationSpec spec) {
        List<Invitation> invitationList = new ArrayList<>();
        for (InvitationSpec invite : spec.getInvitations()) {
            var invitation = new Invitation();
            invitation.setInvitedAt(invite.getInvitedAt());
            invitation.setInvitedBy(invite.getInvitedBy());
            invitationList.add(invitation);
        }
        return invitationList;
    }

    private List<PreviousState> createPreviousStates(UserCompanyAssociationSpec spec) {
        List<PreviousState> previousStateList = new ArrayList<>();
        for (PreviousStateSpec prevState :
                spec.getPreviousStates()) {
            var previousState = new PreviousState();
            previousState.setChangedBy(prevState.getChangedBy());
            previousState.setChangedAt(prevState.getChangedAt());
            previousState.setStatus(prevState.getStatus());
            previousStateList.add(previousState);
        }
        return previousStateList;
    }
}