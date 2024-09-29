package com.platzi.market.domain.service;

import com.platzi.market.domain.Product;
import com.platzi.market.domain.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.getAll1(pageable);
    }

    public Optional<Product> getProduct(int productId) {
        return productRepository.getProduct(productId);
    }

    public Optional<List<Product>> getByCategory(int idCategory){
        return productRepository.getByCategory(idCategory);
    }

    public Optional<List<Product>> getScareProducts(int quantity){
        return productRepository.getScareProducts(quantity);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public boolean deleteProduct(int productId) {
        return getProduct(productId).map(product -> {
            productRepository.delete(productId);
            return true;
        }).orElse(false);
    }
}
