package org.one.patientmanagement.repository.impl;

import org.one.patientmanagement.repository.ScheduleRepository;

import org.one.patientmanagement.domain.models.Schedule;

import java.sql.*;
import javax.sql.DataSource;

public class ScheduleRepositoryImpl implements ScheduleRepository {

    private final DataSource dataSource;

    public ScheduleRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Schedule save(Schedule schedule) {
        String sql = "INSERT INTO schedules (day, start, end, doctor_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, schedule.day().name());
            stmt.setString(2, schedule.start().toString());
            stmt.setString(3, schedule.end().toString());
            stmt.setLong(4, schedule.doctorId());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    return new Schedule(
                            keys.getLong(1),
                            schedule.day(),
                            schedule.start(),
                            schedule.end(),
                            schedule.doctorId()
                    );
                }
                throw new RuntimeException("Failed to retrieve generated id after saving Schedule");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save Schedule", e);
        }
    }

    @Override
    public void update(Schedule schedule) {
        String sql = "UPDATE schedules SET day = ?, start = ?, end = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, schedule.day().name());
            stmt.setString(2, schedule.start().toString());
            stmt.setString(3, schedule.end().toString());
            stmt.setLong(4, schedule.id());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update Schedule with id=" + schedule.id(), e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM schedules WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete Schedule with id=" + id, e);
        }
    }
}
