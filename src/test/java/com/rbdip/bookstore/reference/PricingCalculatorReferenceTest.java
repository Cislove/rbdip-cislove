package com.rbdip.bookstore.reference;

import static org.assertj.core.api.Assertions.assertThat;

import com.rbdip.bookstore.order.PricingCalculator;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Эталонный тест покрывает только базовый путь расчёта цены (обычный
 * клиент, без купона, без оптовой скидки). Остальные ветки (VIP,
 * wholesale, купоны, отрицательный итог, скидка за объём, потолок
 * 1000) намеренно НЕ покрыты - это задание ЛР2: написать
 * характеризационные тесты на эти случаи перед рефакторингом класса.
 */
class PricingCalculatorReferenceTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void calculatesSimpleRegularOrder() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("20.00");
    }

    @Test
    void calculatesNegativeRegularOrder() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("-10.00"), 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @ParameterizedTest
    @MethodSource(value = "provideCheckCalculateScaleScenarios")
    void checkCalculateScale(List<PricingCalculator.LineItem> items, String customerType, String couponCode, BigDecimal expectedTotal) {
        assertCalculation(items, customerType, couponCode, expectedTotal);
    }

    @ParameterizedTest
    @MethodSource(value = "provideCheckCalculateTotalPriceScenarios")
    void calculateOrder(List<PricingCalculator.LineItem> items, String customerType, String couponCode, BigDecimal expectedTotal) {
        assertCalculation(items, customerType, couponCode, expectedTotal);
    }

    @ParameterizedTest
    @MethodSource(value = "provideCustomerTypeDiscountScenarios")
    void calculateOrderWithCustomerTypeDiscount(List<PricingCalculator.LineItem> items, String customerType, String couponCode, BigDecimal expectedTotal) {
        assertCalculation(items, customerType, couponCode, expectedTotal);
    }

    @ParameterizedTest
    @MethodSource(value = "provideCouponScenarios")
    void calculateOrderWithCoupon(List<PricingCalculator.LineItem> items, String customerType, String couponCode, BigDecimal expectedTotal) {
        assertCalculation(items, customerType, couponCode, expectedTotal);
    }

    @ParameterizedTest
    @MethodSource(value = "provideLargeOrderDiscountScenarios")
    void calculateOrderWithLargeOrderDiscount(List<PricingCalculator.LineItem> items, String customerType, String couponCode, BigDecimal expectedTotal) {
        assertCalculation(items, customerType, couponCode, expectedTotal);
    }

    private void assertCalculation(List<PricingCalculator.LineItem> items, String customerType, String couponCode, BigDecimal expectedTotal) {
        BigDecimal total = calculator.calculateOrderTotal(items, customerType, couponCode);
        assertThat(total).isEqualByComparingTo(expectedTotal);
    }

    private static Stream<Arguments> provideCheckCalculateTotalPriceScenarios() {
        return Stream.of (
                Arguments.of(
                        List.of(
                                new PricingCalculator.LineItem(new BigDecimal("1.00"), 10),
                                new PricingCalculator.LineItem(new BigDecimal("5.00"), 2)
                        ),
                        "regular",
                        null,
                        new BigDecimal("20.00")
                ),
                Arguments.of(
                        List.of(
                                new PricingCalculator.LineItem(new BigDecimal("1.00"), 11),
                                new PricingCalculator.LineItem(new BigDecimal("5.00"), 2)
                        ),
                        "regular",
                        null,
                        new BigDecimal("20.45")
                )
        );
    }

    private static Stream<Arguments> provideCustomerTypeDiscountScenarios() {
        return Stream.of(
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)),
                        "vip",
                        null,
                        new BigDecimal("18.00")
                ),
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)),
                        "wholesale",
                        null,
                        new BigDecimal("17.00")
                ),
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)),
                        "regular",
                        null,
                        new BigDecimal("20.00")
                )
        );
    }

    private static Stream<Arguments> provideCouponScenarios() {
        return Stream.of(
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)),
                        "regular",
                        "SAVE10",
                        new BigDecimal("10.00")
                ),
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)),
                        "regular",
                        "SAVE20PERCENT",
                        new BigDecimal("16.00")
                ),
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)),
                        "regular",
                        "INVALID_COUPON",
                        new BigDecimal("20.00")
                )
        );
    }

    private static Stream<Arguments> provideLargeOrderDiscountScenarios() {
        return Stream.of(
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("501.00"), 2)),
                        "regular",
                        null,
                        new BigDecimal("981.96")
                ),
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("500.00"), 2)),
                        "regular",
                        null,
                        new BigDecimal("1000.00")
                )
        );
    }

    private static Stream<Arguments> provideCheckCalculateScaleScenarios() {
        return Stream.of(
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.124"), 1)),
                        "regular",
                        null,
                        new BigDecimal("10.12")
                ),
                Arguments.of(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.125"), 1)),
                        "regular",
                        null,
                        new BigDecimal("10.13")
                )
        );
    }
}
