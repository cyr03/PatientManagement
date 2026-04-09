package org.one.patientmanagement.repository.impl;

import org.one.patientmanagement.domain.models.Attachment;
import org.one.patientmanagement.repository.AttachmentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AttachmentRepositoryImpl implements AttachmentRepository {

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:clinic.db");
    }

    @Override
    public Attachment save(Attachment attachment) {
        String sql = """
            INSERT INTO attachments (data, name, patient_id)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setBytes(1, attachment.data());
            stmt.setString(2, attachment.name());
            stmt.setLong(3, attachment.patientId());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                long id = rs.getLong(1);

                return new Attachment(
                        id,
                        attachment.data(),
                        attachment.name(),
                        null,
                        attachment.patientId(),
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
        String sql = "DELETE FROM attachments WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Optional<Attachment> findByPatient(long patientId) {
        String sql = "SELECT * FROM attachments WHERE patient_id = ? LIMIT 1";

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
    public List<Attachment> findAllByPatient(long patientId) {
        String sql = "SELECT * FROM attachments WHERE patient_id = ?";
        List<Attachment> list = new ArrayList<>();

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
    
    private Attachment mapRow(ResultSet rs) throws SQLException {
        return new Attachment(
                rs.getLong("id"),
                rs.getBytes("data"),
                rs.getString("name"),
                null,
                rs.getLong("patient_id"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}