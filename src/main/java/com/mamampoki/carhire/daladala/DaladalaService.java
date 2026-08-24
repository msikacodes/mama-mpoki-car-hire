package com.mamampoki.carhire.daladala;

import com.mamampoki.carhire.common.enums.TripStatus;
import com.mamampoki.carhire.conductor.Conductor;
import com.mamampoki.carhire.conductor.ConductorRepository;
import com.mamampoki.carhire.driver.Driver;
import com.mamampoki.carhire.driver.DriverRepository;
import com.mamampoki.carhire.exception.ResourceNotFoundException;
import com.mamampoki.carhire.owner.Owner;
import com.mamampoki.carhire.owner.OwnerRepository;
import com.mamampoki.carhire.daladala.dto.*;
import com.mamampoki.carhire.vehicle.Vehicle;
import com.mamampoki.carhire.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DaladalaService {

    private final RouteRepository routeRepository;
    private final DailyOperationRepository operationRepository;
    private final DailyRevenueRepository revenueRepository;
    private final DailyExpenseRepository expenseRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final ConductorRepository conductorRepository;
    private final OwnerRepository ownerRepository;

    // ==================== ROUTES ====================

    public Page<RouteResponse> getRoutes(Long ownerId, Pageable pageable) {
        return routeRepository.findByOwnerIdAndDeletedFalse(ownerId, pageable)
                .map(this::toRouteResponse);
    }

    public RouteResponse getRouteById(Long ownerId, Long routeId) {
        Route route = findRoute(ownerId, routeId);
        return toRouteResponse(route);
    }

    @Transactional
    public RouteResponse createRoute(Long ownerId, RouteRequest request) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner", "id", ownerId));

        Route route = Route.builder()
                .owner(owner)
                .name(request.getName())
                .startPoint(request.getStartPoint())
                .endPoint(request.getEndPoint())
                .distanceKm(request.getDistanceKm())
                .fareAmount(request.getFareAmount())
                .status(com.mamampoki.carhire.common.enums.RouteStatus.ACTIVE)
                .notes(request.getNotes())
                .build();

        route = routeRepository.save(route);
        log.info("Route created: {} - {}", route.getStartPoint(), route.getEndPoint());
        return toRouteResponse(route);
    }

    @Transactional
    public RouteResponse updateRoute(Long ownerId, Long routeId, RouteRequest request) {
        Route route = findRoute(ownerId, routeId);

        route.setName(request.getName());
        route.setStartPoint(request.getStartPoint());
        route.setEndPoint(request.getEndPoint());
        route.setDistanceKm(request.getDistanceKm());
        route.setFareAmount(request.getFareAmount());
        route.setNotes(request.getNotes());

        route = routeRepository.save(route);
        log.info("Route updated: {}", route.getName());
        return toRouteResponse(route);
    }

    @Transactional
    public void deleteRoute(Long ownerId, Long routeId) {
        Route route = findRoute(ownerId, routeId);
        route.softDelete();
        routeRepository.save(route);
        log.info("Route soft-deleted: {}", route.getName());
    }

    // ==================== DAILY OPERATIONS ====================

    public Page<DailyOperationResponse> getOperations(Long ownerId, TripStatus status,
                                                       LocalDate startDate, LocalDate endDate,
                                                       Pageable pageable) {
        Page<DailyOperation> operations;

        if (startDate != null && endDate != null) {
            operations = operationRepository.findByVehicleOwnerIdAndOperationDateBetweenAndDeletedFalse(
                    ownerId, startDate, endDate, pageable);
        } else if (status != null) {
            operations = operationRepository.findByVehicleOwnerIdAndStatusAndDeletedFalse(
                    ownerId, status, pageable);
        } else {
            operations = operationRepository.findByVehicleOwnerIdAndDeletedFalse(ownerId, pageable);
        }

        return operations.map(op -> toOperationResponse(op, ownerId));
    }

    public DailyOperationResponse getOperationById(Long ownerId, Long operationId) {
        DailyOperation operation = findOperation(ownerId, operationId);
        return toOperationResponse(operation, ownerId);
    }

    @Transactional
    public DailyOperationResponse createOperation(Long ownerId, DailyOperationRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));

        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));

        Driver driver = null;
        if (request.getDriverId() != null) {
            driver = driverRepository.findById(request.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));
        }

        Conductor conductor = null;
        if (request.getConductorId() != null) {
            conductor = conductorRepository.findById(request.getConductorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conductor", "id", request.getConductorId()));
        }

        DailyOperation operation = DailyOperation.builder()
                .vehicle(vehicle)
                .route(route)
                .driver(driver)
                .conductor(conductor)
                .operationDate(request.getOperationDate())
                .departureTime(request.getDepartureTime())
                .returnTime(request.getReturnTime())
                .totalPassengers(request.getTotalPassengers() != null ? request.getTotalPassengers() : 0)
                .status(TripStatus.SCHEDULED)
                .notes(request.getNotes())
                .build();

        operation = operationRepository.save(operation);
        log.info("Operation created: route={}, date={}", route.getName(), operation.getOperationDate());
        return toOperationResponse(operation, ownerId);
    }

    @Transactional
    public DailyOperationResponse completeOperation(Long ownerId, Long operationId,
                                                     Integer totalPassengers, LocalTime returnTime) {
        DailyOperation operation = findOperation(ownerId, operationId);
        if (totalPassengers != null) operation.setTotalPassengers(totalPassengers);
        if (returnTime != null) operation.setReturnTime(returnTime);
        operation.setStatus(TripStatus.COMPLETED);
        operation = operationRepository.save(operation);
        log.info("Operation completed: id={}", operation.getId());
        return toOperationResponse(operation, ownerId);
    }

    // ==================== DAILY REVENUE ====================

    public List<DailyRevenueResponse> getRevenues(Long ownerId, Long operationId) {
        DailyOperation operation = findOperation(ownerId, operationId);
        return revenueRepository.findByOperationIdAndDeletedFalse(operation.getId())
                .stream()
                .map(this::toRevenueResponse)
                .toList();
    }

    @Transactional
    public DailyRevenueResponse addRevenue(Long ownerId, Long operationId, DailyRevenueRequest request) {
        DailyOperation operation = findOperation(ownerId, operationId);

        DailyRevenue revenue = DailyRevenue.builder()
                .operation(operation)
                .source(request.getSource())
                .amount(request.getAmount())
                .description(request.getDescription())
                .revenueDate(request.getRevenueDate())
                .build();

        revenue = revenueRepository.save(revenue);
        log.info("Revenue added: operation={}, source={}, amount={}", operation.getId(),
                revenue.getSource(), revenue.getAmount());
        return toRevenueResponse(revenue);
    }

    @Transactional
    public void deleteRevenue(Long ownerId, Long operationId, Long revenueId) {
        DailyOperation operation = findOperation(ownerId, operationId);
        DailyRevenue revenue = revenueRepository.findById(revenueId)
                .orElseThrow(() -> new ResourceNotFoundException("DailyRevenue", "id", revenueId));

        if (!revenue.getOperation().getId().equals(operation.getId())) {
            throw new ResourceNotFoundException("DailyRevenue", "id", revenueId);
        }

        revenue.softDelete();
        revenueRepository.save(revenue);
        log.info("Revenue deleted: id={}", revenueId);
    }

    // ==================== DAILY EXPENSES ====================

    public List<DailyExpenseResponse> getExpenses(Long ownerId, Long operationId) {
        DailyOperation operation = findOperation(ownerId, operationId);
        return expenseRepository.findByOperationIdAndDeletedFalse(operation.getId())
                .stream()
                .map(this::toExpenseResponse)
                .toList();
    }

    @Transactional
    public DailyExpenseResponse addExpense(Long ownerId, Long operationId, DailyExpenseRequest request) {
        DailyOperation operation = findOperation(ownerId, operationId);

        DailyExpense expense = DailyExpense.builder()
                .operation(operation)
                .expenseType(request.getExpenseType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .expenseDate(request.getExpenseDate())
                .build();

        expense = expenseRepository.save(expense);
        log.info("Expense added: operation={}, type={}, amount={}", operation.getId(),
                expense.getExpenseType(), expense.getAmount());
        return toExpenseResponse(expense);
    }

    @Transactional
    public void deleteExpense(Long ownerId, Long operationId, Long expenseId) {
        DailyOperation operation = findOperation(ownerId, operationId);
        DailyExpense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ResourceNotFoundException("DailyExpense", "id", expenseId));

        if (!expense.getOperation().getId().equals(operation.getId())) {
            throw new ResourceNotFoundException("DailyExpense", "id", expenseId);
        }

        expense.softDelete();
        expenseRepository.save(expense);
        log.info("Expense deleted: id={}", expenseId);
    }

    // ==================== HELPERS ====================

    private Route findRoute(Long ownerId, Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", routeId));
        if (!route.getOwner().getId().equals(ownerId) || route.isDeleted()) {
            throw new ResourceNotFoundException("Route", "id", routeId);
        }
        return route;
    }

    private DailyOperation findOperation(Long ownerId, Long operationId) {
        DailyOperation operation = operationRepository.findById(operationId)
                .orElseThrow(() -> new ResourceNotFoundException("DailyOperation", "id", operationId));
        if (!operation.getVehicle().getOwner().getId().equals(ownerId) || operation.isDeleted()) {
            throw new ResourceNotFoundException("DailyOperation", "id", operationId);
        }
        return operation;
    }

    private RouteResponse toRouteResponse(Route route) {
        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .startPoint(route.getStartPoint())
                .endPoint(route.getEndPoint())
                .distanceKm(route.getDistanceKm())
                .fareAmount(route.getFareAmount())
                .status(route.getStatus())
                .notes(route.getNotes())
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }

    private DailyOperationResponse toOperationResponse(DailyOperation op, Long ownerId) {
        BigDecimal totalRevenue = operationRepository.sumRevenuesByOperationId(op.getId());
        BigDecimal totalExpenses = operationRepository.sumExpensesByOperationId(op.getId());
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;

        return DailyOperationResponse.builder()
                .id(op.getId())
                .vehicleId(op.getVehicle().getId())
                .vehicleRegNumber(op.getVehicle().getRegNumber())
                .routeId(op.getRoute().getId())
                .routeName(op.getRoute().getName())
                .driverId(op.getDriver() != null ? op.getDriver().getId() : null)
                .driverName(op.getDriver() != null ? op.getDriver().getFullName() : null)
                .conductorId(op.getConductor() != null ? op.getConductor().getId() : null)
                .conductorName(op.getConductor() != null ? op.getConductor().getFullName() : null)
                .operationDate(op.getOperationDate())
                .departureTime(op.getDepartureTime())
                .returnTime(op.getReturnTime())
                .totalPassengers(op.getTotalPassengers())
                .status(op.getStatus())
                .totalRevenue(totalRevenue)
                .totalExpenses(totalExpenses)
                .profit(totalRevenue.subtract(totalExpenses))
                .notes(op.getNotes())
                .createdAt(op.getCreatedAt())
                .updatedAt(op.getUpdatedAt())
                .build();
    }

    private DailyRevenueResponse toRevenueResponse(DailyRevenue revenue) {
        return DailyRevenueResponse.builder()
                .id(revenue.getId())
                .operationId(revenue.getOperation().getId())
                .source(revenue.getSource())
                .amount(revenue.getAmount())
                .description(revenue.getDescription())
                .revenueDate(revenue.getRevenueDate())
                .createdAt(revenue.getCreatedAt())
                .build();
    }

    private DailyExpenseResponse toExpenseResponse(DailyExpense expense) {
        return DailyExpenseResponse.builder()
                .id(expense.getId())
                .operationId(expense.getOperation().getId())
                .expenseType(expense.getExpenseType())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .createdAt(expense.getCreatedAt())
                .build();
    }
}
