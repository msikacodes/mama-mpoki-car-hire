-- V9: Create payment table
-- Mama Mpoki Car Hire - Payment tracking for bookings

CREATE TABLE payment (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_id      BIGINT NOT NULL,
    amount          DECIMAL(12,2) NOT NULL COMMENT 'Payment amount in TZS',
    payment_method  VARCHAR(20) NOT NULL COMMENT 'CASH, MOBILE_MONEY, BANK_TRANSFER, CHEQUE, OTHER',
    payment_date    DATE NOT NULL,
    reference_number VARCHAR(100) COMMENT 'e.g. M-Pesa confirmation code',
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES hire_booking(id),
    CONSTRAINT chk_payment_method CHECK (payment_method IN ('CASH', 'MOBILE_MONEY', 'BANK_TRANSFER', 'CHEQUE', 'OTHER')),
    CONSTRAINT chk_payment_amount CHECK (amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
