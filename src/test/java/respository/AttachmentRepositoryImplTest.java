package respository;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.one.patientmanagement.repository.impl.AttachmentRepositoryImpl;
import org.one.patientmanagement.storage.DatabaseInitializer;

class AttachmentRepositoryImplTest extends RepositoryContractTest.AttachmentRepositoryContract {

    private DataSource dataSource;

    @BeforeEach
    void setUpDB() {
        dataSource = TestDataSourceFactory.inMemory();

        new DatabaseInitializer(dataSource).init();
    }

    @Override
    protected AttachmentRepositoryImpl repository() {
        return new AttachmentRepositoryImpl(dataSource);
    }
}
