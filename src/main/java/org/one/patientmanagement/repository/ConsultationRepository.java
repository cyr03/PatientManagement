package org.one.patientmanagement.repository;

import java.util.List;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.enums.ConsultationType;
import org.one.patientmanagement.domain.models.Consultation;

public interface ConsultationRepository extends Repository<Consultation> {

    /**
     *
     * @param patientId can be null
     * @param doctorId can be null
     * @param type list of consultation types
     * @return list of consultations
     * @throws IllegalArgumentException if both {@code patientId} and {@code doctorId} are {@code null}
     */
    List<Consultation> findAll(long patientId, long doctorId, @Nonnull ConsultationType... type) throws IllegalArgumentException;
}
