package com.mamampoki.carhire.integration;

import com.mamampoki.carhire.auth.AuthRequest;
import com.mamampoki.carhire.common.enums.*;
import com.mamampoki.carhire.conductor.dto.ConductorRequest;
import com.mamampoki.carhire.daladala.dto.*;
import com.mamampoki.carhire.driver.dto.DriverRequest;
import com.mamampoki.carhire.vehicle.dto.VehicleRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Daladala Integration Tests")
public class DaladalaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String testToken;
    private static Long vehicleId;
    private static Long driverId;
    private static Long conductorId;
    private static Long routeId;
    private static Long operationId;

    @BeforeAll
    static void setup(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
        // Login
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("testowner");
        loginRequest.setPassword("TestPass123!");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        testToken = objectMapper.readTree(responseBody).path("data").path("accessToken").asText();

        // Create daladala vehicle
        VehicleRequest vehicleReq = new VehicleRequest();
        vehicleReq.setRegNumber("T DALA 001");
        vehicleReq.setMake("Toyota");
        vehicleReq.setModel("HiAce");
        vehicleReq.setYear(2021);
        vehicleReq.setModuleType(ModuleType.DALADALA);
        vehicleReq.setVehicleType(VehicleType.DALADALA_BUS);
        vehicleReq.setFuelType(FuelType.DIESEL);
        vehicleReq.setCapacity(16);

        MvcResult vehicleResult = mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleReq)))
                .andExpect(status().isCreated())
                .andReturn();

        vehicleId = objectMapper.readTree(vehicleResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // Create driver
        DriverRequest driverReq = new DriverRequest();
        driverReq.setFullName("Daladala Driver");
        driverReq.setPhone("+255700000003");
        driverReq.setLicenseNumber("TZ-LIC-DALA-001");
        driverReq.setLicenseExpiry(LocalDate.of(2027, 12, 31));
        driverReq.setNationalId("TZ-NID-DALA-001");
        driverReq.setAddress("Dodoma");
        driverReq.setDailyRate(BigDecimal.valueOf(25000));

        MvcResult driverResult = mockMvc.perform(post("/api/v1/drivers")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverReq)))
                .andExpect(status().isCreated())
                .andReturn();

        driverId = objectMapper.readTree(driverResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // Create conductor
        ConductorRequest conductorReq = new ConductorRequest();
        conductorReq.setFullName("Daladala Conductor");
        conductorReq.setPhone("+255700000004");
        conductorReq.setNationalId("TZ-NID-COND-001");
        conductorReq.setAddress("Dodoma");
        conductorReq.setDailyRate(BigDecimal.valueOf(15000));

        MvcResult conductorResult = mockMvc.perform(post("/api/v1/conductors")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(conductorReq)))
                .andExpect(status().isCreated())
                .andReturn();

        conductorId = objectMapper.readTree(conductorResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(1)
    @DisplayName("Create daladala route")
    void createRoute() throws Exception {
        RouteRequest request = new RouteRequest();
        request.setName("Dodoma - Ihumwa");
        request.setStartPoint("Dodoma Town Centre");
        request.setEndPoint("Ihumwa");
        request.setDistanceKm(new BigDecimal("25.50"));
        request.setFareAmount(new BigDecimal("1500"));

        MvcResult result = mockMvc.perform(post("/api/v1/daladala/routes")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Dodoma - Ihumwa"))
                .andReturn();

        routeId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("List routes")
    void listRoutes() throws Exception {
        mockMvc.perform(get("/api/v1/daladala/routes")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test
    @Order(3)
    @DisplayName("Create daily operation")
    void createOperation() throws Exception {
        DailyOperationRequest request = new DailyOperationRequest();
        request.setVehicleId(vehicleId);
        request.setRouteId(routeId);
        request.setDriverId(driverId);
        request.setConductorId(conductorId);
        request.setOperationDate(LocalDate.now());
        request.setDepartureTime(LocalTime.of(6, 30));

        MvcResult result = mockMvc.perform(post("/api/v1/daladala/operations")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"))
                .andReturn();

        operationId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(4)
    @DisplayName("Add revenue to operation")
    void addRevenue() throws Exception {
        DailyRevenueRequest request = new DailyRevenueRequest();
        request.setSource(com.mamampoki.carhire.common.enums.DailyRevenueSource.FARE);
        request.setAmount(new BigDecimal("67500"));
        request.setDescription("45 passengers × 1,500 TZS");
        request.setRevenueDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/daladala/operations/" + operationId + "/revenues")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(67500));
    }

    @Test
    @Order(5)
    @DisplayName("Add expenses to operation")
    void addExpenses() throws Exception {
        DailyExpenseRequest fuelExpense = new DailyExpenseRequest();
        fuelExpense.setExpenseType(com.mamampoki.carhire.common.enums.DailyExpenseType.FUEL);
        fuelExpense.setAmount(new BigDecimal("35000"));
        fuelExpense.setDescription("Full tank diesel");
        fuelExpense.setExpenseDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/daladala/operations/" + operationId + "/expenses")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fuelExpense)))
                .andExpect(status().isCreated());

        DailyExpenseRequest tollExpense = new DailyExpenseRequest();
        tollExpense.setExpenseType(com.mamampoki.carhire.common.enums.DailyExpenseType.TOLL);
        tollExpense.setAmount(new BigDecimal("5000"));
        tollExpense.setDescription("Road toll");
        tollExpense.setExpenseDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/daladala/operations/" + operationId + "/expenses")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tollExpense)))
                .andExpect(status().isCreated());

        DailyExpenseRequest allowanceExpense = new DailyExpenseRequest();
        allowanceExpense.setExpenseType(com.mamampoki.carhire.common.enums.DailyExpenseType.CONDUCTOR_ALLOWANCE);
        allowanceExpense.setAmount(new BigDecimal("15000"));
        allowanceExpense.setDescription("Daily allowance");
        allowanceExpense.setExpenseDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/daladala/operations/" + operationId + "/expenses")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allowanceExpense)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(6)
    @DisplayName("Complete operation")
    void completeOperation() throws Exception {
        mockMvc.perform(put("/api/v1/daladala/operations/" + operationId + "/complete")
                        .header("Authorization", "Bearer " + testToken)
                        .param("totalPassengers", "45")
                        .param("returnTime", "17:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @Order(7)
    @DisplayName("Get operation details with financials")
    void getOperationDetails() throws Exception {
        mockMvc.perform(get("/api/v1/daladala/operations/" + operationId)
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"))
                .andExpect(jsonPath("$.data.totalPassengers").value(45));
    }

    @Test
    @Order(8)
    @DisplayName("List operations with date filter")
    void listOperationsWithDateFilter() throws Exception {
        mockMvc.perform(get("/api/v1/daladala/operations")
                        .header("Authorization", "Bearer " + testToken)
                        .param("startDate", LocalDate.now().toString())
                        .param("endDate", LocalDate.now().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }
}
