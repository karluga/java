package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;

public class DBConnection {
    public static String getUrl() {
        return String.format(
            "jdbc:%s://%s:%s/%s",
            Config.get("DB_CONNECTION"),
            Config.get("DB_HOST"),
            Config.get("DB_PORT"),
            Config.get("DB_DATABASE")
        );
    }

    public static String getUser() {
        return Config.get("DB_USERNAME");
    }

    public static String getPassword() {
        return Config.get("DB_PASSWORD");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getUrl(), getUser(), getPassword());
    }

    public static void runMigrations() {
        String dbUrl = getUrl();
        String dbUser = getUser();
        String dbPassword = getPassword();

        Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPassword)
                .locations("filesystem:src/main/resources/db/migration")
                .load();

        flyway.migrate();
    }

    public static void runSeeders() {
        String dbUrl = getUrl();
        String dbUser = getUser();
        String dbPassword = getPassword();

        Flyway flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPassword)
                .locations("filesystem:src/main/resources/db/seeds")
                .load();

        flyway.migrate();
    }

    public static void runRepair() {
        Flyway flyway = Flyway.configure()
                .dataSource(getUrl(), getUser(), getPassword())
                .locations("filesystem:src/main/resources/db/migration")
                .load();
    
        flyway.repair();
        System.out.println("Flyway repair executed.");
    }
}