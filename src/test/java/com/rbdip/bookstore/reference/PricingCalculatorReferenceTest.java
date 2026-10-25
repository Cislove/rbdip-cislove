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
}
