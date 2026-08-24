package com.mamampoki.carhire.integration;

import com.mamampoki.carhire.auth.AuthRequest;
import com.mamampoki.carhire.common.enums.*;
import com.mamampoki.carhire.customer.dto.CustomerRequest;
import com.mamampoki.carhire.driver.dto.DriverRequest;
import com.mamampoki.carhire.specialhire.dto.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Special Hire Integration Tests")
public class SpecialHireIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String testToken;
    private static Long vehicleId;
    private static Long customerId;
    private static Long driverId;
    private static Long bookingId;
    private static Long tripId;

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

        // Create vehicle
        VehicleRequest vehicleReq = new VehicleRequest();
        vehicleReq.setRegNumber("T HIRE 001");
        vehicleReq.setMake("Toyota");
        vehicleReq.setModel("HiAce");
        vehicleReq.setYear(2024);
        vehicleReq.setModuleType(ModuleType.SPECIAL_HIRE);
        vehicleReq.setVehicleType(VehicleType.COASTER);
        vehicleReq.setCapacity(30);

        MvcResult vehicleResult = mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(vehicleReq)))
                .andExpect(status().isCreated())
                .andReturn();

        vehicleId = objectMapper.readTree(vehicleResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // Create customer
        CustomerRequest customerReq = new CustomerRequest();
        customerReq.setFullName("Test Customer");
        customerReq.setPhone("+255700000001");
        customerReq.setEmail("customer@test.co.tz");
        customerReq.setAddress("Dodoma");

        MvcResult customerResult = mockMvc.perform(post("/api/v1/customers")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerReq)))
                .andExpect(status().isCreated())
                .andReturn();

        customerId = objectMapper.readTree(customerResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();

        // Create driver
        DriverRequest driverReq = new DriverRequest();
        driverReq.setFullName("Test Driver");
        driverReq.setPhone("+255700000002");
        driverReq.setLicenseNumber("TZ-LIC-TEST-001");
        driverReq.setLicenseExpiry(LocalDate.of(2027, 12, 31));
        driverReq.setNationalId("TZ-NID-TEST-001");
        driverReq.setAddress("Dodoma");
        driverReq.setDailyRate(BigDecimal.valueOf(30000));

        MvcResult driverResult = mockMvc.perform(post("/api/v1/drivers")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(driverReq)))
                .andExpect(status().isCreated())
                .andReturn();

        driverId = objectMapper.readTree(driverResult.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(1)
    @DisplayName("Create hire booking")
    void createBooking() throws Exception {
        HireBookingRequest request = new HireBookingRequest();
        request.setVehicleId(vehicleId);
        request.setCustomerId(customerId);
        request.setHireDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(3));
        request.setDestination("Dar es Salaam");
        request.setTripPurpose("Corporate event");
        request.setAgreedPrice(new BigDecimal("1500000"));
        request.setDepositPaid(new BigDecimal("500000"));

        MvcResult result = mockMvc.perform(post("/api/v1/special-hire/bookings")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andReturn();

        bookingId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("Confirm booking")
    void confirmBooking() throws Exception {
        mockMvc.perform(put("/api/v1/special-hire/bookings/" + bookingId + "/status")
                        .header("Authorization", "Bearer " + testToken)
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CONFIRMED"));
    }

    @Test
    @Order(3)
    @DisplayName("Create trip for booking")
    void createTrip() throws Exception {
        TripRequest request = new TripRequest();
        request.setBookingId(bookingId);
        request.setDriverId(driverId);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setDestination("Dar es Salaam");
        request.setActualPrice(new BigDecimal("1500000"));
        request.setOdometerStart(100000);

        MvcResult result = mockMvc.perform(post("/api/v1/special-hire/trips")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("SCHEDULED"))
                .andReturn();

        tripId = objectMapper.readTree(result.getResponse().getContentAsString())
                .path("data").path("id").asLong();
    }

    @Test
    @Order(4)
    @DisplayName("Add trip expenses")
    void addTripExpenses() throws Exception {
        TripExpenseRequest fuelExpense = new TripExpenseRequest();
        fuelExpense.setExpenseType(com.mamampoki.carhire.common.enums.TripExpenseType.FUEL);
        fuelExpense.setAmount(new BigDecimal("300000"));
        fuelExpense.setDescription("Full tank diesel");
        fuelExpense.setExpenseDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/special-hire/trips/" + tripId + "/expenses")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fuelExpense)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(300000));

        TripExpenseRequest allowanceExpense = new TripExpenseRequest();
        allowanceExpense.setExpenseType(com.mamampoki.carhire.common.enums.TripExpenseType.DRIVER_ALLOWANCE);
        allowanceExpense.setAmount(new BigDecimal("75000"));
        allowanceExpense.setDescription("3-day allowance");
        allowanceExpense.setExpenseDate(LocalDate.now());

        mockMvc.perform(post("/api/v1/special-hire/trips/" + tripId + "/expenses")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(allowanceExpense)))
                .andExpect(status().isCreated());
    }

    @Test
    @Order(5)
    @DisplayName("Complete trip")
    void completeTrip() throws Exception {
        TripCompleteRequest request = new TripCompleteRequest();
        request.setOdometerEnd(101500);
        request.setEndDate(LocalDate.now().plusDays(3));

        mockMvc.perform(put("/api/v1/special-hire/trips/" + tripId + "/complete")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @Order(6)
    @DisplayName("Record payment")
    void recordPayment() throws Exception {
        PaymentRequest request = new PaymentRequest();
        request.setAmount(new BigDecimal("1000000"));
        request.setPaymentMethod(PaymentMethod.MOBILE_MONEY);
        request.setPaymentDate(LocalDate.now());
        request.setReferenceNumber("MP260824-TEST");
        request.setNotes("Final payment");

        mockMvc.perform(post("/api/v1/special-hire/bookings/" + bookingId + "/payments")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.amount").value(1000000))
                .andExpect(jsonPath("$.data.paymentMethod").value("MOBILE_MONEY"));
    }

    @Test
    @Order(7)
    @DisplayName("Get booking financials")
    void getBookingFinancials() throws Exception {
        mockMvc.perform(get("/api/v1/special-hire/bookings/" + bookingId + "/financials")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.agreedPrice").value(1500000))
                .andExpect(jsonPath("$.data.totalPaid").value(1000000))
                .andExpect(jsonPath("$.data.outstandingBalance").value(500000));
    }

    @Test
    @Order(8)
    @DisplayName("Get trip expenses")
    void getTripExpenses() throws Exception {
        mockMvc.perform(get("/api/v1/special-hire/trips/" + tripId + "/expenses")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }
}
