package org.one.patientmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.one.patientmanagement.domain.models.Doctor;
import org.one.patientmanagement.domain.models.Schedule;

public interface DoctorRepository extends Repository<Doctor> {
    
    List<Schedule> findSchedules(long doctorId);
    
    Optional<Doctor> findByAccountId(long accountId);
    
    Optional<Doctor> findById(long id);
}
