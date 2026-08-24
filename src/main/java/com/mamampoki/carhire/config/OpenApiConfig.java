package com.mamampoki.carhire.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI mamaMpokiOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mama Mpoki Car Hire API")
                        .description("""
                                Fleet management system for Mama Mpoki Car Hire - Dodoma, Tanzania.
                                
                                ## Modules
                                - **Authentication** - Owner login, token refresh, password change
                                - **Fleet Management** - Vehicles, Drivers, Conductors, Customers
                                - **Special Hire** - Bookings, Trips, Trip Expenses, Payments
                                - **Daladala** - Routes, Daily Operations, Revenue, Expenses
                                - **Private Cars** - Registration, Insurance, Fuel, Maintenance
                                - **Dashboard** - Summary statistics and alerts
                                - **Reports** - Special hire, Daladala, Monthly, Quarterly, Vehicle profitability
                                
                                ## Authentication
                                All endpoints (except login and health check) require a Bearer JWT token.
                                Use `/api/v1/auth/login` to obtain an access token.
                                
                                ## Currency
                                All monetary values are in Tanzanian Shillings (TZS).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Mama Mpoki Car Hire")
                                .email("info@mamampoki.co.tz")
                                .url("https://mamampoki.co.tz"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://mamampoki.co.tz/license")))
                .tags(List.of(
                        new Tag().name("Authentication").description("Owner login and token management"),
                        new Tag().name("Vehicles").description("Fleet vehicle management"),
                        new Tag().name("Drivers").description("Driver management"),
                        new Tag().name("Conductors").description("Conductor management"),
                        new Tag().name("Customers").description("Customer/hirer management"),
                        new Tag().name("Special Hire").description("Special hire bookings, trips, and payments"),
                        new Tag().name("Daladala").description("Daladala routes and daily operations"),
                        new Tag().name("Private Cars").description("Private car management"),
                        new Tag().name("Dashboard").description("Dashboard summary and alerts"),
                        new Tag().name("Reports").description("Financial and operational reports")
                ))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT access token from login endpoint")));
    }
}
