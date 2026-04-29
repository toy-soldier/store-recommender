CREATE TABLE IF NOT EXISTS products (
    sku       INT            NOT NULL,
    name      VARCHAR(255)   NOT NULL,
    brand     VARCHAR(100)   NOT NULL,
    category  VARCHAR(100)   NOT NULL,
    price     DECIMAL(10, 2) NOT NULL,
    stock     INT            NOT NULL,
    PRIMARY KEY (sku)
);
