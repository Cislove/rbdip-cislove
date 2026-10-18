package com.rbdip.bookstore.order;

import com.rbdip.bookstore.confirmation.ConfirmationService;
import com.rbdip.bookstore.product.Product;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.rbdip.bookstore.product.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * God-класс: валидация, расчёт цены, персистентность и "уведомление
 * клиента" смешаны в одном методе. Цель для рефакторинга по SRP в ЛР1.
 */
@Service
public class OrderService {

    private final ProductService productService;
    private final OrderRepository orderRepository;
    private final OrderItemService orderItemService;
    private final ConfirmationService confirmationService;
    private final PricingCalculator pricingCalculator = new PricingCalculator();

    public OrderService(
            ProductService productService,
            OrderRepository orderRepository,
            OrderItemService orderItemService,
            ConfirmationService confirmationService) {
        this.productService = productService;
        this.orderRepository = orderRepository;
        this.orderItemService = orderItemService;
        this.confirmationService = confirmationService;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        List<Product> products = productService.getRequiredProducts(
                request.items().stream().map(CreateOrderRequest.Item::productId).toList());

        List<PricingCalculator.LineItem> lineItems = getLineItemsByProductsAndItem(products, request.items());

        BigDecimal total = pricingCalculator.calculateOrderTotal(
                lineItems, request.customerType() == null ? "regular" : request.customerType(), request.couponCode());

        Order order = orderRepository.save(new Order(
                request.customerFullName(), request.customerAddress(), request.customerPhone(), "new"));

        orderItemService.saveOrderItems(order.getId(), products, lineItems);

        confirmationService.sendOrderConfirmation(request.customerFullName(), order.getId(), total);

        return order;
    }

    private List<PricingCalculator.LineItem> getLineItemsByProductsAndItem(List<Product> products, List<CreateOrderRequest.Item> items) {
        ArrayList<PricingCalculator.LineItem> lineItems = new ArrayList<>(products.size());
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            CreateOrderRequest.Item item = items.get(i);
            lineItems.add(i, new PricingCalculator.LineItem(product.getPrice(), item.quantity()));
        }
        return lineItems;
    }
}
