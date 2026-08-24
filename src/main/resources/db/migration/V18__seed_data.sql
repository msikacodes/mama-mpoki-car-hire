-- V18: Seed data
-- Mama Mpoki Car Hire - Initial data setup

-- Insert default owner
-- Password: "MamaMpoki2026!" (BCrypt hashed)
-- IMPORTANT: Change this password after first login!
INSERT INTO owner (username, password, full_name, phone, email)
VALUES (
    'mamampoki',
    '$2a$12$LJ3m4ys3Lz0wqV9rQ5kZYOeQZ8z9eN3xG2VH5yT8cR6bW4sK2mXy',
    'Mama Mpoki',
    '+255XXXXXXXXX',
    'info@mamampoki.co.tz'
);

-- Insert sample routes for Daladala operations
INSERT INTO route (owner_id, name, start_point, end_point, distance_km, fare_amount, status)
VALUES
    (1, 'Dodoma Town - Ihumwa', 'Dodoma Town Centre', 'Ihumwa', 25.50, 1500.00, 'ACTIVE'),
    (1, 'Dodoma Town - Kondoa', 'Dodoma Town Centre', 'Kondoa', 85.00, 5000.00, 'ACTIVE'),
    (1, 'Dodoma Town - Mpwapwa', 'Dodoma Town Centre', 'Mpwapwa', 120.00, 7000.00, 'ACTIVE'),
    (1, 'Dodoma Town - Bahi', 'Dodoma Town Centre', 'Bahi', 40.00, 2500.00, 'ACTIVE');

-- Insert sample drivers
INSERT INTO driver (owner_id, full_name, phone, license_number, license_expiry, national_id, address, daily_rate, status)
VALUES
    (1, 'John Mwakasege', '+255712345678', 'TZ-LIC-2024-001', '2027-12-31', 'TZ-NID-12345678', 'Dodoma, Chang''ombe', 30000.00, 'ACTIVE'),
    (1, 'Peter Kimaro', '+255723456789', 'TZ-LIC-2024-002', '2027-06-30', 'TZ-NID-23456789', 'Dodoma, Nala', 30000.00, 'ACTIVE'),
    (1, 'Hassan Mweta', '+255734567890', 'TZ-LIC-2024-003', '2028-03-31', 'TZ-NID-34567890', 'Dodoma, Majengo', 35000.00, 'ACTIVE');

-- Insert sample conductors
INSERT INTO conductor (owner_id, full_name, phone, national_id, address, daily_rate, status)
VALUES
    (1, 'Hamisi Juma', '+255756789012', 'TZ-NID-87654321', 'Dodoma, Nala', 15000.00, 'ACTIVE'),
    (1, 'Amina Rashid', '+255767890123', 'TZ-NID-76543210', 'Dodoma, Majengo', 15000.00, 'ACTIVE');

-- Insert sample vehicles (Special Hire)
INSERT INTO vehicle (owner_id, vehicle_type, module_type, make, model, year, reg_number, color, capacity, fuel_type, status)
VALUES
    (1, 'COASTER', 'SPECIAL_HIRE', 'Toyota', 'HiAce', 2022, 'T 123 ABC', 'White', 30, 'DIESEL', 'ACTIVE'),
    (1, 'MINIBUS', 'SPECIAL_HIRE', 'Toyota', 'HiAce', 2021, 'T 234 DEF', 'Silver', 16, 'DIESEL', 'ACTIVE'),
    (1, 'COASTER', 'SPECIAL_HIRE', 'Nissan', 'Civilian', 2023, 'T 345 GHI', 'White', 28, 'DIESEL', 'ACTIVE');

-- Insert sample vehicles (Daladala)
INSERT INTO vehicle (owner_id, vehicle_type, module_type, make, model, year, reg_number, color, capacity, fuel_type, status)
VALUES
    (1, 'DALADALA_BUS', 'DALADALA', 'Toyota', 'HiAce', 2020, 'T 456 JKL', 'Blue', 16, 'DIESEL', 'ACTIVE'),
    (1, 'DALADALA_BUS', 'DALADALA', 'Nissan', 'Urvan', 2019, 'T 567 MNO', 'Green', 14, 'DIESEL', 'ACTIVE'),
    (1, 'DALADALA_BUS', 'DALADALA', 'Toyota', 'HiAce', 2021, 'T 678 PQR', 'Blue', 16, 'DIESEL', 'ACTIVE');

-- Insert sample vehicle (Private Car)
INSERT INTO vehicle (owner_id, vehicle_type, module_type, make, model, year, reg_number, color, capacity, fuel_type, status)
VALUES
    (1, 'PRIVATE_CAR', 'PRIVATE', 'Toyota', 'Land Cruiser', 2023, 'T 789 STU', 'Black', 7, 'DIESEL', 'ACTIVE');

-- Insert private car extended info
INSERT INTO private_car (vehicle_id, insurance_number, insurance_provider, insurance_expiry, registration_expiry, last_service_date)
VALUES
    (7, 'INS-2026-001', 'APA Insurance', '2027-03-31', '2027-06-30', '2026-08-01');
