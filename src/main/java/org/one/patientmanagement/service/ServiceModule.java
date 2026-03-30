package org.one.patientmanagement.service;

import com.google.inject.AbstractModule;

public class ServiceModule extends AbstractModule {
    
    @Override
    protected void configure() {
        bind(AccountManager.class);
        bind(AppointmentManager.class);
        bind(ConsultationService.class);
        bind(DoctorManager.class);
        bind(PatientManager.class);
    }
}
