package org.one.patientmanagement.repository.impl;

import com.google.inject.Inject;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.one.patientmanagement.domain.enums.AppointmentBlock;
import org.one.patientmanagement.domain.enums.AppointmentStatus;
import org.one.patientmanagement.domain.models.Appointment;
import org.one.patientmanagement.repository.AppointmentRepository;

public class AppointmentRepositoryImpl implements AppointmentRepository {

    private final DataSource dataSource;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Inject
    public AppointmentRepositoryImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Appointment save(Appointment model) {
        String sql = """
            INSERT INTO appointments (block, status, referred, referred_description, doctor_id, patient_id, queue_number, created_at)
		VALUES (?, ?, ?, ?, ?, ?, ?, ?)
	""";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, model.block().name());
            stmt.setString(2, model.status().name());
            stmt.setString(3, model.referred());
            stmt.setString(4, model.referredDescription());
            stmt.setLong(5, model.doctorId());
            stmt.setLong(6, model.patientId());
            stmt.setString(7, model.queueNumber());
            stmt.setString(8, model.createdAt().format(formatter));

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return new Appointment(rs.getLong(1), model.block(), model.status(), model.referred(),
                            model.referredDescription(), model.doctorId(), model.patientId(), model.queueNumber(),
                            model.createdAt());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB Error: Save failed", e);
        }
        return model;
    }

    @Override
    public void delete(long id) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement("DELETE FROM appointments WHERE id = ?")) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("DB Error: Delete failed", e);
        }
    }

    @Override
    public List<Appointment> findAll() {
        return findByQuery("SELECT * FROM appointments ORDER BY created_at ASC", null);
    }

    @Override
    public List<Appointment> findAll(long patientId, long doctorId, AppointmentStatus... status)
            throws IllegalArgumentException {
        // Used for filtering records in the Doctor's View
        StringBuilder sql = new StringBuilder("SELECT * FROM appointments WHERE 1=1");
        if (patientId > 0) {
            sql.append(" AND patient_id = ").append(patientId);
        }
        if (doctorId > 0) {
            sql.append(" AND doctor_id = ").append(doctorId);
        }
        sql.append(" ORDER BY created_at DESC");

        return findByQuery(sql.toString(), null);
    }

    @Override
    public List<Appointment> findByDoctor(long doctorId) {
        return findByQuery("SELECT * FROM appointments WHERE doctor_id = ? ORDER BY queue_number ASC", doctorId);
    }

    @Override
    public List<Appointment> findByPatient(long patientId) {
        return findByQuery("SELECT * FROM appointments WHERE patient_id = ? ORDER BY created_at DESC", patientId);
    }

    @Override
    public List<Appointment> findByBlock(AppointmentBlock block) {
        return findByQuery("SELECT * FROM appointments WHERE block = ?", block.name());
    }

    private List<Appointment> findByQuery(String sql, Object param) {
        List<Appointment> list = new ArrayList<>();
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (param != null) {
                stmt.setObject(1, param);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Appointment(
                            rs.getLong("id"),
                            AppointmentBlock.valueOf(rs.getString("block")),
                            AppointmentStatus.valueOf(rs.getString("status")),
                            rs.getString("referred"),
                            rs.getString("referred_description"),
                            rs.getLong("doctor_id"),
                            rs.getLong("patient_id"),
                            rs.getString("queue_number"),
                            LocalDateTime.parse(rs.getString("created_at"), formatter)
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("DB Error: Retrieval failed", e);
        }
        return list;
    }
}
