package com.demoapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private String name;
    private Double price;
    private String description;
    private String supplier;
    private BigInteger stock;
    private byte[] image; // Pentru stocarea imaginii
}
