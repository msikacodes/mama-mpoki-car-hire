package com.mamampoki.carhire.dashboard;

import com.mamampoki.carhire.common.enums.*;
import com.mamampoki.carhire.daladala.DailyOperationRepository;
import com.mamampoki.carhire.daladala.RouteRepository;
import com.mamampoki.carhire.dashboard.dto.*;
import com.mamampoki.carhire.fuel.FuelRecordRepository;
import com.mamampoki.carhire.maintenance.MaintenanceRecordRepository;
import com.mamampoki.carhire.privatecar.PrivateCarRepository;
import com.mamampoki.carhire.specialhire.HireBookingRepository;
import com.mamampoki.carhire.specialhire.Payment;
import com.mamampoki.carhire.specialhire.TripRepository;
import com.mamampoki.carhire.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final VehicleRepository vehicleRepository;
    private final HireBookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final DailyOperationRepository operationRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final PrivateCarRepository privateCarRepository;

    public DashboardSummary getDashboardSummary(Long ownerId) {
        return DashboardSummary.builder()
                .fleet(getFleetSummary(ownerId))
                .specialHire(getSpecialHireSummary(ownerId))
                .daladala(getDaladalaSummary(ownerId))
                .alerts(getAlerts(ownerId))
                .currency("TZS")
                .build();
    }

    private FleetSummary getFleetSummary(Long ownerId) {
        long total = vehicleRepository.countByOwnerIdAndDeletedFalse(ownerId);
        long active = vehicleRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, VehicleStatus.ACTIVE);
        long maintenance = vehicleRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, VehicleStatus.MAINTENANCE);
        long inactive = vehicleRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, VehicleStatus.INACTIVE);
        long specialHire = vehicleRepository.countByOwnerIdAndModuleTypeAndDeletedFalse(ownerId, ModuleType.SPECIAL_HIRE);
        long daladala = vehicleRepository.countByOwnerIdAndModuleTypeAndDeletedFalse(ownerId, ModuleType.DALADALA);
        long privateCars = vehicleRepository.countByOwnerIdAndModuleTypeAndDeletedFalse(ownerId, ModuleType.PRIVATE);

        return FleetSummary.builder()
                .totalVehicles(total)
                .activeVehicles(active)
                .inMaintenance(maintenance)
                .inactive(inactive)
                .specialHire(specialHire)
                .daladala(daladala)
                .privateCars(privateCars)
                .build();
    }

    private SpecialHireSummary getSpecialHireSummary(Long ownerId) {
        long pendingBookings = bookingRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, BookingStatus.PENDING);
        long totalBookings = bookingRepository.countByOwnerIdAndDeletedFalse(ownerId);

        // Get current month stats
        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = LocalDate.now();

        // Monthly revenue from payments
        BigDecimal monthlyRevenue = BigDecimal.ZERO;

        // Monthly expenses from trip expenses
        BigDecimal monthlyExpenses = BigDecimal.ZERO;

        return SpecialHireSummary.builder()
                .pendingBookings(pendingBookings)
                .activeTrips(0)
                .totalBookings(totalBookings)
                .monthlyRevenue(monthlyRevenue)
                .monthlyExpenses(monthlyExpenses)
                .monthlyProfit(monthlyRevenue.subtract(monthlyExpenses))
                .build();
    }

    private DaladalaSummary getDaladalaSummary(Long ownerId) {
        long totalRoutes = routeRepository.countByOwnerIdAndDeletedFalse(ownerId);
        long activeRoutes = routeRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, RouteStatus.ACTIVE);
        long todayOperations = operationRepository.countByVehicleOwnerIdAndOperationDateAndDeletedFalse(ownerId, LocalDate.now());
        long monthlyOperations = operationRepository.countByVehicleOwnerIdAndDeletedFalse(ownerId);

        return DaladalaSummary.builder()
                .totalRoutes(totalRoutes)
                .activeRoutes(activeRoutes)
                .todayOperations(todayOperations)
                .monthlyOperations(monthlyOperations)
                .monthlyRevenue(BigDecimal.ZERO)
                .monthlyExpenses(BigDecimal.ZERO)
                .monthlyProfit(BigDecimal.ZERO)
                .build();
    }

    private List<Alert> getAlerts(Long ownerId) {
        List<Alert> alerts = new ArrayList<>();

        // Check for expiring insurance (next 30 days)
        LocalDate insuranceCutoff = LocalDate.now().plusDays(30);
        List<com.mamampoki.carhire.privatecar.PrivateCar> expiringInsurance =
                privateCarRepository.findWithExpiringInsurance(ownerId, insuranceCutoff);

        for (com.mamampoki.carhire.privatecar.PrivateCar pc : expiringInsurance) {
            long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), pc.getInsuranceExpiry());
            alerts.add(Alert.builder()
                    .type("INSURANCE_EXPIRY")
                    .severity(daysUntilExpiry <= 7 ? "HIGH" : "MEDIUM")
                    .message(String.format("Insurance expires in %d days (%s)",
                            daysUntilExpiry, pc.getInsuranceExpiry()))
                    .vehicleId(pc.getVehicle().getId())
                    .vehicleRegNumber(pc.getVehicle().getRegNumber())
                    .build());
        }

        // Check for expiring registration (next 30 days)
        LocalDate registrationCutoff = LocalDate.now().plusDays(30);
        List<com.mamampoki.carhire.privatecar.PrivateCar> expiringRegistration =
                privateCarRepository.findWithExpiringRegistration(ownerId, registrationCutoff);

        for (com.mamampoki.carhire.privatecar.PrivateCar pc : expiringRegistration) {
            long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), pc.getRegistrationExpiry());
            alerts.add(Alert.builder()
                    .type("REGISTRATION_EXPIRY")
                    .severity(daysUntilExpiry <= 7 ? "HIGH" : "MEDIUM")
                    .message(String.format("Registration expires in %d days (%s)",
                            daysUntilExpiry, pc.getRegistrationExpiry()))
                    .vehicleId(pc.getVehicle().getId())
                    .vehicleRegNumber(pc.getVehicle().getRegNumber())
                    .build());
        }

        // Check for upcoming maintenance (next 30 days)
        LocalDate maintenanceCutoff = LocalDate.now().plusDays(30);
        List<com.mamampoki.carhire.maintenance.MaintenanceRecord> upcomingMaintenance =
                maintenanceRecordRepository.findUpcomingMaintenance(ownerId, maintenanceCutoff);

        for (com.mamampoki.carhire.maintenance.MaintenanceRecord mr : upcomingMaintenance) {
            long daysUntilService = java.time.temporal.ChronoUnit.DAYS.between(
                    LocalDate.now(), mr.getNextServiceDate());
            alerts.add(Alert.builder()
                    .type("MAINTENANCE_DUE")
                    .severity(daysUntilService <= 7 ? "HIGH" : "MEDIUM")
                    .message(String.format("Service due in %d days (%s)",
                            daysUntilService, mr.getNextServiceDate()))
                    .vehicleId(mr.getVehicle().getId())
                    .vehicleRegNumber(mr.getVehicle().getRegNumber())
                    .build());
        }

        // Check for pending bookings
        long pendingBookings = bookingRepository.countByOwnerIdAndStatusAndDeletedFalse(ownerId, BookingStatus.PENDING);
        if (pendingBookings > 0) {
            alerts.add(Alert.builder()
                    .type("PENDING_BOOKINGS")
                    .severity("INFO")
                    .message(String.format("You have %d pending booking(s)", pendingBookings))
                    .build());
        }

        return alerts;
    }
}
