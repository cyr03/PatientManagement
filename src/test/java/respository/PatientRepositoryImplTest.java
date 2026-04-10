package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.one.patientmanagement.repository.impl.PatientRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

class PatientRepositoryImplTest extends RepositoryContractTest.PatientRepositoryContract {

    private DataSource dataSource;

    @BeforeEach
    void setUpDB() {
        dataSource = TestDataSourceFactory.inMemory();

        new DatabaseInitializer(dataSource).init();
    }

    @Override
    protected PatientRepositoryImpl repository() {
        return new PatientRepositoryImpl(dataSource);
    }
}
