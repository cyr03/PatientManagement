package org.one.patientmanagement.service.impl;

import com.google.inject.Inject;
import java.util.List;

import org.one.patientmanagement.domain.enums.AppointmentBlock;
import org.one.patientmanagement.domain.enums.AppointmentStatus;
import org.one.patientmanagement.domain.models.Appointment;
import org.one.patientmanagement.repository.AppointmentRepository;
import org.one.patientmanagement.service.AppointmentManager;

public class AppointmentManagerImpl implements AppointmentManager {

	private final AppointmentRepository repo;

	@Inject
	public AppointmentManagerImpl(AppointmentRepository repo) {
		this.repo = repo;
	}

	@Override
	public Appointment schedule(Appointment appointment) {
		try {
			// Business Logic: Check availability based on the UI's Morning/Afternoon selection
			if (!isDoctorAvailable(appointment.doctorId(), appointment.block())) {
				throw new IllegalStateException("Doctor is fully booked for the " + appointment.block() + " block.");
			}
			return repo.save(appointment);
		} catch (Exception e) {
			System.err.println("[AppointmentManager] Error scheduling: " + e.getMessage());
			throw e;
		}
	}

	@Override
	public List<Appointment> getAppointments() {
		return repo.findAll();
	}

	@Override
	public List<Appointment> getAppointments(long patientId, long doctorId, AppointmentStatus... status)
			throws IllegalArgumentException {
		if (patientId <= 0 && doctorId <= 0) {
			throw new IllegalArgumentException("Must provide at least a Patient or Doctor ID.");
		}
		return repo.findAll(patientId, doctorId, status);
	}

	@Override
	public Appointment update(Appointment appointment) {
		// Updates status when Doctor clicks 'With Doctor' or 'Done' in Doctor's View
		return repo.save(appointment);
	}

	@Override
	public void delete(long appointmentId) {
		repo.delete(appointmentId);
	}

	@Override
	public boolean isDoctorAvailable(long doctorId, AppointmentBlock block) {
		// Logic: If there is an existing appointment in the same block not marked 'DONE', doctor is busy
		List<Appointment> apps = repo.findByDoctor(doctorId);
		return apps.stream()
				.filter(a -> a.block() == block)
				.noneMatch(a -> a.status() != AppointmentStatus.DONE);
	}
}