package com.gautam.orderservice.controller;

import com.gautam.orderservice.client.NotificationClient;
import com.gautam.orderservice.client.ProductClient;
import com.gautam.orderservice.model.Order;
import com.gautam.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ProductClient productClient;
    private final NotificationClient notificationClient;

    @GetMapping
    public List<Order> getAllOrders() {
        return orderService.findAll();
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        // 1. Get product price from Product Service
        ProductClient.ProductResponse product = productClient.getProductById(order.getProductId());

        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // 2. Calculate total price
        order.setTotalPrice(product.price * order.getQuantity());

        // 3. Save order
        Order savedOrder = orderService.save(order);

        // 4. Send notification
        notificationClient.sendNotification(
                "Order " + savedOrder.getId() + " placed for " + product.name + ", total: $" + savedOrder.getTotalPrice()
        );

        return savedOrder;
    }
}
