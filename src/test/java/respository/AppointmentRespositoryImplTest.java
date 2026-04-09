package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.one.patientmanagement.repository.AppointmentRepository;
import org.one.patientmanagement.repository.impl.AppointmentRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

/**
 * Integration contract test for AppointmentRepository.
 */
class AppointmentRespositoryImplTest extends RepositoryContractTest.AppointmentRepositoryContract {

    private DataSource dataSource;

    @BeforeEach
    void setUpDB() {
        dataSource = TestDataSourceFactory.inMemory();

        new DatabaseInitializer(dataSource).init();
    }

    @Override
    protected AppointmentRepository repository() {
        return new AppointmentRepositoryImpl(dataSource);
    }
}
