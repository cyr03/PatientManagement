package org.one.patientmanagement.repository.impl;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.one.patientmanagement.domain.models.Patient;
import org.one.patientmanagement.repository.PatientRepository;

public class PatientRepositoryImpl implements PatientRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:clinic.db");
    }

    @Override
    public Patient save(Patient patient) {
        String sql = "INSERT INTO patients (account_id, name, sex, birthday, blood_type, contact_number, email, address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setLong(1, patient.accountId());
            stmt.setString(2, patient.name());
            stmt.setString(3, patient.sex());
            stmt.setString(4, patient.birthday().toString());
            stmt.setString(5, patient.bloodType());
            stmt.setString(6, patient.contactNumber());
            stmt.setString(7, patient.email());
            stmt.setString(8, patient.address());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                long id = rs.getLong(1);

                return new Patient(
                    id,
                    patient.accountId(),
                    patient.name(),
                    patient.sex(),
                    patient.birthday(),
                    patient.bloodType(),
                    patient.contactNumber(),
                    patient.email(),
                    patient.address()
                );
            }

            throw new RuntimeException("Failed to insert patient");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(long id) {
        String sql = "DELETE FROM patients WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Patient patient) {
        String sql = "UPDATE patients SET account_id=?, name=?, sex=?, birthday=?, blood_type=?, contact_number=?, email=?, address=? WHERE id=?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, patient.accountId());
            stmt.setString(2, patient.name());
            stmt.setString(3, patient.sex());
            stmt.setString(4, patient.birthday().toString());
            stmt.setString(5, patient.bloodType());
            stmt.setString(6, patient.contactNumber());
            stmt.setString(7, patient.email());
            stmt.setString(8, patient.address());
            stmt.setLong(9, patient.id());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Patient> findById(long id) {
        String sql = "SELECT * FROM patients WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
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
    public List<Patient> findAll() {
        String sql = "SELECT * FROM patients";
        List<Patient> list = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

            return list;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Patient mapRow(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getLong("id"),
            rs.getLong("account_id"),
            rs.getString("name"),
            rs.getString("sex"),
            java.time.LocalDate.parse(rs.getString("birthday")),
            rs.getString("blood_type"),
            rs.getString("contact_number"),
            rs.getString("email"),
            rs.getString("address")
        );
    }

    @Override
    public Optional<Patient> findByAccountId(long accountId) {
        String sql = "SELECT * FROM patients WHERE account_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, accountId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRow(rs));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}