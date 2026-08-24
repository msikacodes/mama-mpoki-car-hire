-- V10: Create route table
-- Mama Mpoki Car Hire - Daladala route management

CREATE TABLE route (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    owner_id        BIGINT NOT NULL,
    name            VARCHAR(100) NOT NULL COMMENT 'e.g. Dodoma Town - Ihumwa',
    start_point     VARCHAR(100) NOT NULL,
    end_point       VARCHAR(100) NOT NULL,
    distance_km     DECIMAL(6,2),
    fare_amount     DECIMAL(8,2) NOT NULL COMMENT 'Standard fare per passenger in TZS',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE, INACTIVE',
    notes           TEXT,
    deleted         BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at      TIMESTAMP NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_route_owner FOREIGN KEY (owner_id) REFERENCES owner(id),
    CONSTRAINT chk_route_status CHECK (status IN ('ACTIVE', 'INACTIVE')),
    CONSTRAINT chk_route_fare CHECK (fare_amount > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
