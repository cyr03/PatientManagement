/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package respository;

import java.sql.SQLException;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

public final class TestDataSourceFactory {

    private TestDataSourceFactory() {
        // utility class
    }

    // Add H2 test dependency (if not already present)
// testImplementation 'com.h2database:h2:2.2.224'
    public static synchronized DataSource inMemory() {
         try {
            var dc = new SQLiteDataSource();
            dc.setUrl("jdbc:sqlite:clinic.db");
            try (var conn = dc.getConnection();
                 var stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return dc;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize DataSource", e);
        }
    }
}
