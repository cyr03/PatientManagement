package org.one.patientmanagement.service;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import org.one.patientmanagement.service.impl.AppointmentManagerImpl;
import org.one.patientmanagement.service.impl.ConsultationServiceImpl;
import org.one.patientmanagement.service.impl.PatientManagerImpl;

public class ServiceModule extends AbstractModule {
    
    @Override
    protected void configure() {
//        bind(AccountManager.class);
        bind(AppointmentManager.class).to(AppointmentManagerImpl.class).in(Singleton.class);
        bind(ConsultationService.class).to(ConsultationServiceImpl.class).in(Singleton.class);
//        bind(DoctorManager.class);
        bind(PatientManager.class).to(PatientManagerImpl.class).in(Singleton.class);
    }
}
