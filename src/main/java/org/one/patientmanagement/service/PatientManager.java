package org.one.patientmanagement.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.models.Attachment;
import org.one.patientmanagement.domain.models.Doctor;
import org.one.patientmanagement.domain.models.Patient;
import org.one.patientmanagement.domain.models.Prescription;
import org.one.patientmanagement.domain.models.Vitals;

public interface PatientManager {
    
    Patient create(@Nonnull Patient patient);
    
    void remove(@Nonnull Patient patient);
    
    Patient update(@Nonnull Patient patient);
    
    List<Attachment> getAttachments(@Nonnull Patient patient);
    
    Optional<Vitals> getVitals(@Nonnull Patient patient);
    
    List<Prescription> getPrescriptions(@Nonnull Patient patient);
    
    Optional<Patient> getById(long patientId);
    
    Optional<Patient> getByAccountId(long accountId);

    List<Patient> getPatients();
    
    List<Doctor> getDoctors(long patientId);
    
    Prescription recordPrescription(@Nonnull Prescription prescription);
    
    void removePrescription(@Nonnull Prescription prescription);
    
    Vitals setVitals(@Nonnull Vitals vitals);
    
    Attachment addAttachment(@Nonnull Attachment attachment);
    
    void deleteAttachment(@Nonnull Attachment attachment);
}
