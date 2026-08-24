-- V8: Create trip_expense table
-- Mama Mpoki Car Hire - Trip expense tracking

CREATE TABLE trip_expense (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    trip_id         BIGINT NOT NULL,
    expense_type    VARCHAR(20) NOT NULL COMMENT 'FUEL, DRIVER_ALLOWANCE, TOLL, FOOD, ACCOMMODATION, REPAIR, OTHER',
    amount          DECIMAL(10,2) NOT NULL COMMENT 'Expense amount in TZS',
    description     VARCHAR(200),
    expense_date    DATE NOT NULL,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_trip_expense_trip FOREIGN KEY (trip_id) REFERENCES trip(id),
    CONSTRAINT chk_trip_expense_type CHECK (expense_type IN ('FUEL', 'DRIVER_ALLOWANCE', 'TOLL', 'FOOD', 'ACCOMMODATION', 'REPAIR', 'OTHER')),
    CONSTRAINT chk_trip_expense_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
