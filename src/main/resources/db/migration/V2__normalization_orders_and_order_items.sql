-- Нормализация схемы учебного проекта Bookstore.

CREATE TABLE customers (
   id BIGSERIAL PRIMARY KEY,
   full_name VARCHAR(255) NOT NULL,
   address VARCHAR(500),
   phone VARCHAR(50)
);

ALTER TABLE orders
    ADD COLUMN customer_id BIGINT NOT NULL REFERENCES customers(id),
    DROP COLUMN customer_full_name,
    DROP COLUMN customer_address,
    DROP COLUMN customer_phone;

ALTER TABLE order_items
    ADD COLUMN product_id BIGINT NOT NULL REFERENCES products(id),
    DROP COLUMN product_name,
    DROP COLUMN product_price;