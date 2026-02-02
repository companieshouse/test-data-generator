package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.AppointmentsData;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyWithPopulatedStructureSpec;
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

    @Autowired
    private CompanyProfileRepository companyProfileRepository;

    @Autowired
    private CompanyAuthCodeRepository authCodeRepository;

    @Autowired
    FilingHistoryRepository filingHistoryRepository;

    @Autowired
    private AppointmentsDataRepository appointmentsDataRepository;

    @Autowired
    private AppointmentsRepository appointmentRepository;

    @Autowired
    private CompanyMetricsRepository companyMetricsRepository;

    @Autowired
    private CompanyPscStatementRepository companyPscStatementRepository;

    @Autowired
    private CompanyPscsRepository companyPscsRepository;

    @Autowired
    private CompanyRegistersRepository companyRegistersRepository;

    @Autowired
    private DisqualificationsRepository disqualificationsRepository;

    @Autowired
    private OfficerRepository officerRepository;

    @Override
    public void createCombinedCompany(CompanyWithPopulatedStructureSpec companySpec) {
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

    private void saveCompanyProfile(CompanyWithPopulatedStructureSpec companySpec) {
        var companyProfile = companySpec.getCompanyProfile();
        companyProfileRepository.save(companyProfile);
    }

    private void saveAuthCode(CompanyWithPopulatedStructureSpec companySpec) {
        var companyAuthCode = companySpec.getCompanyAuthCode();
        if (companyAuthCode != null) {
            authCodeRepository.save(companyAuthCode);
        }
    }

    private void saveFilingHistory(CompanyWithPopulatedStructureSpec companySpec) {
        if (companySpec.getFilingHistory() != null) {
            filingHistoryRepository.save(companySpec.getFilingHistory());
        }
    }

    private void saveAppointmentsData(CompanyWithPopulatedStructureSpec companySpec) {
        var appointmentsDataContainer = companySpec.getAppointmentsData();
        if (appointmentsDataContainer != null
                && appointmentsDataContainer.getAppointmentsData() != null) {
            for (AppointmentsData data : appointmentsDataContainer.getAppointmentsData()) {
                appointmentsDataRepository.save(data);
            }
        }
    }

    private void saveAppointments(CompanyWithPopulatedStructureSpec companySpec) {
        var appointmentsDataContainer = companySpec.getAppointmentsData();
        if (appointmentsDataContainer != null
                && appointmentsDataContainer.getAppointment() != null) {
            for (var appointment : appointmentsDataContainer.getAppointment()) {
                appointmentRepository.save(appointment);
            }
        }
    }

    private void saveOfficerAppointments(CompanyWithPopulatedStructureSpec companySpec) {
        var appointmentsDataContainer = companySpec.getAppointmentsData();
        if (appointmentsDataContainer != null
                && appointmentsDataContainer.getOfficerAppointment() != null) {
            for (var officerAppointment : appointmentsDataContainer.getOfficerAppointment()) {
                officerRepository.save(officerAppointment);
            }
        }
    }

    private void saveCompanyMetrics(CompanyWithPopulatedStructureSpec companySpec) {
        if (companySpec.getCompanyMetrics() != null) {
            companyMetricsRepository.save(companySpec.getCompanyMetrics());
        }
    }

    private void saveCompanyPscStatements(CompanyWithPopulatedStructureSpec companySpec) {
        var companyPscStatements = companySpec.getCompanyPscStatement();
        if (companyPscStatements != null) {
            for (CompanyPscStatement companyPscStatement : companyPscStatements) {
                companyPscStatementRepository.save(companyPscStatement);
            }
        }
    }

    private void saveCompanyPscs(CompanyWithPopulatedStructureSpec companySpec) {
        var companyPscsList = companySpec.getCompanyPscs();
        if (companyPscsList != null) {
            for (CompanyPscs companyPscs : companyPscsList) {
                companyPscsRepository.save(companyPscs);
            }
        }
    }

    private void saveCompanyRegisters(CompanyWithPopulatedStructureSpec companySpec) {
        if (companySpec.getCompanyRegisters() != null) {
            companyRegistersRepository.save(companySpec.getCompanyRegisters());
        }
    }

    private void saveDisqualifications(CompanyWithPopulatedStructureSpec companySpec) {
        if (companySpec.getDisqualifications() != null) {
            disqualificationsRepository.save(companySpec.getDisqualifications());
        }
    }
}
