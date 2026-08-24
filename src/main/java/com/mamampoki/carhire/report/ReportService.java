package com.mamampoki.carhire.report;

import com.mamampoki.carhire.common.enums.ModuleType;
import com.mamampoki.carhire.daladala.DailyOperationRepository;
import com.mamampoki.carhire.daladala.DailyOperation;
import com.mamampoki.carhire.fuel.FuelRecordRepository;
import com.mamampoki.carhire.maintenance.MaintenanceRecordRepository;
import com.mamampoki.carhire.report.dto.*;
import com.mamampoki.carhire.specialhire.HireBookingRepository;
import com.mamampoki.carhire.specialhire.TripRepository;
import com.mamampoki.carhire.vehicle.Vehicle;
import com.mamampoki.carhire.vehicle.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final HireBookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final DailyOperationRepository operationRepository;
    private final VehicleRepository vehicleRepository;
    private final FuelRecordRepository fuelRecordRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;

    public SpecialHireReport getSpecialHireReport(Long ownerId, LocalDate fromDate, LocalDate toDate) {
        long totalBookings = bookingRepository.countByOwnerIdAndDeletedFalse(ownerId);

        return SpecialHireReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalBookings(totalBookings)
                .completedTrips(0)
                .cancelledBookings(0)
                .totalRevenue(BigDecimal.ZERO)
                .totalExpenses(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .averageBookingValue(BigDecimal.ZERO)
                .currency("TZS")
                .build();
    }

    public DaladalaReport getDaladalaReport(Long ownerId, LocalDate fromDate, LocalDate toDate) {
        long totalOperations = operationRepository.countByVehicleOwnerIdAndDeletedFalse(ownerId);

        return DaladalaReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalOperations(totalOperations)
                .completedOperations(0)
                .totalRevenue(BigDecimal.ZERO)
                .totalExpenses(BigDecimal.ZERO)
                .totalProfit(BigDecimal.ZERO)
                .profitMargin(BigDecimal.ZERO)
                .averageDailyRevenue(BigDecimal.ZERO)
                .averageDailyExpenses(BigDecimal.ZERO)
                .totalPassengers(0)
                .routePerformance(new ArrayList<>())
                .vehiclePerformance(new ArrayList<>())
                .currency("TZS")
                .build();
    }

    public VehicleProfitabilityReport getVehicleProfitabilityReport(Long ownerId) {
        List<Vehicle> vehicles = vehicleRepository.findByOwnerIdAndDeletedFalse(ownerId);
        List<VehicleProfitabilityReport.VehicleProfitability> vehicleList = new ArrayList<>();

        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalFuelCost = BigDecimal.ZERO;
        BigDecimal totalMaintenanceCost = BigDecimal.ZERO;

        for (Vehicle vehicle : vehicles) {
            BigDecimal fuelCost = fuelRecordRepository.sumTotalCostByVehicleId(vehicle.getId());
            BigDecimal maintenanceCost = maintenanceRecordRepository.sumCostByVehicleId(vehicle.getId());

            if (fuelCost == null) fuelCost = BigDecimal.ZERO;
            if (maintenanceCost == null) maintenanceCost = BigDecimal.ZERO;

            BigDecimal totalExpenses = fuelCost.add(maintenanceCost);

            vehicleList.add(VehicleProfitabilityReport.VehicleProfitability.builder()
                    .vehicleId(vehicle.getId())
                    .regNumber(vehicle.getRegNumber())
                    .make(vehicle.getMake())
                    .model(vehicle.getModel())
                    .moduleType(vehicle.getModuleType().name())
                    .revenue(BigDecimal.ZERO)
                    .fuelCost(fuelCost)
                    .maintenanceCost(maintenanceCost)
                    .totalExpenses(totalExpenses)
                    .profit(BigDecimal.ZERO.negate())
                    .profitMargin(BigDecimal.ZERO)
                    .build());

            totalFuelCost = totalFuelCost.add(fuelCost);
            totalMaintenanceCost = totalMaintenanceCost.add(maintenanceCost);
        }

        return VehicleProfitabilityReport.builder()
                .vehicles(vehicleList)
                .totalRevenue(totalRevenue)
                .totalExpenses(totalFuelCost.add(totalMaintenanceCost))
                .totalProfit(totalRevenue.subtract(totalFuelCost.add(totalMaintenanceCost)))
                .currency("TZS")
                .build();
    }

    public ExpenseReport getExpenseReport(Long ownerId, LocalDate fromDate, LocalDate toDate) {
        BigDecimal totalFuelCost = fuelRecordRepository.sumTotalCostByOwnerId(ownerId);
        BigDecimal totalMaintenanceCost = maintenanceRecordRepository.sumCostByOwnerId(ownerId);

        if (totalFuelCost == null) totalFuelCost = BigDecimal.ZERO;
        if (totalMaintenanceCost == null) totalMaintenanceCost = BigDecimal.ZERO;

        Map<String, BigDecimal> byCategory = new HashMap<>();
        byCategory.put("Fuel", totalFuelCost);
        byCategory.put("Maintenance", totalMaintenanceCost);
        byCategory.put("Total", totalFuelCost.add(totalMaintenanceCost));

        List<ExpenseReport.ExpenseDetail> topExpenses = new ArrayList<>();
        topExpenses.add(ExpenseReport.ExpenseDetail.builder()
                .category("Fuel")
                .amount(totalFuelCost)
                .count(fuelRecordRepository.findByVehicleOwnerIdAndDeletedFalse(ownerId).size())
                .build());
        topExpenses.add(ExpenseReport.ExpenseDetail.builder()
                .category("Maintenance")
                .amount(totalMaintenanceCost)
                .count(maintenanceRecordRepository.findByVehicleOwnerIdAndDeletedFalse(ownerId).size())
                .build());

        return ExpenseReport.builder()
                .fromDate(fromDate)
                .toDate(toDate)
                .totalExpenses(totalFuelCost.add(totalMaintenanceCost))
                .byCategory(byCategory)
                .topExpenses(topExpenses)
                .currency("TZS")
                .build();
    }

    public MonthlySummary getMonthlySummary(Long ownerId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);

        return MonthlySummary.builder()
                .year(year)
                .month(month)
                .specialHire(MonthlySummary.ModuleSummary.builder()
                        .revenue(BigDecimal.ZERO)
                        .expenses(BigDecimal.ZERO)
                        .profit(BigDecimal.ZERO)
                        .build())
                .daladala(MonthlySummary.ModuleSummary.builder()
                        .revenue(BigDecimal.ZERO)
                        .expenses(BigDecimal.ZERO)
                        .profit(BigDecimal.ZERO)
                        .build())
                .privateCars(MonthlySummary.ModuleSummary.builder()
                        .revenue(BigDecimal.ZERO)
                        .expenses(BigDecimal.ZERO)
                        .profit(BigDecimal.ZERO)
                        .build())
                .totalRevenue(BigDecimal.ZERO)
                .totalExpenses(BigDecimal.ZERO)
                .netProfit(BigDecimal.ZERO)
                .currency("TZS")
                .build();
    }

    public MonthlySummary getQuarterlySummary(Long ownerId, int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        int endMonth = startMonth + 2;

        return MonthlySummary.builder()
                .year(year)
                .month(quarter)
                .specialHire(MonthlySummary.ModuleSummary.builder()
                        .revenue(BigDecimal.ZERO)
                        .expenses(BigDecimal.ZERO)
                        .profit(BigDecimal.ZERO)
                        .build())
                .daladala(MonthlySummary.ModuleSummary.builder()
                        .revenue(BigDecimal.ZERO)
                        .expenses(BigDecimal.ZERO)
                        .profit(BigDecimal.ZERO)
                        .build())
                .privateCars(MonthlySummary.ModuleSummary.builder()
                        .revenue(BigDecimal.ZERO)
                        .expenses(BigDecimal.ZERO)
                        .profit(BigDecimal.ZERO)
                        .build())
                .totalRevenue(BigDecimal.ZERO)
                .totalExpenses(BigDecimal.ZERO)
                .netProfit(BigDecimal.ZERO)
                .currency("TZS")
                .build();
    }
}
