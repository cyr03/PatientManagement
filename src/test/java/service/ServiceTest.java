package service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.one.patientmanagement.domain.enums.*;
import org.one.patientmanagement.domain.models.*;
import org.one.patientmanagement.repository.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.one.patientmanagement.service.AccountManager;
import org.one.patientmanagement.service.AppointmentManager;
import org.one.patientmanagement.service.ConsultationService;
import org.one.patientmanagement.service.DoctorManager;
import org.one.patientmanagement.service.PatientManager;

/**
 * Unit tests for all service interfaces.
 *
 * HOW TO USE (for members implementing the services):
 *
 *   Each nested abstract class defines what the service implementation MUST do.
 *   Provide your concrete implementation via {@code service()} and supply
 *   all mocked repositories through the constructor / DI.
 *
 *   Example:
 * <pre>
 *   class MyPatientManagerImplTest
 *           extends ServiceTest.PatientManagerTest {
 *
 *       \@Override
 *       protected PatientManager service() {
 *           return new PatientManagerImpl(
 *               mockPatientRepo, mockAttachmentRepo,
 *               mockVitalsRepo, mockPrescriptionRepo
 *           );
 *       }
 *   }
 * </pre>
 */
public class ServiceTest {

    // -----------------------------------------------------------------------
    // Shared factory helpers
    // -----------------------------------------------------------------------

    static Patient patient(long id) {
        return new Patient(id, 10L, "Test Patient", "Male",
                LocalDate.of(1990, 1, 1), "O+",
                "+639171234567", null, "Manila");
    }

    static Account account(long id) {
        return new Account(id, "user@test.com", "hashed", Role.PATIENT, LocalDateTime.now());
    }

    static Doctor doctor(long id) {
        return new Doctor(id, 5L, "Physician", "Dr. Test");
    }

    static Appointment appointment(long id) {
        return new Appointment(id, AppointmentBlock.MORNING, AppointmentStatus.WAITING,
                null, null, 2L, 3L, "M-001", LocalDateTime.now());
    }

    static Consultation consultation(long id) {
        return new Consultation(id, ConsultationType.GENERAL,
                "Title", "Description", 2L, 3L, LocalDateTime.now());
    }

    static Prescription prescription(long id) {
        return new Prescription(id, "Paracetamol", "500mg", "Twice daily",
                Period.ofDays(3), "After meals", 2L, 3L, LocalDateTime.now());
    }

    static Vitals vitals(long patientId) {
        return new Vitals(1L, 120, 80, 72, 36.6, 65.0, 168.0, patientId, LocalDateTime.now());
    }

    static Attachment attachment(long patientId) {
        return new Attachment(1L, new byte[]{1, 2, 3}, "record.pdf",
                null, patientId, LocalDateTime.now());
    }

    // =======================================================================
    // PatientManager
    // =======================================================================

    @Nested
    @DisplayName("PatientManager")
    @ExtendWith(MockitoExtension.class)
    public abstract static class PatientManagerTest {

        @Mock protected PatientRepository patientRepo;
        @Mock protected AttachmentRepository attachmentRepo;
        @Mock protected VitalsRepository vitalsRepo;
        @Mock protected PrescriptionRepository prescriptionRepo;

        protected abstract PatientManager service();

        @Test
        @DisplayName("create() delegates to repository and returns persisted patient")
        void createDelegatesToRepository() {
            Patient input = patient(0L);
            Patient persisted = patient(1L);
            when(patientRepo.save(input)).thenReturn(persisted);

            Patient result = service().create(input);

            verify(patientRepo).save(input);
            assertThat(result.id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("remove() delegates delete to repository using patient id")
        void removeDelegatesToRepository() {
            Patient p = patient(5L);
            service().remove(p);
            verify(patientRepo).delete(5L);
        }

        @Test
        @DisplayName("update() delegates to repository and returns updated patient")
        void updateDelegatesToRepository() {
            Patient updated = patient(3L);
            when(patientRepo.save(updated)).thenReturn(updated);

            Patient result = service().update(updated);

            verify(patientRepo, atLeastOnce()).update(updated);
            // OR verify via save, depending on implementation strategy
        }

        @Test
        @DisplayName("getById() returns patient when found")
        void getByIdReturnsPresentOptional() {
            when(patientRepo.findById(1L)).thenReturn(Optional.of(patient(1L)));

            Optional<Patient> result = service().getById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(1L);
            verify(patientRepo).findById(1L);
        }

        @Test
        @DisplayName("getById() returns empty when not found")
        void getByIdReturnsEmpty() {
            when(patientRepo.findById(anyLong())).thenReturn(Optional.empty());

            Optional<Patient> result = service().getById(99L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("getPatients() returns all patients from repository")
        void getPatientsReturnsAllPatients() {
            when(patientRepo.findAll()).thenReturn(List.of(patient(1L), patient(2L)));

            List<Patient> result = service().getPatients();

            assertThat(result).hasSize(2);
            verify(patientRepo).findAll();
        }

        @Test
        @DisplayName("getAttachments() returns attachments for patient")
        void getAttachmentsReturnsPatientAttachments() {
            Patient p = patient(3L);
            when(attachmentRepo.findAllByPatient(3L))
                    .thenReturn(List.of(attachment(3L)));

            List<Attachment> result = service().getAttachments(p);

            assertThat(result).hasSize(1);
            verify(attachmentRepo).findAllByPatient(3L);
        }

        @Test
        @DisplayName("getVitals() returns optional vitals for patient")
        void getVitalsReturnsVitals() {
            Patient p = patient(4L);
            when(vitalsRepo.findByPatient(4L)).thenReturn(vitals(4L));

            Optional<Vitals> result = service().getVitals(p);

            assertThat(result).isPresent();
            assertThat(result.get().patientId()).isEqualTo(4L);
        }

        @Test
        @DisplayName("getVitals() returns empty when patient has no vitals")
        void getVitalsReturnsEmptyWhenNone() {
            Patient p = patient(5L);
            when(vitalsRepo.findByPatient(5L)).thenReturn(null);

            Optional<Vitals> result = service().getVitals(p);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("getPrescriptions() returns prescriptions for patient")
        void getPrescriptionsReturnsPrescriptions() {
            Patient p = patient(6L);
            when(prescriptionRepo.findAllByPatient(6L))
                    .thenReturn(List.of(prescription(1L)));

            List<Prescription> result = service().getPrescriptions(p);

            assertThat(result).hasSize(1);
            verify(prescriptionRepo).findAllByPatient(6L);
        }

        @Test
        @DisplayName("recordPrescription() delegates to repository")
        void recordPrescriptionDelegatesToRepository() {
            Prescription rx = prescription(0L);
            Prescription saved = prescription(1L);
            when(prescriptionRepo.save(rx)).thenReturn(saved);

            Prescription result = service().recordPrescription(rx);

            assertThat(result.id()).isEqualTo(1L);
            verify(prescriptionRepo).save(rx);
        }

        @Test
        @DisplayName("removePrescription() calls delete with prescription id")
        void removePrescriptionCallsDelete() {
            Prescription rx = prescription(7L);
            service().removePrescription(rx);
            verify(prescriptionRepo).delete(7L);
        }

        @Test
        @DisplayName("setVitals() delegates save to vitals repository")
        void setVitalsDelegatesToRepository() {
            Vitals v = vitals(8L);
            when(vitalsRepo.save(v)).thenReturn(v);

            Vitals result = service().setVitals(v);

            assertThat(result).isNotNull();
            verify(vitalsRepo).save(v);
        }

        @Test
        @DisplayName("addAttachment() delegates save to attachment repository")
        void addAttachmentDelegatesToRepository() {
            Attachment a = attachment(9L);
            when(attachmentRepo.save(a)).thenReturn(a);

            Attachment result = service().addAttachment(a);

            assertThat(result).isNotNull();
            verify(attachmentRepo).save(a);
        }

        @Test
        @DisplayName("deleteAttachment() calls delete with attachment id")
        void deleteAttachmentCallsDelete() {
            Attachment a = attachment(10L);
            service().deleteAttachment(a);
            verify(attachmentRepo).delete(a.id());
        }
    }

    // =======================================================================
    // AppointmentManager
    // =======================================================================

    @Nested
    @DisplayName("AppointmentManager")
    @ExtendWith(MockitoExtension.class)
    public abstract static class AppointmentManagerTest {

        @Mock protected AppointmentRepository appointmentRepo;

        protected abstract AppointmentManager service();

        @Test
        @DisplayName("schedule() delegates to repository and returns saved appointment")
        void scheduleDelegatesToRepository() {
            Appointment input = appointment(0L);
            Appointment saved = appointment(1L);
            when(appointmentRepo.save(input)).thenReturn(saved);

            Appointment result = service().schedule(input);

            assertThat(result.id()).isEqualTo(1L);
            verify(appointmentRepo).save(input);
        }

        @Test
        @DisplayName("getAppointments() returns all appointments")
        void getAppointmentsReturnsAll() {
            when(appointmentRepo.findAll()).thenReturn(List.of(appointment(1L), appointment(2L)));

            List<Appointment> result = service().getAppointments();

            assertThat(result).hasSize(2);
            verify(appointmentRepo).findAll();
        }

        @Test
        @DisplayName("getAppointments(patientId, doctorId, status) delegates to repository")
        void getAppointmentsWithFiltersDelegatesToRepository() {
            when(appointmentRepo.findAll(3L, 2L, AppointmentStatus.WAITING))
                    .thenReturn(List.of(appointment(1L)));

            List<Appointment> result = service().getAppointments(3L, 2L, AppointmentStatus.WAITING);

            assertThat(result).hasSize(1);
            verify(appointmentRepo).findAll(3L, 2L, AppointmentStatus.WAITING);
        }

        @Test
        @DisplayName("update() delegates to repository")
        void updateDelegatesToRepository() {
            Appointment a = appointment(1L);
            when(appointmentRepo.save(a)).thenReturn(a);

            service().update(a);

            verify(appointmentRepo, atLeastOnce()).update(a);
        }

        @Test
        @DisplayName("delete() delegates to repository")
        void deleteDelegatesToRepository() {
            service().delete(5L);
            verify(appointmentRepo).delete(5L);
        }

        @Test
        @DisplayName("isDoctorAvailable() returns false when doctor has appointment in that block")
        void isDoctorAvailableReturnsFalseWhenBooked() {
            when(appointmentRepo.findByBlock(AppointmentBlock.MORNING))
                    .thenReturn(List.of(appointment(1L))); // doctor 2 is booked in appointment(1L)

            boolean available = service().isDoctorAvailable(2L, AppointmentBlock.MORNING);

            assertThat(available).isFalse();
        }

        @Test
        @DisplayName("isDoctorAvailable() returns true when doctor has no appointment in that block")
        void isDoctorAvailableReturnsTrueWhenFree() {
            when(appointmentRepo.findByBlock(AppointmentBlock.AFTERNOON))
                    .thenReturn(List.of()); // no appointments in afternoon

            boolean available = service().isDoctorAvailable(2L, AppointmentBlock.AFTERNOON);

            assertThat(available).isTrue();
        }
    }

    // =======================================================================
    // ConsultationService
    // =======================================================================

    @Nested
    @DisplayName("ConsultationService")
    @ExtendWith(MockitoExtension.class)
    public abstract static class ConsultationServiceTest {

        @Mock protected ConsultationRepository consultationRepo;

        protected abstract ConsultationService service();

        @Test
        @DisplayName("create() delegates to repository and returns persisted consultation")
        void createDelegatesToRepository() {
            Consultation input = consultation(0L);
            Consultation saved = consultation(1L);
            when(consultationRepo.save(input)).thenReturn(saved);

            Consultation result = service().create(input);

            assertThat(result.id()).isEqualTo(1L);
            verify(consultationRepo).save(input);
        }

        @Test
        @DisplayName("remove() calls delete on repository")
        void removeDelegatesToRepository() {
            Consultation c = consultation(3L);
            service().remove(c);
            verify(consultationRepo).delete(3L);
        }

        @Test
        @DisplayName("update() persists changes to repository")
        void updateDelegatesToRepository() {
            Consultation c = consultation(4L);
            when(consultationRepo.save(c)).thenReturn(c);

            service().update(c);

            verify(consultationRepo, atLeastOnce()).update(c);
        }

        @Test
        @DisplayName("getConsultations(patientId) returns consultations for that patient")
        void getConsultationsByPatientIdDelegatesToRepository() {
            when(consultationRepo.findAll(eq(5L), anyLong(), any()))
                    .thenReturn(List.of(consultation(1L)));

            List<Consultation> result = service().getConsultations(5L);

            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("getConsultations(patientId, doctorId, types) delegates to repository")
        void getConsultationsWithFiltersDelegatesToRepository() {
            when(consultationRepo.findAll(6L, 2L, ConsultationType.DIAGNOSIS))
                    .thenReturn(List.of(consultation(2L)));

            List<Consultation> result = service().getConsultations(6L, 2L, ConsultationType.DIAGNOSIS);

            assertThat(result).hasSize(1);
            verify(consultationRepo).findAll(6L, 2L, ConsultationType.DIAGNOSIS);
        }

        @Test
        @DisplayName("getConsultations() throws when both ids are 0")
        void getConsultationsThrowsWhenBothIdsAreZero() {
            when(consultationRepo.findAll(0L, 0L, ConsultationType.GENERAL))
                    .thenThrow(new IllegalArgumentException("both ids are null"));

            assertThatThrownBy(() -> service().getConsultations(0L, 0L, ConsultationType.GENERAL))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // =======================================================================
    // AccountManager
    // =======================================================================

    @Nested
    @DisplayName("AccountManager")
    @ExtendWith(MockitoExtension.class)
    public abstract static class AccountManagerTest {

        @Mock protected AccountRepository accountRepo;

        protected abstract AccountManager service();

        @Test
        @DisplayName("register() delegates to repository and returns saved account")
        void registerDelegatesToRepository() {
            Account input = account(0L);
            Account saved = account(1L);
            when(accountRepo.save(input)).thenReturn(saved);

            Account result = service().register(input);

            assertThat(result.id()).isEqualTo(1L);
            verify(accountRepo).save(input);
        }

        @Test
        @DisplayName("delete() delegates to repository")
        void deleteDelegatesToRepository() {
            service().delete(7L);
            verify(accountRepo).delete(7L);
        }

        @Test
        @DisplayName("update() persists changes and returns updated account")
        void updateDelegatesToRepository() {
            Account a = account(2L);
            when(accountRepo.save(a)).thenReturn(a);

            Account result = service().update(a);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("getById() returns account when found")
        void getByIdReturnsAccount() {
            when(accountRepo.findById(3L)).thenReturn(Optional.of(account(3L)));

            Optional<Account> result = service().getById(3L);

            assertThat(result).isPresent();
            verify(accountRepo).findById(3L);
        }

        @Test
        @DisplayName("getById() returns empty when not found")
        void getByIdReturnsEmpty() {
            when(accountRepo.findById(anyLong())).thenReturn(Optional.empty());

            Optional<Account> result = service().getById(99L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("authenticate() returns account when credentials match")
        void authenticateReturnsAccountOnValidCredentials() {
            Account a = account(4L);
            // The implementation is expected to use accountRepo to look up by user/password
            // Stub the most likely lookup signature:
            when(accountRepo.findById(anyLong())).thenReturn(Optional.of(a));

            // Test that authenticate doesn't throw for valid credentials.
            // Adjust the stub if your implementation uses a different query method.
            assertThatCode(() -> service().authenticate("user@test.com", "hashed"))
                    .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("authenticate() throws when credentials are wrong")
        void authenticateThrowsForInvalidCredentials() {
            // Implementation should throw when user is not found / password mismatch
            assertThatThrownBy(() -> service().authenticate("bad@user.com", "wrongpw"))
                    .isInstanceOf(Exception.class);
        }
    }
}
