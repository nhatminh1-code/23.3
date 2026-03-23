package com.example.demoproduct.service;

import com.example.demoproduct.model.CartItem;
import com.example.demoproduct.model.Order;
import com.example.demoproduct.model.OrderDetail;
import com.example.demoproduct.model.Product;
import com.example.demoproduct.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Transactional
    public Order createOrder(Order order, Map<Long, CartItem> cart) {
        double totalAmount = 0;

        for (CartItem item : cart.values()) {
            Product product = productService.getProductById(item.getProductId());
            if (product != null) {
                OrderDetail detail = new OrderDetail();
                detail.setOrder(order);
                detail.setProduct(product);
                detail.setQuantity(item.getQuantity());
                detail.setPrice(item.getPrice());
                detail.setSubtotal(item.getSubtotal());
                order.getOrderDetails().add(detail);
                totalAmount += item.getSubtotal();
            }
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(null);
    }
}