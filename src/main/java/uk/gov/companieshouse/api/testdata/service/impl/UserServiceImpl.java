package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Autowired
    private UserRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public UserData create(UserSpec userSpec) throws DataException {
        var dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();
        long timestamp = dateNow.toEpochMilli();
        final String password = userSpec.getPassword();
        final var user = new User();
        if(userSpec.getRoles() != null){
            user.setRoles(userSpec.getRoles().stream().map(RoleSpec::getId).collect(Collectors.toList()));
        }
        String email = "test-data-generated" + timestamp + "@test.companieshouse.gov.uk";
        user.setId(randomService.getString(24));
        user.setEmail(email);
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
        var user = repository.findById(userId);
        user.ifPresent(repository::delete);
        return user.isPresent();
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return repository.findById(userId);
    }
}
