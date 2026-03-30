package org.one.patientmanagement.repository;

import com.google.inject.AbstractModule;

public class RepositoryModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(AccountRepository.class);
        bind(AppointmentRepository.class);
        bind(ConsultationRepository.class);
        bind(PatientRepository.class);
        bind(PrescriptionRepository.class);
        bind(VitalsRepository.class);
    }
}
