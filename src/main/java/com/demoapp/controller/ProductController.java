package com.demoapp.controller;

import com.demoapp.entity.Product;
import com.demoapp.model.ProductDTO;
import com.demoapp.service.ProductService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/products")
@CrossOrigin(origins = "http://localhost:4200")
@PermitAll
public class ProductController {
    private final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    // Partial Retrieval: Get products by category with optional fields
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Map<String, Object>>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(required = false) String fields) {
        try {
            List<Product> products = productService.getProductsByCategory(categoryId);
            List<Map<String, Object>> partialProducts = productService.filterFields(products, fields);
            return ResponseEntity.ok(partialProducts);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        try {
            Product product = productService.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @PostMapping("/{categoryId}")
    @PermitAll
    public ResponseEntity<Product> addProduct(@RequestBody ProductDTO productDTO, @PathVariable Long categoryId) {
        try {
            Product savedProduct = productService.addProduct(productDTO, categoryId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    // Partial Update: Update product by ID with PATCH
    @PatchMapping("/{id}")
    public ResponseEntity<Product> partialUpdateProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        try {
            Product updatedProduct = productService.partialUpdateProduct(id, updates);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}