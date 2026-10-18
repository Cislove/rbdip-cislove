package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Модуль расчёта цены заказа. Намеренно почти не покрыт тестами и
 * содержит magic numbers / нечитаемые ветвления скидок - цель для
 * характеризационных тестов (ЛР2) и mutation-testing гейта PIT (ЛР5).
 */
public class PricingCalculator {
    private static final String SAVE_TEN_COUPON = "SAVE10";
    private static final String SAVE_TWENTY_PERCENT_COUPON = "SAVE20PERCENT";

    private static final String VIP_CUSTOMER = "vip";
    private static final String WHOLESALE_CUSTOMER = "wholesale";

    private static final int BULK_QUANTITY_LIMIT = 10;
    private static final BigDecimal FIXED_COUPON_DISCOUNT = BigDecimal.TEN;
    private static final BigDecimal LARGE_ORDER_LIMIT = new BigDecimal("1000");

    private static final BigDecimal BULK_DISCOUNT = new BigDecimal("0.95");
    private static final BigDecimal VIP_DISCOUNT = new BigDecimal("0.90");
    private static final BigDecimal WHOLESALE_DISCOUNT = new BigDecimal("0.85");
    private static final BigDecimal PERCENT_COUPON_DISCOUNT = new BigDecimal("0.80");
    private static final BigDecimal LARGE_ORDER_DISCOUNT = new BigDecimal("0.98");


    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = calculateTotalPrice(items);
        total = applyCustomerTypeDiscount(total, customerType);
        total = applyCouponDiscount(total, couponCode);
        total = applyLargeOrderDiscount(total);

        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO;
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateTotalPrice(List<LineItem> items) {
        BigDecimal total = BigDecimal.ZERO;
        for (LineItem item : items) {
            total = total.add(calculateLineItemDiscount(item));
        }
        return total;
    }

    private BigDecimal calculateLineItemDiscount(LineItem item) {
        BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
        if (item.quantity() > BULK_QUANTITY_LIMIT) {
            linePrice = linePrice.multiply(BULK_DISCOUNT);
        }
        return linePrice;
    }

    private BigDecimal applyCustomerTypeDiscount(BigDecimal total, String customerType) {
        if (VIP_CUSTOMER.equals(customerType)) {
            return total.multiply(VIP_DISCOUNT);
        } else if (WHOLESALE_CUSTOMER.equals(customerType)) {
            return total.multiply(WHOLESALE_DISCOUNT);
        }
        return total;
    }

    private BigDecimal applyCouponDiscount(BigDecimal total, String couponCode) {
        if (SAVE_TEN_COUPON.equals(couponCode)) {
            return total.subtract(FIXED_COUPON_DISCOUNT);
        } else if (SAVE_TWENTY_PERCENT_COUPON.equals(couponCode)) {
            return total.multiply(PERCENT_COUPON_DISCOUNT);
        }
        return total;
    }

    private BigDecimal applyLargeOrderDiscount(BigDecimal total) {
        if (total.compareTo(LARGE_ORDER_LIMIT) > 0) {
            return total.multiply(LARGE_ORDER_DISCOUNT);
        }
        return total;
    }
}
