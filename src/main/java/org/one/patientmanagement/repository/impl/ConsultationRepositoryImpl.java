package org.one.patientmanagement.repository.impl;

import org.one.patientmanagement.domain.enums.ConsultationType;
import org.one.patientmanagement.domain.models.Consultation;
import org.one.patientmanagement.repository.ConsultationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:clinic.db");
    }

    @Override
    public Consultation save(Consultation consultation) {
        String sql = """
            INSERT INTO consultations (
                type, title, description, doctor_id, patient_id
            )
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, consultation.type().name());
            stmt.setString(2, consultation.title());
            stmt.setString(3, consultation.description());
            stmt.setLong(4, consultation.doctorId());
            stmt.setLong(5, consultation.patientId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                long id = rs.getLong(1);

                return new Consultation(
                        id,
                        consultation.type(),
                        consultation.title(),
                        consultation.description(),
                        consultation.doctorId(),
                        consultation.patientId(),
                        java.time.LocalDateTime.now()
                );
            }

            throw new RuntimeException("No generated key");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public void update(Consultation consultation) {
        String sql = """
            UPDATE consultations
            SET type = ?, title = ?, description = ?, doctor_id = ?, patient_id = ?
            WHERE id = ?
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, consultation.type().name());
            stmt.setString(2, consultation.title());
            stmt.setString(3, consultation.description());
            stmt.setLong(4, consultation.doctorId());
            stmt.setLong(5, consultation.patientId());
            stmt.setLong(6, consultation.id());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM consultations WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Consultation> findAll(long patientId, long doctorId, ConsultationType... types) {

        if (patientId == 0 && doctorId == 0) {
            throw new IllegalArgumentException("patientId and doctorId cannot both be null/0");
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM consultations WHERE 1=1");

        List<Object> params = new ArrayList<>();

        if (patientId != 0) {
            sql.append(" AND patient_id = ?");
            params.add(patientId);
        }

        if (doctorId != 0) {
            sql.append(" AND doctor_id = ?");
            params.add(doctorId);
        }

        if (types != null && types.length > 0) {
            sql.append(" AND type IN (");
            for (int i = 0; i < types.length; i++) {
                sql.append("?");
                if (i < types.length - 1) sql.append(",");
                params.add(types[i].name());
            }
            sql.append(")");
        }

        List<Consultation> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private Consultation mapRow(ResultSet rs) throws SQLException {
        return new Consultation(
                rs.getLong("id"),
                ConsultationType.valueOf(rs.getString("type")),
                rs.getString("title"),
                rs.getString("description"),
                rs.getLong("doctor_id"),
                rs.getLong("patient_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}