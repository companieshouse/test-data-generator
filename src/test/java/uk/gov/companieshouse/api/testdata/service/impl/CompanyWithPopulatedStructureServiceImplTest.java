package uk.gov.companieshouse.api.testdata.service.impl;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.OfficerAppointment;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;

import uk.gov.companieshouse.api.testdata.repository.AppointmentsDataRepository;
import uk.gov.companieshouse.api.testdata.repository.AppointmentsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyAuthCodeRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyProfileRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscStatementRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyPscsRepository;
import uk.gov.companieshouse.api.testdata.repository.CompanyRegistersRepository;
import uk.gov.companieshouse.api.testdata.repository.DisqualificationsRepository;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.repository.OfficerRepository;

@ExtendWith(MockitoExtension.class)
class CompanyWithPopulatedStructureServiceImplTest {

    @Mock
    private CompanyProfileRepository companyProfileRepository;
    @Mock
    private CompanyAuthCodeRepository authCodeRepository;
    @Mock
    private FilingHistoryRepository filingHistoryRepository;
    @Mock
    private AppointmentsDataRepository appointmentsDataRepository;
    @Mock
    private AppointmentsRepository appointmentRepository;
    @Mock
    private CompanyMetricsRepository companyMetricsRepository;
    @Mock
    private CompanyPscStatementRepository companyPscStatementRepository;
    @Mock
    private CompanyPscsRepository companyPscsRepository;
    @Mock
    private CompanyRegistersRepository companyRegistersRepository;
    @Mock
    private DisqualificationsRepository disqualificationsRepository;
    @Mock
    private OfficerRepository officerRepository;

    @InjectMocks
    private CompanyWithPopulatedStructureServiceImpl service;

    @Test
    void createCompany_WithPopulatedStructure_savesAllEntities() {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();

        CompanyProfile profile = new CompanyProfile();
        spec.setCompanyProfile(profile);

        CompanyAuthCode authCode = new CompanyAuthCode();
        spec.setCompanyAuthCode(authCode);

        FilingHistory filingHistory = new FilingHistory();
        spec.setFilingHistory(filingHistory);

        AppointmentsData data1 = new AppointmentsData();
        AppointmentsData data2 = new AppointmentsData();
        Appointment appointment1 = new Appointment();
        Appointment appointment2 = new Appointment();
        OfficerAppointment officerAppointment = new OfficerAppointment();

        AppointmentsResultResponse appointmentsDataList = new AppointmentsResultResponse();
        appointmentsDataList.setAppointmentsData(List.of(data1, data2));
        appointmentsDataList.setAppointment(List.of(appointment1, appointment2));
        appointmentsDataList.setOfficerAppointment(List.of(officerAppointment));
        spec.setAppointmentsData(appointmentsDataList);

        CompanyMetrics metrics = new CompanyMetrics();
        spec.setCompanyMetrics(metrics);

        CompanyPscStatement pscStatement = new CompanyPscStatement();
        spec.setCompanyPscStatement(List.of(pscStatement));

        CompanyPscs pscs = new CompanyPscs();
        spec.setCompanyPscs(List.of(pscs));

        CompanyRegisters registers = new CompanyRegisters();
        spec.setCompanyRegisters(registers);

        Disqualifications disqualifications = new Disqualifications();
        spec.setDisqualifications(disqualifications);

        service.createCompanyWithPopulatedStructure(spec);

        verify(companyProfileRepository).save(profile);
        verify(authCodeRepository).save(authCode);
        verify(filingHistoryRepository).save(filingHistory);
        verify(appointmentsDataRepository, times(2)).save(any(AppointmentsData.class));
        verify(appointmentRepository, times(2)).save(any(Appointment.class));
        verify(officerRepository).save(officerAppointment);
        verify(companyMetricsRepository).save(metrics);
        verify(companyPscStatementRepository).save(pscStatement);
        verify(companyPscsRepository).save(pscs);
        verify(companyRegistersRepository).save(registers);
        verify(disqualificationsRepository).save(disqualifications);
    }

    @Test
    void createCompany_WithPopulatedStructure_handlesNullsGracefully() {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();
        spec.setCompanyProfile(new CompanyProfile());
        // All other fields are null

        service.createCompanyWithPopulatedStructure(spec);

        verify(companyProfileRepository).save(any(CompanyProfile.class));
        verifyNoInteractions(authCodeRepository, filingHistoryRepository,
                appointmentsDataRepository,
                appointmentRepository, officerRepository, companyMetricsRepository,
                companyPscStatementRepository, companyPscsRepository, companyRegistersRepository,
                disqualificationsRepository);
    }

    @Test
    void createCompany_WithPopulatedStructure_appointmentsDataWithNullLists_doesNotSave() {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();
        spec.setCompanyProfile(new CompanyProfile());
        AppointmentsResultResponse appointmentsData = new AppointmentsResultResponse();
        // All sublists are null
        spec.setAppointmentsData(appointmentsData);

        service.createCompanyWithPopulatedStructure(spec);

        verify(companyProfileRepository).save(any());
        verifyNoInteractions(appointmentsDataRepository, appointmentRepository, officerRepository);
    }

    @Test
    void createCompany_WithPopulatedStructure_emptyLists_doesNotSave() {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();
        spec.setCompanyProfile(new CompanyProfile());
        AppointmentsResultResponse appointmentsData = new AppointmentsResultResponse();
        appointmentsData.setAppointmentsData(List.of());
        appointmentsData.setAppointment(List.of());
        appointmentsData.setOfficerAppointment(List.of());
        spec.setAppointmentsData(appointmentsData);
        spec.setCompanyPscStatement(List.of());
        spec.setCompanyPscs(List.of());

        service.createCompanyWithPopulatedStructure(spec);

        verify(companyProfileRepository).save(any());
        verifyNoInteractions(appointmentsDataRepository, appointmentRepository, officerRepository,
                companyPscStatementRepository, companyPscsRepository);
    }

    @Test
    void createCompany_WithPopulatedStructure_multiplePscStatementsAndPscs_allSaved() {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();
        spec.setCompanyProfile(new CompanyProfile());
        CompanyPscStatement s1 = new CompanyPscStatement();
        CompanyPscStatement s2 = new CompanyPscStatement();
        spec.setCompanyPscStatement(List.of(s1, s2));
        CompanyPscs p1 = new CompanyPscs();
        CompanyPscs p2 = new CompanyPscs();
        spec.setCompanyPscs(List.of(p1, p2));

        service.createCompanyWithPopulatedStructure(spec);

        verify(companyPscStatementRepository).save(s1);
        verify(companyPscStatementRepository).save(s2);
        verify(companyPscsRepository).save(p1);
        verify(companyPscsRepository).save(p2);
    }

}
