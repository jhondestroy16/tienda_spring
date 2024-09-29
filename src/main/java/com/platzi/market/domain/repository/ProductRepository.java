package com.platzi.market.domain.repository;

import com.platzi.market.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    List<Product> getAll();
    Optional<List<Product>> getByCategory(int idCategory);
    Optional<List<Product>> getScareProducts(int quantity);
    Optional<Product> getProduct(int productId);
    Product save(Product product);
    void delete(int productId);
    Page<Product> getAll1(Pageable pageable);
}
