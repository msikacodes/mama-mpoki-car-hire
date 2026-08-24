-- V6: Create hire_booking table
-- Mama Mpoki Car Hire - Special hire booking management

CREATE TABLE hire_booking (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    vehicle_id      BIGINT NOT NULL,
    customer_id     BIGINT,
    hire_date       DATE NOT NULL,
    end_date        DATE,
    destination     VARCHAR(200),
    trip_purpose    VARCHAR(200) COMMENT 'e.g. Wedding, Corporate, Tour',
    agreed_price    DECIMAL(12,2) NOT NULL COMMENT 'Hire price in TZS',
    deposit_paid    DECIMAL(12,2) NOT NULL DEFAULT 0.00 COMMENT 'Deposit paid in TZS',
    status          VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING, CONFIRMED, IN_PROGRESS, COMPLETED, CANCELLED',
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_booking_owner FOREIGN KEY (owner_id) REFERENCES owner(id),
    CONSTRAINT fk_booking_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicle(id),
    CONSTRAINT fk_booking_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED')),
    CONSTRAINT chk_booking_dates CHECK (end_date IS NULL OR end_date >= hire_date),
    CONSTRAINT chk_booking_price CHECK (agreed_price > 0),
    CONSTRAINT chk_booking_deposit CHECK (deposit_paid >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
