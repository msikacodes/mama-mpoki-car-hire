-- V17: Create indexes
-- Mama Mpoki Car Hire - Performance indexes for all tables

-- Vehicle indexes
CREATE INDEX idx_vehicle_owner ON vehicle(owner_id);
CREATE INDEX idx_vehicle_module ON vehicle(module_type);
CREATE INDEX idx_vehicle_status ON vehicle(status);
CREATE INDEX idx_vehicle_deleted ON vehicle(deleted);
CREATE INDEX idx_vehicle_owner_module ON vehicle(owner_id, module_type);
CREATE INDEX idx_vehicle_owner_status ON vehicle(owner_id, status);

-- Driver indexes
CREATE INDEX idx_driver_owner ON driver(owner_id);
CREATE INDEX idx_driver_status ON driver(status);
CREATE INDEX idx_driver_deleted ON driver(deleted);

-- Conductor indexes
CREATE INDEX idx_conductor_owner ON conductor(owner_id);
CREATE INDEX idx_conductor_status ON conductor(status);
CREATE INDEX idx_conductor_deleted ON conductor(deleted);

-- Customer indexes
CREATE INDEX idx_customer_owner ON customer(owner_id);
CREATE INDEX idx_customer_deleted ON customer(customer_id);

-- Hire booking indexes (critical for availability checks)
CREATE INDEX idx_booking_vehicle ON hire_booking(vehicle_id);
CREATE INDEX idx_booking_customer ON hire_booking(customer_id);
CREATE INDEX idx_booking_owner ON hire_booking(owner_id);
CREATE INDEX idx_booking_status ON hire_booking(status);
CREATE INDEX idx_booking_hire_date ON hire_booking(hire_date);
CREATE INDEX idx_booking_end_date ON hire_booking(end_date);
CREATE INDEX idx_booking_deleted ON hire_booking(deleted);
CREATE INDEX idx_booking_vehicle_dates ON hire_booking(vehicle_id, hire_date, end_date);
CREATE INDEX idx_booking_owner_status ON hire_booking(owner_id, status);

-- Trip indexes
CREATE INDEX idx_trip_booking ON trip(booking_id);
CREATE INDEX idx_trip_driver ON trip(driver_id);
CREATE INDEX idx_trip_vehicle ON trip(vehicle_id);
CREATE INDEX idx_trip_status ON trip(status);
CREATE INDEX idx_trip_deleted ON trip(deleted);
CREATE INDEX idx_trip_vehicle_status ON trip(vehicle_id, status);

-- Trip expense indexes
CREATE INDEX idx_trip_expense_trip ON trip_expense(trip_id);
CREATE INDEX idx_trip_expense_deleted ON trip_expense(deleted);

-- Payment indexes
CREATE INDEX idx_payment_booking ON payment(booking_id);
CREATE INDEX idx_payment_deleted ON payment(deleted);
CREATE INDEX idx_payment_booking_deleted ON payment(booking_id, deleted);

-- Route indexes
CREATE INDEX idx_route_owner ON route(owner_id);
CREATE INDEX idx_route_status ON route(status);
CREATE INDEX idx_route_deleted ON route(deleted);

-- Daily operation indexes
CREATE INDEX idx_daily_op_vehicle ON daily_operation(vehicle_id);
CREATE INDEX idx_daily_op_route ON daily_operation(route_id);
CREATE INDEX idx_daily_op_driver ON daily_operation(driver_id);
CREATE INDEX idx_daily_op_conductor ON daily_operation(conductor_id);
CREATE INDEX idx_daily_op_date ON daily_operation(operation_date);
CREATE INDEX idx_daily_op_status ON daily_operation(status);
CREATE INDEX idx_daily_op_deleted ON daily_operation(deleted);
CREATE INDEX idx_daily_op_vehicle_date ON daily_operation(vehicle_id, operation_date);
CREATE INDEX idx_daily_op_route_date ON daily_operation(route_id, operation_date);

-- Daily revenue indexes
CREATE INDEX idx_daily_rev_operation ON daily_revenue(operation_id);
CREATE INDEX idx_daily_rev_deleted ON daily_revenue(deleted);
CREATE INDEX idx_daily_rev_date ON daily_revenue(revenue_date);

-- Daily expense indexes
CREATE INDEX idx_daily_exp_operation ON daily_expense(operation_id);
CREATE INDEX idx_daily_exp_deleted ON daily_expense(deleted);
CREATE INDEX idx_daily_exp_date ON daily_expense(expense_date);

-- Fuel record indexes
CREATE INDEX idx_fuel_vehicle ON fuel_record(vehicle_id);
CREATE INDEX idx_fuel_date ON fuel_record(fuel_date);
CREATE INDEX idx_fuel_deleted ON fuel_record(deleted);
CREATE INDEX idx_fuel_vehicle_date ON fuel_record(vehicle_id, fuel_date);

-- Maintenance record indexes
CREATE INDEX idx_maintenance_vehicle ON maintenance_record(vehicle_id);
CREATE INDEX idx_maintenance_deleted ON maintenance_record(deleted);
CREATE INDEX idx_maintenance_next_service ON maintenance_record(next_service_date);

-- Private car indexes
CREATE INDEX idx_private_car_vehicle ON private_car(vehicle_id);
CREATE INDEX idx_private_car_deleted ON private_car(deleted);
CREATE INDEX idx_private_car_insurance_expiry ON private_car(insurance_expiry);
CREATE INDEX idx_private_car_registration_expiry ON private_car(registration_expiry);
