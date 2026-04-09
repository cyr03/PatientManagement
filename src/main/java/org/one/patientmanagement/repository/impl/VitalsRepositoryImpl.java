package org.one.patientmanagement.repository.impl;

import org.one.patientmanagement.domain.models.Vitals;
import org.one.patientmanagement.repository.VitalsRepository;

import java.sql.*;

public class VitalsRepositoryImpl implements VitalsRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:clinic.db");
    }

    @Override
    public Vitals save(Vitals vitals) {
        String sql = """
            INSERT INTO vitals (
                systolic_bp,
                diastolic_bp,
                heart_rate,
                temperature,
                weight,
                height,
                patient_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setObject(1, vitals.systolicBp());
            stmt.setObject(2, vitals.diastolicBp());
            stmt.setObject(3, vitals.heartRate());
            stmt.setObject(4, vitals.temperature());
            stmt.setObject(5, vitals.weight());
            stmt.setObject(6, vitals.height());
            stmt.setLong(7, vitals.patientId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                long id = rs.getLong(1);

                return new Vitals(
                        id,
                        vitals.systolicBp(),
                        vitals.diastolicBp(),
                        vitals.heartRate(),
                        vitals.temperature(),
                        vitals.weight(),
                        vitals.height(),
                        vitals.patientId(),
                        java.time.LocalDateTime.now()
                );
            }

            throw new RuntimeException("No generated key returned");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM vitals WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Vitals findByPatient(long patientId) {
        String sql = """
            SELECT * FROM vitals
            WHERE patient_id = ?
            ORDER BY recorded_at DESC
            LIMIT 1
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapRow(rs);
            }

            throw new RuntimeException("No vitals found for patient: " + patientId);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Vitals mapRow(ResultSet rs) throws SQLException {
        return new Vitals(
                rs.getLong("id"),
                (Integer) rs.getObject("systolic_bp"),
                (Integer) rs.getObject("diastolic_bp"),
                (Integer) rs.getObject("heart_rate"),
                (Double) rs.getObject("temperature"),
                (Double) rs.getObject("weight"),
                (Double) rs.getObject("height"),
                rs.getLong("patient_id"),
                rs.getTimestamp("recorded_at").toLocalDateTime()
        );
    }
}