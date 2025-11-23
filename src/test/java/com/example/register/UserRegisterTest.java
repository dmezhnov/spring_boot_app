package com.example.register;

import com.example.dto.UserRequestImpl;
import com.example.dto.UserResponseImpl;

import com.example.repository.UserRepositoryImpl;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

public class UserRegisterTest {

    private JdbcTemplate jdbcTemplate;
    private UserRegisterImpl service;

    @BeforeClass
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/app_db?sslmode=disable");
        dataSource.setUsername("app_user");
        dataSource.setPassword("app_password");

        runLiquibaseMigrations(dataSource);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.service = new UserRegisterImpl(new UserRepositoryImpl(jdbcTemplate));
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
            throw new IllegalStateException("Failed to run Liquibase migrations for UserRegisterTest", e);
        }
    }

    @Test
    void processUserCreatesActive() {
        UserRequestImpl req = UserRequestImpl.builder()
                .name("Alice")
                .email("alice@example.com")
                .age(28)
                .build();

        UserResponseImpl resp = service.processUser(req);
        assertEquals(resp.name, "ALICE");
        assertEquals(resp.status, "ACTIVE");
        assertEquals(resp.age, 28);

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        assertNotNull(count);
        assertEquals(count.longValue(), 1L);

        UserResponseImpl fromDb = jdbcTemplate.queryForObject(
                "SELECT id, name, email, age, status, created_at FROM users WHERE email = ?",
                userRowMapper(),
                "alice@example.com"
        );
        assertNotNull(fromDb);
        assertEquals(fromDb.name, "ALICE");
        assertEquals(fromDb.email, "alice@example.com");
        assertEquals(fromDb.age, 28);
        assertEquals(fromDb.status, "ACTIVE");
    }

    @Test
    void validateUserReturnsValidated() {
        UserRequestImpl req = UserRequestImpl.builder()
                .name("Bob")
                .email("bob@example.com")
                .age(40)
                .build();

        UserResponseImpl resp = service.validateUser(req);
        assertEquals(resp.status, "VALIDATED");

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM users", Long.class);
        assertNotNull(count);
        assertEquals(count.longValue(), 1L);

        UserResponseImpl fromDb = jdbcTemplate.queryForObject(
                "SELECT id, name, email, age, status, created_at FROM users WHERE email = ?",
                userRowMapper(),
                "bob@example.com"
        );
        assertNotNull(fromDb);
        assertEquals(fromDb.name, "Bob");
        assertEquals(fromDb.email, "bob@example.com");
        assertEquals(fromDb.age, 40);
        assertEquals(fromDb.status, "VALIDATED");
    }

    private RowMapper<UserResponseImpl> userRowMapper() {
        return (rs, rowNum) -> UserResponseImpl.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .age(rs.getInt("age"))
                .status(rs.getString("status"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .build();
    }
}
