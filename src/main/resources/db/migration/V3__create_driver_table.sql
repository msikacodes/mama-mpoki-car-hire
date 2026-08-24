-- V3: Create driver table
-- Mama Mpoki Car Hire - Driver management

CREATE TABLE driver (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    license_number  VARCHAR(50),
    license_expiry  DATE,
    national_id     VARCHAR(50),
    address         VARCHAR(200),
    daily_rate      DECIMAL(10,2) COMMENT 'Default daily allowance in TZS',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE, ON_LEAVE',
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_driver_owner FOREIGN KEY (owner_id) REFERENCES owner(id),
    CONSTRAINT chk_driver_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'ON_LEAVE'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
