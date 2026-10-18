package com.rbdip.bookstore.product;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<Product> getRequiredProducts(List<Long> productIds) {
        return productIds.stream().map(
                id -> productRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("product " + id + " not found"))
        ).toList();
    }
}
