CREATE TABLE IF NOT EXISTS plants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    scientific_name VARCHAR(200),
    price DECIMAL(10,2) NOT NULL,
    stock_quantity INT NOT NULL,
    CONSTRAINT chk_price_positive CHECK (price >= 0.01),
    CONSTRAINT chk_stock_non_negative CHECK (stock_quantity >= 0),
    CONSTRAINT uk_plant_name UNIQUE (name)
);

CREATE INDEX IF NOT EXISTS idx_plant_name ON plants(name);
CREATE INDEX IF NOT EXISTS idx_plant_scientific_name ON plants(scientific_name);
CREATE INDEX IF NOT EXISTS idx_plant_price ON plants(price);
CREATE INDEX IF NOT EXISTS idx_plant_stock ON plants(stock_quantity);
