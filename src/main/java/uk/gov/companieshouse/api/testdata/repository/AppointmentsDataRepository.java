package uk.gov.companieshouse.api.testdata.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;

public interface AppointmentsDataRepository extends MongoRepository<AppointmentsData, String> {

    /**
     * Finds all appointments by company number.
     *
     * @param companyNumber the company number to search for
     * @return a list of appointments associated with the specified company number
     */
    List<AppointmentsData> findAllByCompanyNumber(String companyNumber);
}
