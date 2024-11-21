package com.demoapp.controller;

import com.demoapp.exception.ProductNotFoundException;
import com.demoapp.service.ProductService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
@PermitAll
public class ImageController {

    private final ProductService productService;

    @GetMapping("/{productId}")
    public ResponseEntity<byte[]> getProductImage(@PathVariable Long productId) {
        try {
            byte[] imageData = productService.getProductImage(productId);
            if (imageData.length == 0) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(imageData);
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/{productId}")
    public ResponseEntity<String> uploadProductImage(
            @PathVariable Long productId,
            @RequestParam("image") MultipartFile imageFile) {
        try {
            byte[] imageBytes = imageFile.getBytes();
            productService.setProductImage(productId, imageBytes);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (ProductNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product not found");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image");
        }
    }
}
