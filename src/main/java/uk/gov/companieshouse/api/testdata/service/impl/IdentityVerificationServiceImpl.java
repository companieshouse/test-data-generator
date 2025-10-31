package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityVerificationData;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class IdentityVerificationServiceImpl implements
        VerifiedIdentityService<IdentityVerificationData> {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private IdentityRepository repository;

    @Autowired
    private UvidRepository uvidRepository;

    @Override
    public IdentityVerificationData getIdentityVerificationData(String email) {
        LOG.debug("getIdentityVerificationData called with email= "
                + email);

        if (email == null || email.isBlank()) {
            LOG.debug("Email is null or blank; returning null");
            return null;
        }

        Optional<Identity> identityOpt = repository.findByEmail(email);
        if (identityOpt.isEmpty()) {
            LOG.debug("No identity found for email= "
                    + email);
            return null;
        }

        var identity = identityOpt.get();
        LOG.debug("Found identity id= "
                + identity.getId() + " for email= " + email);

        var uvidOpt = uvidRepository.findByIdentityId(identity.getId());
        if (uvidOpt.isEmpty()) {
            LOG.debug("No UVID found for identityId= " + identity.getId());
            return null;
        }

        var uvid = uvidOpt.get();
        LOG.debug("Found UVID value= " + uvid.getValue() + " for identityId= " + identity.getId());

        var data = new IdentityVerificationData(identity.getId(), uvid.getValue());
        LOG.debug("Returning IdentityVerificationData for email "
                + email + " and identityId= " + identity.getId());
        return data;
    }
}