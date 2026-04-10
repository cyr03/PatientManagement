package org.one.patientmanagement.storage;

import com.google.inject.Inject;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;

public class DatabaseInitializer {

    private final DataSource dataSource;

    @Inject
    public DatabaseInitializer(DataSource ds) {
        this.dataSource = ds;
    }

    public void init() {
        try (var conn = dataSource.getConnection(); var stmt = conn.createStatement()) {
            createAccountsTable(stmt);
            createPatientsTable(stmt);
            createDoctorsTable(stmt);
            createAppointmentsTable(stmt);
            createAttachmentsTable(stmt);
            createConsultationsTable(stmt);
            createPrescriptionsTable(stmt);
            createVitalsTable(stmt);
            createSchedulesTable(stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createAccountsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('DOCTOR', 'PATIENT')),
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """);
    }

    private void createPatientsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS patients (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id INTEGER UNIQUE,
                name TEXT NOT NULL,
                sex TEXT,
                birthday TEXT,
                blood_type TEXT,
                contact_number TEXT,
                email TEXT,
                address TEXT,
                FOREIGN KEY (account_id) REFERENCES accounts(id)
            );
        """);
    }

    private void createDoctorsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS doctors (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id INTEGER UNIQUE,
                name TEXT NOT NULL,
                profession TEXT NOT NULL,
                FOREIGN KEY (account_id) REFERENCES accounts(id)
            );
        """);
    }

    private void createAppointmentsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS appointments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                block TEXT NOT NULL CHECK(block IN ('MORNING', 'AFTERNOON')),
                status TEXT CHECK(status IN ('WITH_DOCTOR', 'WAITING', 'DONE')),
                referred TEXT,
                referred_description TEXT,
                doctor_id INTEGER NOT NULL,
                patient_id INTEGER NOT NULL,
                queue_number TEXT NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id),
                FOREIGN KEY (patient_id) REFERENCES patients(id)
            );
        """);
    }

    private void createAttachmentsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS attachments (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                data BLOB,
                name TEXT NOT NULL,
                doctor_id INTEGER,
                patient_id INTEGER,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id),
                FOREIGN KEY (patient_id) REFERENCES patients(id)
            );
        """);
    }

    private void createConsultationsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS consultations (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT CHECK(type IN ('GENERAL', 'DIAGNOSIS')),
                title TEXT,
                description TEXT,
                doctor_id INTEGER NOT NULL,
                patient_id INTEGER NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id),
                FOREIGN KEY (patient_id) REFERENCES patients(id)
            );
        """);
    }

    private void createPrescriptionsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS prescriptions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                medication_name TEXT NOT NULL,
                dosage TEXT NOT NULL,
                frequency TEXT NOT NULL,
                duration TEXT,
                instructions TEXT,
                doctor_id INTEGER NOT NULL,
                patient_id INTEGER NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id),
                FOREIGN KEY (patient_id) REFERENCES patients(id)
            );
        """);
    }

    private void createVitalsTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS vitals (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                systolic_bp INTEGER,
                diastolic_bp INTEGER,
                heart_rate INTEGER,
                temperature REAL,
                weight REAL,
                height REAL,
                patient_id INTEGER NOT NULL,
                recorded_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (patient_id) REFERENCES patients(id)
            );
        """);
    }
    
    private void createSchedulesTable(Statement stmt) throws SQLException {
        stmt.execute("""
            CREATE TABLE IF NOT EXISTS schedules (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                day TEXT NOT NULL CHECK(day IN (
                    'MONDAY','TUESDAY','WEDNESDAY',
                    'THURSDAY','FRIDAY','SATURDAY','SUNDAY'
                )),
                start TEXT NOT NULL,
                end TEXT NOT NULL,
                doctor_id INTEGER NOT NULL,
                FOREIGN KEY (doctor_id) REFERENCES doctors(id)
            );
        """);
    }
}
