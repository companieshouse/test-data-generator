package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
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
import uk.gov.companieshouse.api.testdata.service.CompanyWithPopulatedStructureService;

@Service
public class CompanyWithPopulatedStructureServiceImpl
        implements CompanyWithPopulatedStructureService {

    private final CompanyProfileRepository companyProfileRepository;
    private final CompanyAuthCodeRepository authCodeRepository;
    private final FilingHistoryRepository filingHistoryRepository;
    private final AppointmentsDataRepository appointmentsDataRepository;
    private final AppointmentsRepository appointmentRepository;
    private final CompanyMetricsRepository companyMetricsRepository;
    private final CompanyPscStatementRepository companyPscStatementRepository;
    private final CompanyPscsRepository companyPscsRepository;
    private final CompanyRegistersRepository companyRegistersRepository;
    private final DisqualificationsRepository disqualificationsRepository;
    private final OfficerRepository officerRepository;

    @Autowired
    public CompanyWithPopulatedStructureServiceImpl(
            CompanyProfileRepository companyProfileRepository,
            CompanyAuthCodeRepository authCodeRepository,
            FilingHistoryRepository filingHistoryRepository,
            AppointmentsDataRepository appointmentsDataRepository,
            AppointmentsRepository appointmentRepository,
            CompanyMetricsRepository companyMetricsRepository,
            CompanyPscStatementRepository companyPscStatementRepository,
            CompanyPscsRepository companyPscsRepository,
            CompanyRegistersRepository companyRegistersRepository,
            DisqualificationsRepository disqualificationsRepository,
            OfficerRepository officerRepository) {
        this.companyProfileRepository = companyProfileRepository;
        this.authCodeRepository = authCodeRepository;
        this.filingHistoryRepository = filingHistoryRepository;
        this.appointmentsDataRepository = appointmentsDataRepository;
        this.appointmentRepository = appointmentRepository;
        this.companyMetricsRepository = companyMetricsRepository;
        this.companyPscStatementRepository = companyPscStatementRepository;
        this.companyPscsRepository = companyPscsRepository;
        this.companyRegistersRepository = companyRegistersRepository;
        this.disqualificationsRepository = disqualificationsRepository;
        this.officerRepository = officerRepository;
    }

    @Override
    public void createCompanyWithPopulatedStructure(CompanyWithPopulatedStructureRequest companySpec) {
        saveCompanyProfile(companySpec);
        saveAuthCode(companySpec);
        saveFilingHistory(companySpec);
        saveAppointmentsData(companySpec);
        saveAppointments(companySpec);
        saveOfficerAppointments(companySpec);
        saveCompanyMetrics(companySpec);
        saveCompanyPscStatements(companySpec);
        saveCompanyPscs(companySpec);
        saveCompanyRegisters(companySpec);
        saveDisqualifications(companySpec);
    }

    private void saveCompanyProfile(CompanyWithPopulatedStructureRequest companySpec) {
        var companyProfile = companySpec.getCompanyProfile();
        companyProfileRepository.save(companyProfile);
    }

    private void saveAuthCode(CompanyWithPopulatedStructureRequest companySpec) {
        var companyAuthCode = companySpec.getCompanyAuthCode();
        if (companyAuthCode != null) {
            authCodeRepository.save(companyAuthCode);
        }
    }

    private void saveFilingHistory(CompanyWithPopulatedStructureRequest companySpec) {
        if (companySpec.getFilingHistory() != null) {
            filingHistoryRepository.save(companySpec.getFilingHistory());
        }
    }

    private void saveAppointmentsData(CompanyWithPopulatedStructureRequest companySpec) {
        var appointmentsDataContainer = companySpec.getAppointmentsData();
        if (appointmentsDataContainer != null
                && appointmentsDataContainer.getAppointmentsData() != null) {
            for (AppointmentsData data : appointmentsDataContainer.getAppointmentsData()) {
                appointmentsDataRepository.save(data);
            }
        }
    }

    private void saveAppointments(CompanyWithPopulatedStructureRequest companySpec) {
        var appointmentsDataContainer = companySpec.getAppointmentsData();
        if (appointmentsDataContainer != null
                && appointmentsDataContainer.getAppointment() != null) {
            for (var appointment : appointmentsDataContainer.getAppointment()) {
                appointmentRepository.save(appointment);
            }
        }
    }

    private void saveOfficerAppointments(CompanyWithPopulatedStructureRequest companySpec) {
        var appointmentsDataContainer = companySpec.getAppointmentsData();
        if (appointmentsDataContainer != null
                && appointmentsDataContainer.getOfficerAppointment() != null) {
            for (var officerAppointment : appointmentsDataContainer.getOfficerAppointment()) {
                officerRepository.save(officerAppointment);
            }
        }
    }

    private void saveCompanyMetrics(CompanyWithPopulatedStructureRequest companySpec) {
        if (companySpec.getCompanyMetrics() != null) {
            companyMetricsRepository.save(companySpec.getCompanyMetrics());
        }
    }

    private void saveCompanyPscStatements(CompanyWithPopulatedStructureRequest companySpec) {
        var companyPscStatements = companySpec.getCompanyPscStatement();
        if (companyPscStatements != null) {
            for (CompanyPscStatement companyPscStatement : companyPscStatements) {
                companyPscStatementRepository.save(companyPscStatement);
            }
        }
    }

    private void saveCompanyPscs(CompanyWithPopulatedStructureRequest companySpec) {
        var companyPscsList = companySpec.getCompanyPscs();
        if (companyPscsList != null) {
            for (CompanyPscs companyPscs : companyPscsList) {
                companyPscsRepository.save(companyPscs);
            }
        }
    }

    private void saveCompanyRegisters(CompanyWithPopulatedStructureRequest companySpec) {
        if (companySpec.getCompanyRegisters() != null) {
            companyRegistersRepository.save(companySpec.getCompanyRegisters());
        }
    }

    private void saveDisqualifications(CompanyWithPopulatedStructureRequest companySpec) {
        if (companySpec.getDisqualifications() != null) {
            disqualificationsRepository.save(companySpec.getDisqualifications());
        }
    }
}
