-- V12: Create daily_revenue table
-- Mama Mpoki Car Hire - Daladala daily revenue tracking

CREATE TABLE daily_revenue (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_id    BIGINT NOT NULL,
    source          VARCHAR(20) NOT NULL COMMENT 'FARE, CHARTER, ADVERTISING, OTHER',
    amount          DECIMAL(10,2) NOT NULL COMMENT 'Revenue amount in TZS',
    description     VARCHAR(200),
    revenue_date    DATE NOT NULL,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_revenue_operation FOREIGN KEY (operation_id) REFERENCES daily_operation(id),
    CONSTRAINT chk_revenue_source CHECK (source IN ('FARE', 'CHARTER', 'ADVERTISING', 'OTHER')),
    CONSTRAINT chk_revenue_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
