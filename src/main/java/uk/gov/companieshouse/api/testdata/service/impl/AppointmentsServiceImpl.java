package uk.gov.companieshouse.api.testdata.service.impl;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.constants.ErrorMessageConstants;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class AppointmentsServiceImpl implements DataService<Appointment> {

    private static final int SALT_LENGTH = 8;
    private static final int ID_LENGTH = 10;
    private static final String APPOINTMENT_DATA_NOT_FOUND = "appointment data not found";

    private RandomService randomService;
    private AppointmentsRepository repository;

    @Autowired
    public AppointmentsServiceImpl(RandomService randomService, AppointmentsRepository repository) {
        this.randomService = randomService;
        this.repository = repository;
    }

    @Override
    public Appointment create(String companyNumber) throws DataException {
        Appointment appointment = new Appointment();

        String encodedId = randomService.getEncodedIdWithSalt(ID_LENGTH, SALT_LENGTH);

        appointment.setCompanyNumber(companyNumber);
        appointment.setId(encodedId);

        try {
            repository.save(appointment);
        } catch (DuplicateKeyException e) {

            throw new DataException(ErrorMessageConstants.DUPLICATE_KEY);
        } catch (MongoException e) {

            throw new DataException(ErrorMessageConstants.FAILED_TO_INSERT);
        }

        return appointment;
    }

    @Override
    public void delete(String companyNumber) throws NoDataFoundException, DataException {
        Appointment existingAppointment = repository.findByCompanyNumber(companyNumber);

        if (existingAppointment == null) {
            throw new NoDataFoundException(APPOINTMENT_DATA_NOT_FOUND);
        }

        try {
            repository.delete(existingAppointment);
        } catch (MongoException e) {
            throw new DataException(ErrorMessageConstants.FAILED_TO_DELETE);
        }
    }
}
