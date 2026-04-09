package org.one.patientmanagement.service.impl;

import org.one.patientmanagement.domain.enums.ConsultationType;
import org.one.patientmanagement.domain.models.Consultation;
import org.one.patientmanagement.repository.ConsultationRepository;
import org.one.patientmanagement.service.ConsultationService;

import java.util.List;

public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;

    public ConsultationServiceImpl(ConsultationRepository consultationRepository) {
        this.consultationRepository = consultationRepository;
    }

    @Override
    public Consultation create(Consultation consultation) {
        return consultationRepository.save(consultation);
    }

    @Override
    public Consultation remove(Consultation consultation) {
        consultationRepository.delete(consultation.id());
        return consultation;
    }

    @Override
    public Consultation update(Consultation consultation) {
        consultationRepository.update(consultation);
        return consultation;
    }
    

    @Override
    public List<Consultation> getConsultations(long patientId) {
        return consultationRepository.findAll(patientId, 0);
    }

    @Override
    public List<Consultation> getConsultations(long patientId, long doctorId, ConsultationType... types) {
        return consultationRepository.findAll(patientId, doctorId, types);
    }
}