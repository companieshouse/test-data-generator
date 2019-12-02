package uk.gov.companieshouse.api.testdata.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;

@NoRepositoryBean
public interface AppointmentsRepository extends MongoRepository<Appointment, String> {
    Optional<Appointment> findByCompanyNumber(String companyNumber);
}
