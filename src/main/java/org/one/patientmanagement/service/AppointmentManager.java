package org.one.patientmanagement.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.enums.AppointmentBlock;
import org.one.patientmanagement.domain.enums.AppointmentStatus;
import org.one.patientmanagement.domain.models.Appointment;

public interface AppointmentManager {

    Appointment schedule(@Nonnull Appointment appointment);

//    Optional<Appointment> getById(long id);

    List<Appointment> getAppointments();

    /**
     *
     * @param patientId can be null
     * @param doctorId can be null
     * @param status list of appointment status
     * @return list of consultations
     * @throws IllegalArgumentException if both {@code patientId} and {@code doctorId} are {@code null}
     */
    List<Appointment> getAppointments(long patientId, long doctorId, AppointmentStatus... status) throws IllegalArgumentException;

    Appointment update(@Nonnull Appointment appointment);

    void delete(long appointmentId);

    boolean isDoctorAvailable(long doctorId, @Nonnull AppointmentBlock block);
}
