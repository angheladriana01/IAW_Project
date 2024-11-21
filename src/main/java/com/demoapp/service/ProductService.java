package com.demoapp.service;

import com.demoapp.entity.Category;
import com.demoapp.entity.Product;
import com.demoapp.exception.ProductNotFoundException;
import com.demoapp.model.ProductDTO;
import com.demoapp.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with ID: ";

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    @Transactional
    public Product addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }

        Product product = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .description(productDTO.getDescription())
                .supplier(productDTO.getSupplier())
                .stock(productDTO.getStock())
                .image(productDTO.getImage())
                .category(category)
                .build();

        return productRepository.save(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Map<String, Object>> filterFields(List<Product> products, String fields) {
        if (fields == null || fields.isEmpty()) {
            return products.stream()
                    .map(product -> {
                        Map<String, Object> fullProduct = new HashMap<>();
                        fullProduct.put("id", product.getId());
                        fullProduct.put("name", product.getName());
                        fullProduct.put("price", product.getPrice());
                        fullProduct.put("description", product.getDescription());
                        fullProduct.put("image", product.getImage() != null ? Base64.getEncoder()
                                .encodeToString(product.getImage()) : null);
                        return fullProduct;
                    })
                    .toList();
        }

        List<String> fieldList = List.of(fields.split(","));
        return products.stream()
                .map(product -> {
                    Map<String, Object> filteredProduct = new HashMap<>();
                    if (fieldList.contains("id")) {
                        filteredProduct.put("id", product.getId());
                    }
                    if (fieldList.contains("name")) {
                        filteredProduct.put("name", product.getName());
                    }
                    if (fieldList.contains("price")) {
                        filteredProduct.put("price", product.getPrice());
                    }
                    if (fieldList.contains("description")) {
                        filteredProduct.put("description", product.getDescription());
                    }
                    if (fieldList.contains("image")) {
                        filteredProduct.put("image", product.getImage() != null ? Base64.getEncoder()
                                .encodeToString(product.getImage()) : null);
                    }
                    return filteredProduct;
                })
                .toList();
    }



    public Product partialUpdateProduct(Long id, Map<String, Object> updates) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> product.setName((String) value);
                case "price" -> product.setPrice(Double.valueOf(value.toString()));
                case "description" -> product.setDescription((String) value);
                default -> throw new RuntimeException("Invalid field: " + key);
            }
        });

        return productRepository.save(product);
    }

    @Transactional
    public byte[] getProductImage(Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            return product.getImage() != null ? product.getImage() : new byte[0];
        } else {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId);
        }
    }

    @Transactional
    public void setProductImage(Long productId, byte[] image) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));
        product.setImage(image);
        productRepository.save(product);
    }
}