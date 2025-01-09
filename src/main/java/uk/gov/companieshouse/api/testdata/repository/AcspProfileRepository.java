package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;

import java.util.Optional;

@NoRepositoryBean
public interface AcspProfileRepository extends MongoRepository<AcspProfile, String> {
    Optional<Appointment> findByAcspNumber(String acspNumber);
}
