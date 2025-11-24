package com.example.repository;

import com.example.dto.ProductResponseImpl;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

/**
 * JDBC-based implementation of ProductRepository using Spring JdbcTemplate.
 */
@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ProductResponseImpl save(ProductResponseImpl product) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }

        String sql = """
                INSERT INTO products (title, description, price, quantity, total_value, category, available)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        BigDecimal price = BigDecimal.valueOf(product.price);
        BigDecimal totalValue = BigDecimal.valueOf(product.totalValue);

        Long generatedId = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                product.title,
                product.description,
                price,
                product.quantity,
                totalValue,
                product.category,
                product.available
        );

        product.id = generatedId;
        return product;
    }

    @Override
    public ProductResponseImpl findByTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Title must not be null or empty");
        }

        String sql = """
                SELECT id, title, description, price, quantity, total_value, category, available
                FROM products
                WHERE title = ?
                """;

        return jdbcTemplate.queryForObject(
                sql,
                (rs, rowNum) -> ProductResponseImpl.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .description(rs.getString("description"))
                        .price(rs.getBigDecimal("price").doubleValue())
                        .quantity(rs.getInt("quantity"))
                        .totalValue(rs.getBigDecimal("total_value").doubleValue())
                        .category(rs.getString("category"))
                        .available(rs.getBoolean("available"))
                        .build(),
                title
        );
    }
}
