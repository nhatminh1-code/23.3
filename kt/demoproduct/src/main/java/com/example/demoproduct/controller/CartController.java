package com.example.demoproduct.controller;

import com.example.demoproduct.model.CartItem;
import com.example.demoproduct.model.Product;
import com.example.demoproduct.service.ProductService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private ProductService productService;

    // ========== Câu 6: VIEW CART ==========
    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        Map<Long, CartItem> cart = getCart(session);
        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalAmount", calculateTotal(cart));
        model.addAttribute("cartSize", cart.size());
        return "cart";
    }

    // ========== Câu 5: ADD TO CART ==========
    @PostMapping("/add")
    public String addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Product product = productService.getProductById(productId);
        if (product == null) {
            redirectAttributes.addFlashAttribute("error",
                    "San pham khong ton tai!");
            return "redirect:/products";
        }

        Map<Long, CartItem> cart = getCart(session);

        if (cart.containsKey(productId)) {
            CartItem item = cart.get(productId);
            item.setQuantity(item.getQuantity() + quantity);
        } else {
            CartItem item = new CartItem(
                    product.getId(),
                    product.getName(),
                    product.getImage(),
                    product.getPrice(),
                    quantity
            );
            cart.put(productId, item);
        }

        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("message",
                "Da them \"" + product.getName() + "\" vao gio hang!");
        return "redirect:/products";
    }

    // ========== UPDATE QUANTITY ==========
    @PostMapping("/update")
    public String updateQuantity(
            @RequestParam Long productId,
            @RequestParam Integer quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<Long, CartItem> cart = getCart(session);

        if (cart.containsKey(productId)) {
            if (quantity <= 0) {
                cart.remove(productId);
                redirectAttributes.addFlashAttribute("message",
                        "Da xoa san pham khoi gio hang!");
            } else {
                cart.get(productId).setQuantity(quantity);
                redirectAttributes.addFlashAttribute("message",
                        "Da cap nhat so luong!");
            }
        }

        session.setAttribute("cart", cart);
        return "redirect:/cart";
    }

    // ========== REMOVE ITEM ==========
    @GetMapping("/remove/{productId}")
    public String removeFromCart(
            @PathVariable Long productId,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<Long, CartItem> cart = getCart(session);
        cart.remove(productId);
        session.setAttribute("cart", cart);
        redirectAttributes.addFlashAttribute("message",
                "Da xoa san pham khoi gio hang!");
        return "redirect:/cart";
    }

    // ========== CLEAR CART ==========
    @GetMapping("/clear")
    public String clearCart(HttpSession session,
                            RedirectAttributes redirectAttributes) {
        session.removeAttribute("cart");
        redirectAttributes.addFlashAttribute("message",
                "Da xoa toan bo gio hang!");
        return "redirect:/cart";
    }

    // ========== HELPERS ==========
    @SuppressWarnings("unchecked")
    private Map<Long, CartItem> getCart(HttpSession session) {
        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }
        return cart;
    }

    private double calculateTotal(Map<Long, CartItem> cart) {
        return cart.values().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
}