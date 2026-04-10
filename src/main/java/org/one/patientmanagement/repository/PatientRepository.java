package org.one.patientmanagement.repository;

import java.util.List;
import java.util.Optional;
import org.one.patientmanagement.domain.models.Patient;

public interface PatientRepository extends Repository<Patient> {
    
    Optional<Patient> findById(long id);
    
    List<Patient> findAll();

    Optional<Patient> findByAccountId(long accountId);
}
