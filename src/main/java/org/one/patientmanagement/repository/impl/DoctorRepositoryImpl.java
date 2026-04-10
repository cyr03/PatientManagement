package org.one.patientmanagement.repository.impl;

import org.one.patientmanagement.repository.DoctorRepository;

import org.one.patientmanagement.domain.models.Doctor;
import org.one.patientmanagement.domain.models.Schedule;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;

public class DoctorRepositoryImpl implements DoctorRepository {

    private final DataSource dataSource;

    public DoctorRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Doctor save(Doctor doctor) {
        String sql = "INSERT INTO doctors (account_id, name, profession) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, doctor.accountId());
            stmt.setString(2, doctor.name());
            stmt.setString(3, doctor.profession());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Doctor(keys.getLong(1), doctor.accountId(), doctor.profession(), doctor.name());
                }
                throw new RuntimeException("Failed to retrieve generated id after saving Doctor");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save Doctor", e);
        }
    }

    @Override
    public void update(Doctor doctor) {
        String sql = "UPDATE doctors SET name = ?, profession = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, doctor.name());
            stmt.setString(2, doctor.profession());
            stmt.setLong(3, doctor.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Doctor with id=" + doctor.id(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM doctors WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Doctor with id=" + id, e);
        }
    }

    @Override
    public Optional<Doctor> findById(long id) {
        String sql = "SELECT id, account_id, name, profession FROM doctors WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapDoctor(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Doctor with id=" + id, e);
        }
    }

    @Override
    public Optional<Doctor> findByAccountId(long accountId) {
        String sql = "SELECT id, account_id, name, profession FROM doctors WHERE account_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapDoctor(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find Doctor with accountId=" + accountId, e);
        }
    }

    @Override
    public List<Schedule> findSchedules(long doctorId) {
        String sql = "SELECT id, day, start, end, doctor_id FROM schedules WHERE doctor_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, doctorId);
            try (ResultSet rs = stmt.executeQuery()) {
                List<Schedule> schedules = new ArrayList<>();
                while (rs.next()) {
                    schedules.add(mapSchedule(rs));
                }
                return schedules;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find schedules for doctorId=" + doctorId, e);
        }
    }

    private Doctor mapDoctor(ResultSet rs) throws SQLException {
        return new Doctor(
                rs.getLong("id"),
                rs.getLong("account_id"),
                rs.getString("profession"),
                rs.getString("name")
        );
    }

    private Schedule mapSchedule(ResultSet rs) throws SQLException {
        return new Schedule(
                rs.getLong("id"),
                DayOfWeek.valueOf(rs.getString("day")),
                LocalTime.parse(rs.getString("start")),
                LocalTime.parse(rs.getString("end")),
                rs.getLong("doctor_id")
        );
    }
}
