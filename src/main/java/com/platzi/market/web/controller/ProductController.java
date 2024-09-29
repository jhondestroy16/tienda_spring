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

    @PostMapping("/save/masivo")
    public void saveProductsFromFile(@RequestParam("file") MultipartFile file) {
        List<Product> products = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("nombre")) {
                    continue; // Ignorar la cabecera
                }

                String[] data = line.split(";");

                // Validar que la línea tenga los 4 campos esperados
                if (data.length < 4) {
                    // Manejar error o continuar si no tiene suficientes datos
                    System.err.println("Línea con formato inválido: " + line);
                    continue; // O puedes lanzar una excepción si prefieres
                }

                // Asignar valores al objeto Product
                Product product = new Product();
                product.setNombre(data[0]);
                product.setCategoryId(Integer.parseInt(data[1]));
                product.setPrice(Integer.parseInt(data[2]));
                product.setStock(Integer.parseInt(data[3]));
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
