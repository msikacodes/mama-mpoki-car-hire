-- V5: Create customer table
-- Mama Mpoki Car Hire - Special hire customer management

CREATE TABLE customer (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    full_name       VARCHAR(100) NOT NULL,
    phone           VARCHAR(20) NOT NULL,
    email           VARCHAR(100),
    address         VARCHAR(200),
    id_type         VARCHAR(20) COMMENT 'NATIONAL_ID, PASSPORT, DRIVING_LICENSE, OTHER',
    id_number       VARCHAR(50),
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_customer_owner FOREIGN KEY (owner_id) REFERENCES owner(id),
    CONSTRAINT chk_customer_id_type CHECK (id_type IS NULL OR id_type IN ('NATIONAL_ID', 'PASSPORT', 'DRIVING_LICENSE', 'OTHER'))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
