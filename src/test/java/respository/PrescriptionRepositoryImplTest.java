package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.one.patientmanagement.repository.impl.PrescriptionRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

class PrescriptionRepositoryImplTest extends RepositoryContractTest.PrescriptionRepositoryContract {

    private DataSource dataSource;

    @BeforeEach
    void setUpDB() {
        dataSource = TestDataSourceFactory.inMemory();

        new DatabaseInitializer(dataSource).init();
    }

    @Override
    protected PrescriptionRepositoryImpl repository() {
        return new PrescriptionRepositoryImpl(dataSource);
    }
}
