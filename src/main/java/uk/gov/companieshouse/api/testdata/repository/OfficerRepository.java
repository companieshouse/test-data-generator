package uk.gov.companieshouse.api.testdata.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;

@NoRepositoryBean
public interface OfficerRepository extends MongoRepository<OfficerAppointment, String> {

    OfficerAppointment findByCompanyNumber(String companyId);
}
