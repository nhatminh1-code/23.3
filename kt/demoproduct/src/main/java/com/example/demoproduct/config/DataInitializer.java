package com.example.demoproduct.config;

import com.example.demoproduct.model.Category;
import com.example.demoproduct.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        if (categoryRepository.count() == 0) {
            Category c1 = new Category();
            c1.setName("Laptop");
            categoryRepository.save(c1);

            Category c2 = new Category();
            c2.setName("Điện thoại");
            categoryRepository.save(c2);

            Category c3 = new Category();
            c3.setName("Tablet");
            categoryRepository.save(c3);

            Category c4 = new Category();
            c4.setName("Phụ kiện");
            categoryRepository.save(c4);

            System.out.println("=== Da tao categories mau! ===");
        }
    }
}