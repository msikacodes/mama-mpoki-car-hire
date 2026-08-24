-- V13: Create daily_expense table
-- Mama Mpoki Car Hire - Daladala daily expense tracking

CREATE TABLE daily_expense (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    operation_id    BIGINT NOT NULL,
    expense_type    VARCHAR(20) NOT NULL COMMENT 'FUEL, REPAIR, TOLL, MAINTENANCE, CONDUCTOR_ALLOWANCE, OTHER',
    amount          DECIMAL(10,2) NOT NULL COMMENT 'Expense amount in TZS',
    description     VARCHAR(200),
    expense_date    DATE NOT NULL,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_expense_operation FOREIGN KEY (operation_id) REFERENCES daily_operation(id),
    CONSTRAINT chk_expense_type CHECK (expense_type IN ('FUEL', 'REPAIR', 'TOLL', 'MAINTENANCE', 'CONDUCTOR_ALLOWANCE', 'OTHER')),
    CONSTRAINT chk_expense_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
