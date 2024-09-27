package com.platzi.market.web.controller;

import com.platzi.market.domain.Product;
import com.platzi.market.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("/all")
    public List<Product> getAll() {
        return productService.getAll();
    }
    @GetMapping("/{productId}")
    public Optional<Product> getProduct(@PathVariable("productId") int productId) {
        return productService.getProduct(productId);
    }
    @GetMapping("/category/{productId}")
    public Optional<List<Product>> getByCategory(@PathVariable("productId")int categoryId) {
        return productService.getByCategory(categoryId);
    }
    @PostMapping("/save")
    public Product save(@RequestBody Product product) {
        try{
            return productService.save(product);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @DeleteMapping("/delete/{productId}")
    public boolean delete(@PathVariable("productId")int productId) {
        return productService.deleteProduct(productId);
    }
    @PostMapping("/save/masivo")
    public void saveProductsFromFile(@RequestParam("file") MultipartFile file) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("nombre")) {
                    continue;
                }

                // Separa la línea por los delimitadores ";"
                String[] data = line.split(";");

                // Asigna los valores a un nuevo objeto Product
                Product product = new Product();
                product.setNombre(data[0]);                    // nombre
                product.setCategoryId(Integer.parseInt(data[1])); // categoria
                product.setPrice(Integer.parseInt(data[2])); // precio_venta
                product.setStock(Integer.parseInt(data[3]));  // cantidad_stock
                product.setActive(true);

                products.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Guardar todos los productos en la base de datos
        for (Product product : products) {
            productService.save(product); // Llama a tu servicio para guardar cada producto
        }
    }
}
