package com.example.repository;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;
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
import static org.testng.Assert.assertTrue;

/**
 * Integration tests for {@link ProductRepositoryImpl} using a PostgreSQL database and Liquibase migrations.
 *
 * <p>Usage example:
 * {@code
 * ProductRepositoryTest test = new ProductRepositoryTest();
 * test.setUp();
 * test.saveAndFindByTitlePersistAndLoadProduct();
 * }
 */
public class ProductRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private ProductRepositoryImpl repository;

    @BeforeClass
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/app_db?sslmode=disable");
        dataSource.setUsername("app_user");
        dataSource.setPassword("app_password");

        runLiquibaseMigrations(dataSource);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.repository = new ProductRepositoryImpl(jdbcTemplate);
    }

    @BeforeMethod
    void cleanProductsTable() {
        jdbcTemplate.update("DELETE FROM products");
    }

    @SuppressWarnings("deprecation")
    private void runLiquibaseMigrations(DriverManagerDataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            )) {
                liquibase.update();
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to run Liquibase migrations for ProductRepositoryTest", e);
        }
    }

    @Test
    void saveAndFindByTitlePersistAndLoadProduct() {
        ProductRequestImpl req = ProductRequestImpl.builder()
                .title("Repo Product")
                .description("From repository test")
                .price(10.0)
                .quantity(5)
                .build();

        double totalValue = req.price * req.quantity;

        ProductResponseImpl saved = ProductResponseImpl.builder()
                .id(null)
                .title(req.title)
                .description(req.description)
                .price(req.price)
                .quantity(req.quantity)
                .totalValue(totalValue)
                .category("REPO")
                .available(true)
                .build();

        ProductResponseImpl persisted = repository.save(saved);
        assertNotNull(persisted.id);

        ProductResponseImpl fromDb = repository.findByTitle("Repo Product");
        assertNotNull(fromDb.id);
        assertEquals(fromDb.title, "Repo Product");
        assertEquals(fromDb.description, "From repository test");
        assertEquals(fromDb.price, 10.0, 1e-6);
        assertEquals(fromDb.quantity, 5);
        assertEquals(fromDb.totalValue, totalValue, 1e-6);
        assertEquals(fromDb.category, "REPO");
        assertTrue(fromDb.available);
    }
}
