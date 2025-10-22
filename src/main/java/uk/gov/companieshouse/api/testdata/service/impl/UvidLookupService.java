package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class UvidLookupService {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UvidRepository uvidRepository;

    public String getUvidByIdentityId(String identityId) {
        Uvid uvidEntity = uvidRepository.findByIdentityId(identityId);

        if (uvidEntity != null) {
            String uvid = uvidEntity.getUvid();
            LOG.info("Found UViD: " + uvid + " for identity ID: " + identityId);
            return uvid;
        } else {
            LOG.info("No UViD found for identity ID: " + identityId);
            return null;
        }
    }
}