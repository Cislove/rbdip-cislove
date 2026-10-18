package com.rbdip.bookstore.confirmation;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ConfirmationService {
    private final SenderService senderService;

    public ConfirmationService(SenderService senderService) {
        this.senderService = senderService;
    }

    public void sendOrderConfirmation(String customerName, Long orderId, BigDecimal total) {
        String message = createConfirmationMessage(customerName, orderId, total);
        senderService.send(message);
    }

    private String createConfirmationMessage(String customerName, Long orderId, BigDecimal total) {
        return String.format("[email] Dear %s, your order #%d for %s has been placed.%n", customerName, orderId, total);
    }
}
