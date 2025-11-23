package com.example.repository;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class UserRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private UserRepositoryImpl repository;

    @BeforeClass
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/app_db?sslmode=disable");
        dataSource.setUsername("app_user");
        dataSource.setPassword("app_password");

        runLiquibaseMigrations(dataSource);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.repository = new UserRepositoryImpl(jdbcTemplate);
    }

    @BeforeMethod
    void cleanUsersTable() {
        jdbcTemplate.update("DELETE FROM users");
    }

    @SuppressWarnings("deprecation")
    private void runLiquibaseMigrations(DriverManagerDataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to run Liquibase migrations for UserRepositoryTest", e);
        }
    }

    @Test
    void saveAndFindByEmailPersistAndLoadUser() {
        UserRequestImpl req = UserRequestImpl.builder()
                .name("Repo User")
                .email("repo@example.com")
                .age(33)
                .build();

        UserResponseImpl saved = UserResponseImpl.builder()
                .id(null)
                .name(req.name)
                .email(req.email)
                .age(req.age)
                .status("SAVED")
                .createdAt(null)
                .build();

        UserResponseImpl persisted = repository.save(saved);
        assertNotNull(persisted.id);

        UserResponseImpl fromDb = repository.findByEmail("repo@example.com");
        assertNotNull(fromDb.id);
        assertEquals(fromDb.email, "repo@example.com");
        assertEquals(fromDb.name, "Repo User");
        assertEquals(fromDb.age, 33);
        assertEquals(fromDb.status, "SAVED");
    }
}
