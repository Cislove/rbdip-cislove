package com.rbdip.bookstore.order;

import com.rbdip.bookstore.product.Product;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderItemService {
    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    @Transactional()
    public void saveOrderItems(Long orderId, List<Product> products, List<PricingCalculator.LineItem> lineItems) {
        List<OrderItem> orderItems = new ArrayList<>(products.size());
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            int quantity = lineItems.get(i).quantity();

            orderItems.add(i, new OrderItem(orderId, product.getName(), product.getPrice(), quantity));
        }

        orderItemRepository.saveAll(orderItems);
    }
}
