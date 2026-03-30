package org.one.patientmanagement.repository;

import java.util.List;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.enums.AppointmentBlock;
import org.one.patientmanagement.domain.enums.AppointmentStatus;
import org.one.patientmanagement.domain.models.Appointment;

public interface AppointmentRepository extends Repository<Appointment> {
    
    List<Appointment> findAll();
    
    /**
     *      * 
     * @param patientId can be null
     * @param doctorId can be null
     * @param status list of appointment status
     * @return list of consultations
     * @throws IllegalArgumentException if both {@code patientId} and {@code doctorId} are {@code null}
     */
    List<Appointment> findAll(long patientId, long doctorId, @Nonnull AppointmentStatus... status) throws IllegalArgumentException;
    
    List<Appointment> findByDoctor(long doctorId);

    List<Appointment> findByPatient(long patientId);

    List<Appointment> findByBlock(@Nonnull AppointmentBlock block);
}
