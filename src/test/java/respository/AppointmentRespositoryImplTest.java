package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.one.patientmanagement.repository.AppointmentRepository;
import org.one.patientmanagement.repository.impl.AppointmentRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

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
