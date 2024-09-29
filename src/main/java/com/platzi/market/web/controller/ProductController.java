package com.platzi.market.web.controller;

import com.platzi.market.domain.Product;
import com.platzi.market.domain.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("/all")
    public ResponseEntity<Page<Product>> getAll1(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Product> productsPage = productService.getAll(PageRequest.of(page, size));
        return new ResponseEntity<>(productsPage, HttpStatus.OK);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable("productId") int productId) {
        return productService.getProduct(productId).map(p -> new ResponseEntity<>(p, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/category/{productId}")
    public ResponseEntity<List<Product>> getByCategory(@PathVariable("productId")int categoryId) {
        return productService.getByCategory(categoryId).map(products -> new ResponseEntity<>(products, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/save")
    public ResponseEntity<Product> save(@RequestBody Product product) {
        try{
            return new ResponseEntity<>(productService.save(product), HttpStatus.CREATED);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity delete(@PathVariable("productId")int productId) {
        if(productService.deleteProduct(productId)){
            return new ResponseEntity<>(HttpStatus.OK);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable("id") int productId,
            @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(productId, productDetails);
        return ResponseEntity.ok(updatedProduct);
    }

    @PostMapping("/save/masivo")
    public ResponseEntity<?> saveProductsFromFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("El archivo está vacío.");
        }

        if (!Objects.equals(file.getContentType(), "text/plain")) {
            return ResponseEntity.badRequest().body("Formato de archivo no soportado. Solo se permiten archivos de texto.");
        }

        List<Product> products = new ArrayList<>();
        List<String> errorLines = new ArrayList<>(); // Para almacenar las líneas con errores
        boolean headerSkipped = false;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true; // Saltar el encabezado
                    continue;
                }

                String[] data = line.split(";");

                if (data.length < 4) {
                    errorLines.add("Línea con formato inválido: " + line);
                    continue;
                }

                try {
                    Product product = new Product();
                    product.setNombre(data[0]);
                    product.setCategoryId(Integer.parseInt(data[1]));
                    product.setPrice(Double.parseDouble(data[2]));
                    product.setStock(Integer.parseInt(data[3]));
                    product.setActive(true);

                    products.add(product);
                } catch (NumberFormatException e) {
                    errorLines.add("Error en la conversión de datos en la línea: " + line);
                }
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar el archivo.");
        }

        // Guardar todos los productos en una sola transacción
        try {
            productService.saveAll(products); // Usar saveAll para optimizar el guardado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar productos: " + e.getMessage());
        }

        if (!errorLines.isEmpty()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT).body("Productos guardados parcialmente. Errores en las siguientes líneas:\n" + String.join("\n", errorLines));
        }

        return ResponseEntity.ok("Productos guardados exitosamente.");
    }



}
