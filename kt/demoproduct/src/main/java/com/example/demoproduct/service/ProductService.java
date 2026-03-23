package com.example.demoproduct.service;

import com.example.demoproduct.model.Product;
import com.example.demoproduct.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getProducts(String keyword, Long categoryId, Pageable pageable) {
        if (keyword != null && !keyword.trim().isEmpty() && categoryId != null) {
            return productRepository.findByNameContainingIgnoreCaseAndCategoryId(
                    keyword.trim(), categoryId, pageable);
        } else if (keyword != null && !keyword.trim().isEmpty()) {
            return productRepository.findByNameContainingIgnoreCase(keyword.trim(), pageable);
        } else if (categoryId != null) {
            return productRepository.findByCategoryId(categoryId, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void deleteById(Long id) {
        productRepository.deleteById(id);
    }
}