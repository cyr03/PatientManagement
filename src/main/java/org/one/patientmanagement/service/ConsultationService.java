package org.one.patientmanagement.service;

import java.util.List;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.enums.ConsultationType;
import org.one.patientmanagement.domain.models.Consultation;

public interface ConsultationService {
    
    Consultation create(Consultation consultation);
    
    Consultation remove(Consultation consultation);
    
    Consultation update(Consultation consultation);
    
    List<Consultation> getConsultations(long patientId);
    
    /**
     *
     * @param patientId can be null
     * @param doctorId can be null
     * @param types list of consultation types
     * @return list of consultations
     * @throws IllegalArgumentException if both {@code patientId} and {@code doctorId} are {@code null}
     */
    List<Consultation> getConsultations(long patientId, long doctorId, @Nonnull ConsultationType... types) throws IllegalArgumentException;
}
