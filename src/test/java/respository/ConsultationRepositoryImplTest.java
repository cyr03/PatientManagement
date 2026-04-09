package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.one.patientmanagement.repository.impl.ConsultationRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

class ConsultationRepositoryImplTest extends RepositoryContractTest.ConsultationRepositoryContract {

    private DataSource dataSource;

    @BeforeEach
    void setUpDB() {
        dataSource = TestDataSourceFactory.inMemory();

        new DatabaseInitializer(dataSource).init();
    }

    @Override
    protected ConsultationRepositoryImpl repository() {
        return new ConsultationRepositoryImpl(dataSource);
    }
}
