package com.mamampoki.carhire.integration;

import com.mamampoki.carhire.auth.AuthRequest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Vehicle Integration Tests")
public class VehicleIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static String testToken;
    private static Long createdVehicleId;

    @BeforeAll
    static void setup(@Autowired MockMvc mockMvc, @Autowired ObjectMapper objectMapper) throws Exception {
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
    }

    @Test
    @Order(1)
    @DisplayName("Create a new vehicle")
    void createVehicle() throws Exception {
        VehicleRequest request = new VehicleRequest();
        request.setRegNumber("T TEST 001");
        request.setMake("Toyota");
        request.setModel("HiAce");
        request.setYear(2024);
        request.setColor("White");
        request.setModuleType(com.mamampoki.carhire.common.enums.ModuleType.SPECIAL_HIRE);
        request.setVehicleType(com.mamampoki.carhire.common.enums.VehicleType.COASTER);
        request.setFuelType(com.mamampoki.carhire.common.enums.FuelType.DIESEL);
        request.setCapacity(30);

        MvcResult result = mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.regNumber").value("T TEST 001"))
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        createdVehicleId = objectMapper.readTree(responseBody).path("data").path("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("Get vehicle by ID")
    void getVehicleById() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles/" + createdVehicleId)
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.regNumber").value("T TEST 001"));
    }

    @Test
    @Order(3)
    @DisplayName("List vehicles with pagination")
    void listVehicles() throws Exception {
        mockMvc.perform(get("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + testToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("Update vehicle")
    void updateVehicle() throws Exception {
        VehicleRequest request = new VehicleRequest();
        request.setRegNumber("T TEST 001");
        request.setMake("Toyota");
        request.setModel("HiAce");
        request.setYear(2024);
        request.setColor("Silver");
        request.setModuleType(com.mamampoki.carhire.common.enums.ModuleType.SPECIAL_HIRE);
        request.setVehicleType(com.mamampoki.carhire.common.enums.VehicleType.COASTER);
        request.setFuelType(com.mamampoki.carhire.common.enums.FuelType.DIESEL);
        request.setCapacity(30);

        mockMvc.perform(put("/api/v1/vehicles/" + createdVehicleId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.color").value("Silver"));
    }

    @Test
    @Order(5)
    @DisplayName("Delete vehicle")
    void deleteVehicle() throws Exception {
        mockMvc.perform(delete("/api/v1/vehicles/" + createdVehicleId)
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @Order(6)
    @DisplayName("Create vehicle with duplicate reg number fails")
    void createDuplicateVehicle() throws Exception {
        // Create first vehicle
        VehicleRequest request1 = new VehicleRequest();
        request1.setRegNumber("T DUP 001");
        request1.setMake("Toyota");
        request1.setModel("HiAce");
        request1.setYear(2024);
        request1.setModuleType(com.mamampoki.carhire.common.enums.ModuleType.SPECIAL_HIRE);
        request1.setVehicleType(com.mamampoki.carhire.common.enums.VehicleType.COASTER);

        mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Try to create duplicate
        VehicleRequest request2 = new VehicleRequest();
        request2.setRegNumber("T DUP 001");
        request2.setMake("Nissan");
        request2.setModel("Urvan");
        request2.setYear(2023);
        request2.setModuleType(com.mamampoki.carhire.common.enums.ModuleType.SPECIAL_HIRE);
        request2.setVehicleType(com.mamampoki.carhire.common.enums.VehicleType.MINIBUS);

        mockMvc.perform(post("/api/v1/vehicles")
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }
}
