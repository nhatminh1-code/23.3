package com.example.demoproduct.controller;

import com.example.demoproduct.model.CartItem;
import com.example.demoproduct.model.Order;
import com.example.demoproduct.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ========== Câu 7: CHECKOUT PAGE ==========
    @GetMapping("/checkout")
    @SuppressWarnings("unchecked")
    public String showCheckoutForm(HttpSession session, Model model) {
        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            return "redirect:/cart";
        }

        double totalAmount = cart.values().stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();

        model.addAttribute("cartItems", cart.values());
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("order", new Order());
        return "checkout";
    }

    // ========== PLACE ORDER ==========
    @PostMapping("/checkout/place-order")
    @SuppressWarnings("unchecked")
    public String placeOrder(
            @RequestParam String customerName,
            @RequestParam String customerEmail,
            @RequestParam String customerPhone,
            @RequestParam String customerAddress,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Map<Long, CartItem> cart =
                (Map<Long, CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    "Gio hang trong!");
            return "redirect:/cart";
        }

        Order order = new Order();
        order.setCustomerName(customerName);
        order.setCustomerEmail(customerEmail);
        order.setCustomerPhone(customerPhone);
        order.setCustomerAddress(customerAddress);

        Order savedOrder = orderService.createOrder(order, cart);

        session.removeAttribute("cart");

        return "redirect:/order/confirmation/" + savedOrder.getId();
    }

    // ========== ORDER CONFIRMATION ==========
    @GetMapping("/order/confirmation/{id}")
    public String orderConfirmation(@PathVariable Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/products";
        }
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}