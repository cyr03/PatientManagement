package org.one.patientmanagement.service.impl;

import com.google.inject.Inject;
import org.one.patientmanagement.domain.models.Doctor;
import org.one.patientmanagement.domain.models.Schedule;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.one.patientmanagement.repository.DoctorRepository;
import org.one.patientmanagement.repository.ScheduleRepository;
import org.one.patientmanagement.service.DoctorManager;

public class DoctorManagerImpl implements DoctorManager {

    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;

    @Inject
    public DoctorManagerImpl(DoctorRepository doctorRepository, ScheduleRepository scheduleRepository) {
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public Optional<Doctor> getDoctorById(long doctorId) {
        return doctorRepository.findById(doctorId);
    }

    @Override
    public Optional<Doctor> getDoctorByAccountId(long accountId) {
        return doctorRepository.findByAccountId(accountId);
    }

    @Override
    public List<Schedule> getSchedules(long doctorId) {
        return doctorRepository.findSchedules(doctorId);
    }

    @Override
    public Schedule addSchedule(@Nonnull Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public Schedule removeSchedule(@Nonnull Schedule schedule) {
        scheduleRepository.delete(schedule.id());
        return schedule;
    }

    @Override
    public Schedule update(@Nonnull Schedule schedule) {
        scheduleRepository.update(schedule);
        return schedule;
    }
}