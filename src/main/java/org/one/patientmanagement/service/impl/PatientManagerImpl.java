package org.one.patientmanagement.service.impl;

import org.one.patientmanagement.repository.AttachmentRepository;
import org.one.patientmanagement.repository.VitalsRepository;
import org.one.patientmanagement.repository.PrescriptionRepository;


import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;

import org.one.patientmanagement.domain.models.Attachment;
import org.one.patientmanagement.domain.models.Patient;
import org.one.patientmanagement.domain.models.Prescription;
import org.one.patientmanagement.domain.models.Vitals;
import org.one.patientmanagement.repository.PatientRepository;
import org.one.patientmanagement.service.PatientManager;

public class PatientManagerImpl implements PatientManager {

    private final PatientRepository patientRepository;
    private final AttachmentRepository attachmentRepository;
    private final VitalsRepository vitalsRepository;
    private final PrescriptionRepository prescriptionRepository;


    public PatientManagerImpl(
            PatientRepository patientRepository,
            AttachmentRepository attachmentRepository,
            VitalsRepository vitalsRepository,
            PrescriptionRepository prescriptionRepository
    ) {
        this.patientRepository = patientRepository;
        this.attachmentRepository = attachmentRepository;
		this.vitalsRepository = vitalsRepository;
		this.prescriptionRepository = prescriptionRepository;
    }
    
    @Override
    public Patient create(@Nonnull Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public void remove(@Nonnull Patient patient) {
        patientRepository.delete(patient.id());
    }

    @Override
    public Patient update(@Nonnull Patient patient) {
        patientRepository.update(patient);
        return patient;
    }

    @Override
    public Optional<Patient> getById(long patientId) {
        return patientRepository.findById(patientId);
    }

    @Override
    public List<Patient> getPatients() {
        return patientRepository.findAll();
    }

    @Override
    public Optional<Patient> getByAccountId(long accountId) {
    	return patientRepository.findByAccountId(accountId);
    }

    @Override
    public List<Attachment> getAttachments(@Nonnull Patient patient) {
        return attachmentRepository.findAllByPatient(patient.id());
    }

    @Override
    public Optional<Vitals> getVitals(@Nonnull Patient patient) {
        return Optional.of(vitalsRepository.findByPatient(patient.id()));
    }
    
    @Override
    public List<Prescription> getPrescriptions(@Nonnull Patient patient) {
        return prescriptionRepository.findAllByPatient(patient.id());
    }

    @Override
    public Prescription recordPrescription(@Nonnull Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }

    @Override
    public void removePrescription(@Nonnull Prescription prescription) {
        prescriptionRepository.delete(prescription.id());
    }
    
    @Override
    public Vitals setVitals(@Nonnull Vitals vitals) {
        return vitalsRepository.save(vitals);
    }

    @Override
    public Attachment addAttachment(@Nonnull Attachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Override
    public void deleteAttachment(@Nonnull Attachment attachment) {
        attachmentRepository.delete(attachment.id());
    }
}