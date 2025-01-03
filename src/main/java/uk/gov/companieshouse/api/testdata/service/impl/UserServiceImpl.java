package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UserRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public UserData create(UserSpec userSpec) throws DataException {
        var dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();
        long timestamp = dateNow.toEpochMilli();
        final String password = userSpec.getPassword();
        final var user = new Users();
        if(userSpec.getRoles() != null){
            user.setRoles(userSpec.getRoles().stream().map(RoleSpec::getId).collect(Collectors.toList()));
        }
        String randomUser = "test-data-generated" + timestamp + "@test.companieshouse.gov.uk";
        user.setId(randomService.getString(24));
        user.setEmail(randomUser);
        user.setForename("Forename-"+timestamp);
        user.setSurname("Surname-"+timestamp);
        user.setLocale("GB_en");
        user.setPassword(password);
        user.setDirectLoginPrivilege(true);
        user.setCreated(dateNow);
        repository.save(user);
        return new UserData(user.getId(), user.getEmail(), user.getForename(), user.getSurname());
    }


    @Override
    public boolean delete(String userId) {
        try {
            var user = repository.findById(userId);
            user.ifPresent(repository::delete);
            return user.isPresent();
        } catch (Exception e) {
            LOG.error("Failed to delete user", e);
            return false;
        }
    }

    @Override
    public Users getUserById(String userId) {
        return repository.findById(userId).orElse(null);
    }
}
