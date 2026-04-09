package org.one.patientmanagement.repository.impl;

import org.one.patientmanagement.domain.models.Prescription;
import org.one.patientmanagement.repository.PrescriptionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:clinic.db");
    }

    @Override
    public Prescription save(Prescription prescription) {
        String sql = """
            INSERT INTO prescriptions (
                medication_name,
                dosage,
                frequency,
                duration,
                instructions,
                doctor_id,
                patient_id
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, prescription.medicationName());
            stmt.setString(2, prescription.dosage());
            stmt.setString(3, prescription.frequency());
            stmt.setString(4, prescription.duration());
            stmt.setString(5, prescription.instructions());
            stmt.setLong(6, prescription.doctorId());
            stmt.setLong(7, prescription.patientId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                long id = rs.getLong(1);

                return new Prescription(
                        id,
                        prescription.medicationName(),
                        prescription.dosage(),
                        prescription.frequency(),
                        prescription.duration(),
                        prescription.instructions(),
                        prescription.doctorId(),
                        prescription.patientId(),
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
        String sql = "DELETE FROM prescriptions WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Prescription> findByPatient(long patientId) {
        String sql = """
            SELECT * FROM prescriptions
            WHERE patient_id = ?
            ORDER BY created_at DESC
            LIMIT 1
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Prescription> findAllByPatient(long patientId) {
        String sql = "SELECT * FROM prescriptions WHERE patient_id = ?";
        List<Prescription> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    private Prescription mapRow(ResultSet rs) throws SQLException {
        return new Prescription(
                rs.getLong("id"),
                rs.getString("medication_name"),
                rs.getString("dosage"),
                rs.getString("frequency"),
                rs.getString("duration"),
                rs.getString("instructions"),
                rs.getLong("doctor_id"),
                rs.getLong("patient_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}