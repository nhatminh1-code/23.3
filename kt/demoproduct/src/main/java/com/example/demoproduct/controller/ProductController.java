package com.example.demoproduct.controller;

import com.example.demoproduct.model.Product;
import com.example.demoproduct.service.CategoryService;
import com.example.demoproduct.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    private static final String UPLOAD_DIR = "uploads/";

    // ========== LIST (Câu 1,2,3,4 tích hợp) ==========
    @GetMapping
    public String listProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            Model model) {

        int pageSize = 5;

        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortField).ascending()
                : Sort.by(sortField).descending();

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Page<Product> productPage = productService.getProducts(keyword, categoryId, pageable);

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalItems", productPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir",
                sortDir.equalsIgnoreCase("asc") ? "desc" : "asc");
        model.addAttribute("categories", categoryService.getAllCategories());

        return "products/list";
    }

    // ========== CREATE FORM ==========
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/create";
    }

    // ========== SAVE ==========
    @PostMapping("/save")
    public String saveProduct(
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam Long categoryId,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            Product product = new Product();
            product.setName(name);
            product.setPrice(price);
            product.setCategory(categoryService.getCategoryById(categoryId));

            if (!imageFile.isEmpty()) {
                String filename = saveImage(imageFile);
                product.setImage(filename);
            }

            productService.save(product);
            redirectAttributes.addFlashAttribute("message",
                    "Tao san pham thanh cong!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Loi upload anh: " + e.getMessage());
        }

        return "redirect:/products";
    }

    // ========== EDIT FORM ==========
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "products/edit";
    }

    // ========== UPDATE ==========
    @PostMapping("/update/{id}")
    public String updateProduct(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam Double price,
            @RequestParam Long categoryId,
            @RequestParam("imageFile") MultipartFile imageFile,
            RedirectAttributes redirectAttributes) {

        try {
            Product product = productService.getProductById(id);
            if (product == null) {
                return "redirect:/products";
            }

            product.setName(name);
            product.setPrice(price);
            product.setCategory(categoryService.getCategoryById(categoryId));

            if (!imageFile.isEmpty()) {
                String filename = saveImage(imageFile);
                product.setImage(filename);
            }

            productService.save(product);
            redirectAttributes.addFlashAttribute("message",
                    "Cap nhat san pham thanh cong!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Loi upload anh: " + e.getMessage());
        }

        return "redirect:/products";
    }

    // ========== DELETE ==========
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {
        productService.deleteById(id);
        redirectAttributes.addFlashAttribute("message",
                "Xoa san pham thanh cong!");
        return "redirect:/products";
    }

    // ========== HELPER: Save image file ==========
    private String saveImage(MultipartFile file) throws IOException {
        File dir = new File(UPLOAD_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String filename = System.currentTimeMillis() + "_"
                + file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + filename);
        Files.write(path, file.getBytes());
        return filename;
    }
}