package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.one.patientmanagement.repository.impl.VitalsRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

class VitalsRepositoryImplTest extends RepositoryContractTest.VitalsRepositoryContract {

    private DataSource dataSource;

    @BeforeEach
    void setUpDB() {
        dataSource = TestDataSourceFactory.inMemory();

        new DatabaseInitializer(dataSource).init();
    }

    @Override
    protected VitalsRepositoryImpl repository() {
        return new VitalsRepositoryImpl(dataSource);
    }
}
