package com.platzi.market.web.controller;

import com.platzi.market.domain.Category;
import com.platzi.market.domain.Product;
import com.platzi.market.domain.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin(origins = "*")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/getCategory")
    public ResponseEntity<List<Category>> getAll() {
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
    }
}
