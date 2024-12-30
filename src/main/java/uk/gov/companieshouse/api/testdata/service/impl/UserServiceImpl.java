package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UserRepository repository;

    @Autowired
    private RandomService randomService;

    public boolean userExists(String userId) {
        return repository.findById(userId).isPresent();
    }

    @Override
    public UserTestData create(UserSpec userSpec) throws DataException {
        var dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();
        long timestamp = dateNow.toEpochMilli();
        final String password = userSpec.getPassword();
        final var user = new Users();
        if(userSpec.getRoles() != null){
            user.setRoles(userSpec.getRoles().stream().map(RoleSpec::getId).toList());
        }
        String randomUser = "playwright-user" + timestamp + "@test.companieshouse.gov.uk";
        user.setId(randomService.getString(24));
        user.setEmail(randomUser);
        user.setForename("Forename-"+timestamp);
        user.setSurname("Surname-"+timestamp);
        user.setLocale("GB_en");
        user.setPassword(password);
        user.setDirectLoginPrivilege(true);
        user.setCreated(dateNow);
        repository.save(user);
        return new UserTestData(user.getId(), user.getEmail(), user.getForename(), user.getSurname());
    }


    @Override
    public boolean delete(String userId) throws DataException {
        if(!userExists(userId)){
            return false;
        }
        Users user = repository.findById(userId).get();
        try {
            repository.delete(user);
            return !userExists(userId);
        } catch (Exception e) {
            LOG.error("Failed to delete user", e);
            throw new DataException("Failed to delete user", e);
        }
    }

    @Override
    public List<String> getRolesByUserId(String userId) {
        return repository.findById(userId)
                .map(Users::getRoles)
                .orElse(new ArrayList<>());
    }
}
