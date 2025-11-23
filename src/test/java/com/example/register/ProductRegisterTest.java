package com.example.register;

import com.example.dto.ProductRequestImpl;
import com.example.dto.ProductResponseImpl;

import com.example.repository.ProductRepositoryImpl;
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

public class ProductRegisterTest {

    private JdbcTemplate jdbcTemplate;
    private ProductRegisterImpl service;

    @BeforeClass
    void setUp() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://127.0.0.1:5432/app_db?sslmode=disable");
        dataSource.setUsername("app_user");
        dataSource.setPassword("app_password");

        runLiquibaseMigrations(dataSource);

        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.service = new ProductRegisterImpl(new ProductRepositoryImpl(jdbcTemplate));
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
            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.yaml",
                    new ClassLoaderResourceAccessor(),
                    database
            );
            liquibase.update();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to run Liquibase migrations for ProductRegisterTest", e);
        }
    }

    @Test
    void createProductValid() {
        ProductRequestImpl req = ProductRequestImpl.builder()
                .title("Phone")
                .description("Smartphone")
                .price(300.0)
                .quantity(2)
                .build();

        ProductResponseImpl resp = service.createProduct(req);
        assertEquals(resp.title, "Phone");
        assertTrue(resp.available);
        assertEquals(resp.totalValue, 600.0, 1e-6);

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
        assertNotNull(count);
        assertEquals(count.longValue(), 1L);

        ProductResponseImpl fromDb = jdbcTemplate.queryForObject(
                "SELECT id, title, description, price, quantity, total_value, category, available FROM products WHERE title = ?",
                productRowMapper(),
                "Phone"
        );
        assertNotNull(fromDb);
        assertEquals(fromDb.title, "Phone");
        assertEquals(fromDb.description, "Smartphone");
        assertEquals(fromDb.price, 300.0, 1e-6);
        assertEquals(fromDb.quantity, 2);
        assertEquals(fromDb.totalValue, 600.0, 1e-6);
        assertEquals(fromDb.category, "GENERAL");
        assertTrue(fromDb.available);
    }

    @Test
    void applyDiscountCalculates() {
        ProductRequestImpl req = ProductRequestImpl.builder()
                .title("Item")
                .description("")
                .price(100.0)
                .quantity(3)
                .build();

        ProductResponseImpl resp = service.applyDiscount(req, 10.0);
        assertEquals(resp.category, "DISCOUNTED");
        assertEquals(resp.price, 90.0, 1e-6);

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM products", Long.class);
        assertNotNull(count);
        assertEquals(count.longValue(), 1L);

        ProductResponseImpl fromDb = jdbcTemplate.queryForObject(
                "SELECT id, title, description, price, quantity, total_value, category, available FROM products WHERE title = ?",
                productRowMapper(),
                "Item"
        );
        assertNotNull(fromDb);
        assertEquals(fromDb.title, "Item");
        assertEquals(fromDb.description, "");
        assertEquals(fromDb.price, 90.0, 1e-6);
        assertEquals(fromDb.quantity, 3);
        assertEquals(fromDb.totalValue, 270.0, 1e-6);
        assertEquals(fromDb.category, "DISCOUNTED");
        assertTrue(fromDb.available);
    }

    private RowMapper<ProductResponseImpl> productRowMapper() {
        return (rs, rowNum) -> ProductResponseImpl.builder()
                .id(rs.getLong("id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .price(rs.getBigDecimal("price").doubleValue())
                .quantity(rs.getInt("quantity"))
                .totalValue(rs.getBigDecimal("total_value").doubleValue())
                .category(rs.getString("category"))
                .available(rs.getBoolean("available"))
                .build();
    }
}
