package respository;

import org.one.patientmanagement.domain.enums.AppointmentBlock;
import org.one.patientmanagement.domain.enums.AppointmentStatus;
import org.one.patientmanagement.domain.enums.ConsultationType;
import org.one.patientmanagement.domain.enums.Role;
import org.one.patientmanagement.domain.models.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.one.patientmanagement.repository.AccountRepository;
import org.one.patientmanagement.repository.AppointmentRepository;
import org.one.patientmanagement.repository.AttachmentRepository;
import org.one.patientmanagement.repository.ConsultationRepository;
import org.one.patientmanagement.repository.PatientRepository;
import org.one.patientmanagement.repository.PrescriptionRepository;
import org.one.patientmanagement.repository.VitalsRepository;

/**
 * Contract tests for all repository interfaces.
 *
 * HOW TO USE (for members implementing the repositories):
 *
 *   Each nested abstract class defines the contract that your implementation
 *   MUST satisfy.  To plug in your concrete class, create a subclass in your
 *   test source set and override {@code repository()} (and any setUp helper
 *   if you need to seed data differently).
 *
 *   Example:
 * <pre>
 *   class MyPatientRepositoryImplTest
 *           extends RepositoryContractTest.PatientRepositoryContract {
 *
 *       \@Override
 *       protected PatientRepository repository() {
 *           return new MyPatientRepositoryImpl(dataSource);
 *       }
 *   }
 * </pre>
 *
 *   All tests in the parent class run automatically for your implementation.
 */
public class RepositoryContractTest {

    // -----------------------------------------------------------------------
    // Shared test-data factories
    // -----------------------------------------------------------------------

    protected static Patient makePatient(long accountId) {
        return new Patient(0L, accountId, "Test Patient", "Male",
                LocalDate.of(1995, 6, 15), "O+",
                "+63917" + accountId + "00000", null, "Manila");
    }

    protected static Account makeAccount(String user) {
        return new Account(0L, user, "hashed-pw", Role.PATIENT, LocalDateTime.now());
    }

    protected static Doctor makeDoctor(long accountId) {
        return new Doctor(0L, accountId, "Physician", "Dr. Test " + accountId);
    }

    protected static Appointment makeAppointment(long doctorId, long patientId) {
        return new Appointment(0L, AppointmentBlock.MORNING, AppointmentStatus.WAITING,
                null, null, doctorId, patientId, "M-001", LocalDateTime.now());
    }

    protected static Consultation makeConsultation(long doctorId, long patientId) {
        return new Consultation(0L, ConsultationType.GENERAL,
                "Test Consult", "Description", doctorId, patientId, LocalDateTime.now());
    }

    protected static Prescription makePrescription(long doctorId, long patientId) {
        return new Prescription(0L, "Paracetamol", "500mg", "Twice daily",
                Period.ofDays(3), "After meals", doctorId, patientId, LocalDateTime.now());
    }

    protected static Vitals makeVitals(long patientId) {
        return new Vitals(0L, 120, 80, 72, 36.6, 65.0, 168.0, patientId, LocalDateTime.now());
    }

    protected static Attachment makeAttachment(long patientId) {
        return new Attachment(0L, new byte[]{1, 2, 3}, "record.pdf",
                null, patientId, LocalDateTime.now());
    }

    // =======================================================================
    // PatientRepository contract
    // =======================================================================

    @DisplayName("PatientRepository contract")
    public abstract static class PatientRepositoryContract {

        protected abstract PatientRepository repository();

        @Test
        @DisplayName("save() persists a patient and returns it with a generated id")
        void saveReturnsPersistedPatientWithId() {
            Patient saved = repository().save(makePatient(10L));
            assertThat(saved).isNotNull();
            assertThat(saved.id()).isPositive();
            assertThat(saved.name()).isEqualTo("Test Patient");
        }

        @Test
        @DisplayName("findById() returns the saved patient")
        void findByIdReturnsPatient() {
            Patient saved = repository().save(makePatient(11L));
            Optional<Patient> found = repository().findById(saved.id());
            assertThat(found).isPresent();
            assertThat(found.get().id()).isEqualTo(saved.id());
        }

        @Test
        @DisplayName("findById() returns empty for non-existent id")
        void findByIdReturnsEmptyForUnknownId() {
            Optional<Patient> found = repository().findById(Long.MAX_VALUE);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("findAll() returns all saved patients")
        void findAllReturnsSavedPatients() {
            repository().save(makePatient(20L));
            repository().save(makePatient(21L));
            List<Patient> all = repository().findAll();
            assertThat(all).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("update() persists changed fields")
        void updatePersistsChangedFields() {
            Patient saved = repository().save(makePatient(30L));
            Patient updated = new Patient(saved.id(), saved.accountId(),
                    "Updated Name", saved.sex(), saved.birthday(), saved.bloodType(),
                    saved.contactNumber(), saved.email(), "New Address");
            repository().update(updated);
            Optional<Patient> fetched = repository().findById(saved.id());
            assertThat(fetched).isPresent();
            assertThat(fetched.get().name()).isEqualTo("Updated Name");
        }

        @Test
        @DisplayName("delete() removes the patient from the store")
        void deleteRemovesPatient() {
            Patient saved = repository().save(makePatient(40L));
            repository().delete(saved.id());
            Optional<Patient> found = repository().findById(saved.id());
            assertThat(found).isEmpty();
        }
    }

    // =======================================================================
    // AccountRepository contract
    // =======================================================================

    @DisplayName("AccountRepository contract")
    public abstract static class AccountRepositoryContract {

        protected abstract AccountRepository repository();

        private AccountRepository repo;

        @BeforeEach
        void setUp() {
            repo = repository();
        }

        @Test
        @DisplayName("save() returns account with generated id")
        void saveReturnsAccountWithId() {
            Account saved = repository().save(makeAccount("09181234567"));
            assertThat(saved.id()).isPositive();
        }

        @Test
        @DisplayName("findById() returns saved account")
        void findByIdReturnsAccount() {
            Account saved = repository().save(makeAccount("test@mail.com"));
            Optional<Account> found = repository().findById(saved.id());
            assertThat(found).isPresent();
            assertThat(found.get().user()).isEqualTo("test@mail.com");
        }

        @Test
        @DisplayName("findById() returns empty when id does not exist")
        void findByIdReturnsEmptyWhenNotFound() {
            assertThat(repository().findById(Long.MAX_VALUE)).isEmpty();
        }

        @Test
        @DisplayName("delete() removes account from store")
        void deleteRemovesAccount() {
            Account saved = repository().save(makeAccount("todelete@mail.com"));
            repository().delete(saved.id());
            assertThat(repository().findById(saved.id())).isEmpty();
        }
    }

    // =======================================================================
    // AppointmentRepository contract
    // =======================================================================

    @DisplayName("AppointmentRepository contract")
    public abstract static class AppointmentRepositoryContract {

        protected abstract AppointmentRepository repository();

        @Test
        @DisplayName("save() persists appointment with generated id")
        void saveReturnsSavedAppointment() {
            Appointment saved = repository().save(makeAppointment(1L, 2L));
            assertThat(saved.id()).isPositive();
            assertThat(saved.block()).isEqualTo(AppointmentBlock.MORNING);
        }

        @Test
        @DisplayName("findAll() contains persisted appointment")
        void findAllContainsSavedAppointment() {
            Appointment saved = repository().save(makeAppointment(1L, 2L));
            assertThat(repository().findAll()).extracting(Appointment::id).contains(saved.id());
        }

        @Test
        @DisplayName("findByDoctor() returns only that doctor's appointments")
        void findByDoctorReturnsDoctorAppointments() {
            repository().save(makeAppointment(10L, 2L));
            repository().save(makeAppointment(11L, 2L)); // different doctor
            List<Appointment> forDoctor = repository().findByDoctor(10L);
            assertThat(forDoctor).allMatch(a -> a.doctorId() == 10L);
        }

        @Test
        @DisplayName("findByPatient() returns only that patient's appointments")
        void findByPatientReturnsPatientAppointments() {
            repository().save(makeAppointment(1L, 20L));
            repository().save(makeAppointment(1L, 21L)); // different patient
            List<Appointment> forPatient = repository().findByPatient(20L);
            assertThat(forPatient).allMatch(a -> a.patientId() == 20L);
        }

        @Test
        @DisplayName("findByBlock() returns only appointments in that block")
        void findByBlockReturnsMatchingAppointments() {
            Appointment morningAppt = repository().save(makeAppointment(1L, 2L)); // MORNING by default
            List<Appointment> morning = repository().findByBlock(AppointmentBlock.MORNING);
            assertThat(morning).extracting(Appointment::id).contains(morningAppt.id());
            assertThat(morning).allMatch(a -> a.block() == AppointmentBlock.MORNING);
        }

        @Test
        @DisplayName("findAll(patientId, doctorId, status) throws when both ids are 0")
        void findAllWithFiltersThrowsWhenBothIdsAreZero() {
            assertThatThrownBy(() -> repository().findAll(0L, 0L, AppointmentStatus.WAITING))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("delete() removes the appointment")
        void deleteRemovesAppointment() {
            Appointment saved = repository().save(makeAppointment(1L, 2L));
            repository().delete(saved.id());
            assertThat(repository().findAll()).extracting(Appointment::id).doesNotContain(saved.id());
        }
    }

    // =======================================================================
    // ConsultationRepository contract
    // =======================================================================

    @DisplayName("ConsultationRepository contract")
    public abstract static class ConsultationRepositoryContract {

        protected abstract ConsultationRepository repository();

        @Test
        @DisplayName("save() persists consultation with generated id")
        void saveReturnsSavedConsultation() {
            Consultation saved = repository().save(makeConsultation(1L, 2L));
            assertThat(saved.id()).isPositive();
        }

        @Test
        @DisplayName("findAll(patientId, doctorId, types) returns consultations for patient")
        void findAllByPatientReturnsMachingConsultations() {
            repository().save(makeConsultation(1L, 30L));
            repository().save(makeConsultation(1L, 31L)); // different patient
            List<Consultation> result = repository().findAll(30L, 0L, ConsultationType.GENERAL);
            assertThat(result).allMatch(c -> c.patientId() == 30L);
        }

        @Test
        @DisplayName("findAll(patientId, doctorId, types) returns consultations for doctor")
        void findAllByDoctorReturnsMatchingConsultations() {
            repository().save(makeConsultation(50L, 2L));
            repository().save(makeConsultation(51L, 2L)); // different doctor
            List<Consultation> result = repository().findAll(0L, 50L, ConsultationType.GENERAL);
            assertThat(result).allMatch(c -> c.doctorId() == 50L);
        }

        @Test
        @DisplayName("findAll() throws IllegalArgumentException when both patientId and doctorId are 0")
        void findAllThrowsWhenBothIdsAreZero() {
            assertThatThrownBy(() -> repository().findAll(0L, 0L, ConsultationType.GENERAL))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("delete() removes the consultation")
        void deleteRemovesConsultation() {
            Consultation saved = repository().save(makeConsultation(1L, 2L));
            repository().delete(saved.id());
            // After delete, a search by patientId should not contain the deleted record
            List<Consultation> remaining = repository().findAll(2L, 0L, ConsultationType.GENERAL);
            assertThat(remaining).extracting(Consultation::id).doesNotContain(saved.id());
        }
    }

    // =======================================================================
    // PrescriptionRepository contract
    // =======================================================================

    @DisplayName("PrescriptionRepository contract")
    public abstract static class PrescriptionRepositoryContract {

        protected abstract PrescriptionRepository repository();

        @Test
        @DisplayName("save() persists prescription with generated id")
        void saveReturnsSavedPrescription() {
            Prescription saved = repository().save(makePrescription(1L, 2L));
            assertThat(saved.id()).isPositive();
            assertThat(saved.medicationName()).isEqualTo("Paracetamol");
        }

        @Test
        @DisplayName("findAllByPatient() returns all prescriptions for a patient")
        void findAllByPatientReturnsCorrectRecords() {
            repository().save(makePrescription(1L, 40L));
            repository().save(makePrescription(1L, 40L)); // second prescription same patient
            repository().save(makePrescription(1L, 41L)); // different patient
            List<Prescription> result = repository().findAllByPatient(40L);
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
            assertThat(result).allMatch(p -> p.patientId() == 40L);
        }

        @Test
        @DisplayName("findByPatient() returns a prescription for a patient")
        void findByPatientReturnsPrescription() {
            repository().save(makePrescription(1L, 50L));
            Optional<Prescription> found = repository().findByPatient(50L);
            assertThat(found).isPresent();
            assertThat(found.get().patientId()).isEqualTo(50L);
        }

        @Test
        @DisplayName("findByPatient() returns empty when patient has no prescriptions")
        void findByPatientReturnsEmptyWhenNone() {
            Optional<Prescription> found = repository().findByPatient(Long.MAX_VALUE);
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("delete() removes the prescription")
        void deleteRemovesPrescription() {
            Prescription saved = repository().save(makePrescription(1L, 60L));
            repository().delete(saved.id());
            List<Prescription> remaining = repository().findAllByPatient(60L);
            assertThat(remaining).extracting(Prescription::id).doesNotContain(saved.id());
        }
    }

    // =======================================================================
    // VitalsRepository contract
    // =======================================================================

    @DisplayName("VitalsRepository contract")
    public abstract static class VitalsRepositoryContract {

        protected abstract VitalsRepository repository();

        @Test
        @DisplayName("save() persists vitals with generated id")
        void saveReturnsSavedVitals() {
            Vitals saved = repository().save(makeVitals(2L));
            assertThat(saved.id()).isPositive();
            assertThat(saved.patientId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("findByPatient() returns the vitals for a patient")
        void findByPatientReturnsVitals() {
            repository().save(makeVitals(70L));
            Vitals found = repository().findByPatient(70L);
            assertThat(found).isNotNull();
            assertThat(found.patientId()).isEqualTo(70L);
        }

        @Test
        @DisplayName("update() persists changed vital values")
        void updatePersistsChangedVitals() {
            Vitals saved = repository().save(makeVitals(80L));
            Vitals updated = new Vitals(saved.id(), 130, 85, 80,
                    37.0, 70.0, 170.0, 80L, LocalDateTime.now());
            repository().update(updated);
            Vitals fetched = repository().findByPatient(80L);
            assertThat(fetched.systolicBp()).isEqualTo(130);
        }

        @Test
        @DisplayName("delete() removes vitals")
        void deleteRemovesVitals() {
            Vitals saved = repository().save(makeVitals(90L));
            repository().delete(saved.id());
            Vitals found = repository().findByPatient(90L);
            assertThat(found).isNull();
        }
    }

    // =======================================================================
    // AttachmentRepository contract
    // =======================================================================

    @DisplayName("AttachmentRepository contract")
    public abstract static class AttachmentRepositoryContract {

        protected abstract AttachmentRepository repository();

        @Test
        @DisplayName("save() persists attachment with generated id")
        void saveReturnsSavedAttachment() {
            Attachment saved = repository().save(makeAttachment(3L));
            assertThat(saved.id()).isPositive();
            assertThat(saved.name()).isEqualTo("record.pdf");
        }

        @Test
        @DisplayName("findAllByPatient() returns all attachments for a patient")
        void findAllByPatientReturnsCorrectRecords() {
            repository().save(makeAttachment(100L));
            repository().save(makeAttachment(100L));
            repository().save(makeAttachment(101L));
            List<Attachment> result = repository().findAllByPatient(100L);
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
            assertThat(result).allMatch(a -> a.patientId() != null && a.patientId() == 100L);
        }

        @Test
        @DisplayName("findByPatient() returns an attachment for a patient")
        void findByPatientReturnsAttachment() {
            repository().save(makeAttachment(110L));
            Optional<Attachment> found = repository().findByPatient(110L);
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("findByPatient() returns empty when patient has no attachments")
        void findByPatientReturnsEmptyWhenNone() {
            assertThat(repository().findByPatient(Long.MAX_VALUE)).isEmpty();
        }

        @Test
        @DisplayName("delete() removes the attachment")
        void deleteRemovesAttachment() {
            Attachment saved = repository().save(makeAttachment(120L));
            repository().delete(saved.id());
            List<Attachment> remaining = repository().findAllByPatient(120L);
            assertThat(remaining).extracting(Attachment::id).doesNotContain(saved.id());
        }
    }
}
