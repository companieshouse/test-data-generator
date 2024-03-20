package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.DeltaAppointment;

import java.util.Optional;

@NoRepositoryBean
public interface DeltaAppointmentsRepository extends MongoRepository<DeltaAppointment, String> {
    Optional<DeltaAppointment> findByCompanyNumber(String companyNumber);
}
