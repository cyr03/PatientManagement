package org.one.patientmanagement.storage;

import com.google.inject.Inject;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    private final Connection connection;

    @Inject
    public DatabaseInitializer(Connection c) {
        this.connection = c;
    }
    
    public void init() {
        // TODO INCLUDE DOCTOR SCHEDULES TABLE AND LINK IT TO DOCTORS
        createAccountsTable();
        createPatientsTable();
        createDoctorsTable();
        createAppointmentsTable();
        createAttachmentsTable();
        createConsultationsTable();
        createPrescriptionsTable();
        createVitalsTable();
    }

    private void createAccountsTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS accounts (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                role TEXT NOT NULL CHECK(role IN ('DOCTOR', 'PATIENT')),
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            );
        """);
    }

    private void createPatientsTable() {
        execute("""
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

    private void createDoctorsTable() {
        execute("""
            CREATE TABLE IF NOT EXISTS doctors (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                account_id INTEGER UNIQUE,
                name TEXT NOT NULL,
                FOREIGN KEY (account_id) REFERENCES accounts(id)
            );
        """);
    }

    private void createAppointmentsTable() {
        execute("""
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

    private void createAttachmentsTable() {
        execute("""
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

    private void createConsultationsTable() {
        execute("""
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

    private void createPrescriptionsTable() {
        execute("""
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

    private void createVitalsTable() {
        execute("""
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

    private void execute(String sql) {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}