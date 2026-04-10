package org.one.patientmanagement.service;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.one.patientmanagement.domain.models.Doctor;
import org.one.patientmanagement.domain.models.Schedule;

public interface DoctorManager {

    Optional<Doctor> getDoctorById(long doctorId);

    Optional<Doctor> getDoctorByAccountId(long accountId);
    
    List<Schedule> getSchedules(long doctorId);
    
    Schedule addSchedule(@Nonnull Schedule schedule);
    
    Schedule removeSchedule(@Nonnull Schedule schedule);
    
    Schedule update(@Nonnull Schedule schedule);
}
