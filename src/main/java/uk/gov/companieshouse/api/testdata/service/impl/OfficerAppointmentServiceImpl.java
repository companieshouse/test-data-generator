package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointmentItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;
import uk.gov.companieshouse.api.testdata.service.AddressService;
import uk.gov.companieshouse.api.testdata.service.OfficerAppointmentService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class OfficerAppointmentServiceImpl implements OfficerAppointmentService {

    @Autowired
    private RandomService randomService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public OfficerAppointment create(CompanySpec spec, String officerId, String appointmentId)
            throws DataException {

        OfficerAppointment officerAppointment = new OfficerAppointment();

        Instant dayTimeNow = Instant.now();
        Instant dayNow = LocalDate.now().atStartOfDay(ZoneId.of("UTC")).toInstant();

        officerAppointment.setId(officerId);
        officerAppointment.setCreatedAt(dayTimeNow);
        officerAppointment.setUpdatedAt(dayTimeNow);

        officerAppointment.setTotalResults(1);
        officerAppointment.setActiveCount(1);
        officerAppointment.setInactiveCount(0);
        officerAppointment.setResignedCount(1);
        officerAppointment.setCorporateOfficer(false);
        officerAppointment.setName("Test DIRECTOR");

        Links links = new Links();
        links.setSelf("/officers/" + officerId + "/appointments");
        officerAppointment.setLinks(links);

        officerAppointment.setEtag(this.randomService.getEtag());
        officerAppointment.setDateOfBirthYear(1990);
        officerAppointment.setDateOfBirthMonth(3);
        officerAppointment.setOfficerAppointmentItems(createItems(spec, appointmentId, dayNow, dayTimeNow));

        try {
            return officerRepository.save(officerAppointment);
        } catch (MongoException e) {
            throw new DataException("Failed to save officer appointment", e);
        }

    }

    @Override
    public void delete(String officerId) throws NoDataFoundException, DataException {
        //TODO delete Officer Appointment data
    }

    private List<OfficerAppointmentItem> createItems(CompanySpec companySpec, String appointmentId,
                                                     Instant dayNow, Instant dayTimeNow) {
        List<OfficerAppointmentItem> officerAppointmentItemList = new ArrayList<>();

        String companyNumber = companySpec.getCompanyNumber();
        Jurisdiction jurisdiction = companySpec.getJurisdiction();

        OfficerAppointmentItem officerAppointmentItem = new OfficerAppointmentItem();
        officerAppointmentItem.setOccupation("Director");
        officerAppointmentItem.setAddress(addressService.getAddress(jurisdiction));
        officerAppointmentItem.setForename("Test");
        officerAppointmentItem.setSurname("Director");
        officerAppointmentItem.setOfficerRole("director");
        officerAppointmentItem.setLinks(createItemLinks(companyNumber, appointmentId));
        officerAppointmentItem.setCountryOfResidence(addressService.getCountryOfResidence(jurisdiction));
        officerAppointmentItem.setAppointedOn(dayNow);
        officerAppointmentItem.setNationality("British");
        officerAppointmentItem.setUpdatedAt(dayTimeNow);
        officerAppointmentItem.setName("Test DIRECTOR");
        officerAppointmentItem.setCompanyName("Company " + companyNumber);
        officerAppointmentItem.setCompanyNumber(companyNumber);
        officerAppointmentItem.setCompanyStatus("active");

        officerAppointmentItemList.add(officerAppointmentItem);

        return officerAppointmentItemList;
    }

    private Links createItemLinks(String companyNumber, String appointmentId) {

        Links links = new Links();
        links.setSelf("/company/" + companyNumber + "/appointments/" + appointmentId);
        links.setCompany("/company/" + companyNumber);

        return links;
    }

}
