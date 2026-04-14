package com.letschange.aot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PlantShopController.class)
@DisplayName("Plant Shop Controller Tests")
class PlantShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @MockitoBean
    private PlantShopService plantShopService;

    private Plant monstera;
    private Plant snakePlant;
    private Plant pothos;
    private List<Plant> allPlants;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        monstera = new Plant(1L, "Monstera Deliciosa", "Monstera deliciosa", 45.99, 25);
        snakePlant = new Plant(2L, "Snake Plant", "Sansevieria trifasciata", 28.50, 40);
        pothos = new Plant(3L, "Pothos", "Epipremnum aureum", 15.99, 60);
        allPlants = Arrays.asList(monstera, snakePlant, pothos);
    }

    @Nested
    @DisplayName("GET Endpoints")
    class GetEndpointsTests {

        @Test
        @DisplayName("Should return all plants")
        void shouldReturnAllPlants() throws Exception {
            when(plantShopService.getAllPlants()).thenReturn(allPlants);

            mockMvc.perform(get("/api/plants"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("Monstera Deliciosa"))
                    .andExpect(jsonPath("$[0].scientificName").value("Monstera deliciosa"))
                    .andExpect(jsonPath("$[0].price").value(45.99))
                    .andExpect(jsonPath("$[0].stockQuantity").value(25))
                    .andExpect(jsonPath("$[1].name").value("Snake Plant"))
                    .andExpect(jsonPath("$[2].name").value("Pothos"));
        }

        @Test
        @DisplayName("Should return plant by ID when found")
        void shouldReturnPlantById() throws Exception {
            when(plantShopService.getPlantById(1L)).thenReturn(Optional.of(monstera));

            mockMvc.perform(get("/api/plants/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Monstera Deliciosa"))
                    .andExpect(jsonPath("$.scientificName").value("Monstera deliciosa"))
                    .andExpect(jsonPath("$.price").value(45.99))
                    .andExpect(jsonPath("$.stockQuantity").value(25));
        }

        @Test
        @DisplayName("Should return 404 when plant not found by ID")
        void shouldReturn404WhenPlantNotFoundById() throws Exception {
            when(plantShopService.getPlantById(999L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/plants/999"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should return plant by name when found")
        void shouldReturnPlantByName() throws Exception {
            when(plantShopService.getPlantByName("Monstera Deliciosa")).thenReturn(Optional.of(monstera));

            mockMvc.perform(get("/api/plants/name/Monstera Deliciosa"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Monstera Deliciosa"))
                    .andExpect(jsonPath("$.price").value(45.99));
        }

        @Test
        @DisplayName("Should return 404 when plant not found by name")
        void shouldReturn404WhenPlantNotFoundByName() throws Exception {
            when(plantShopService.getPlantByName("Nonexistent")).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/plants/name/Nonexistent"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Search Endpoints")
    class SearchEndpointsTests {

        @Test
        @DisplayName("Should search plants by name")
        void shouldSearchPlantsByName() throws Exception {
            List<Plant> searchResults = Arrays.asList(monstera);
            when(plantShopService.searchPlantsByName("Monstera")).thenReturn(searchResults);

            mockMvc.perform(get("/api/plants/search?name=Monstera"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value("Monstera Deliciosa"));
        }

        @Test
        @DisplayName("Should search plants by scientific name")
        void shouldSearchPlantsByScientificName() throws Exception {
            List<Plant> searchResults = Arrays.asList(snakePlant);
            when(plantShopService.searchPlantsByScientificName("trifasciata")).thenReturn(searchResults);

            mockMvc.perform(get("/api/plants/search?scientificName=trifasciata"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].scientificName").value("Sansevieria trifasciata"));
        }

        @Test
        @DisplayName("Should search plants by price range")
        void shouldSearchPlantsByPriceRange() throws Exception {
            List<Plant> searchResults = Arrays.asList(pothos, snakePlant);
            when(plantShopService.searchPlantsByPriceRange(10.0, 30.0)).thenReturn(searchResults);

            mockMvc.perform(get("/api/plants/search?minPrice=10&maxPrice=30"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(2))
                    .andExpect(jsonPath("$[0].price").value(15.99))
                    .andExpect(jsonPath("$[1].price").value(28.50));
        }

        @Test
        @DisplayName("Should return plants in stock only")
        void shouldReturnPlantsInStockOnly() throws Exception {
            when(plantShopService.getPlantsInStock()).thenReturn(allPlants);

            mockMvc.perform(get("/api/plants/search?inStockOnly=true"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3));
        }

        @Test
        @DisplayName("Should return all plants when no search parameters provided")
        void shouldReturnAllPlantsWhenNoSearchParams() throws Exception {
            when(plantShopService.getAllPlants()).thenReturn(allPlants);

            mockMvc.perform(get("/api/plants/search"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$.length()").value(3));
        }
    }

    @Nested
    @DisplayName("POST Endpoint")
    class PostEndpointTests {

        @Test
        @DisplayName("Should create new plant successfully")
        void shouldCreateNewPlant() throws Exception {
            Plant newPlant = new Plant(null, "Golden Pothos", "Epipremnum aureum 'Golden'", 19.99, 35);
            Plant createdPlant = new Plant(4L, "Golden Pothos", "Epipremnum aureum 'Golden'", 19.99, 35);

            when(plantShopService.createPlant(any(Plant.class))).thenReturn(createdPlant);

            mockMvc.perform(post("/api/plants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPlant)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(4))
                    .andExpect(jsonPath("$.name").value("Golden Pothos"))
                    .andExpect(jsonPath("$.scientificName").value("Epipremnum aureum 'Golden'"))
                    .andExpect(jsonPath("$.price").value(19.99))
                    .andExpect(jsonPath("$.stockQuantity").value(35));
        }

        @Test
        @DisplayName("Should return 400 when creating plant with duplicate name")
        void shouldReturn400WhenCreatingPlantWithDuplicateName() throws Exception {
            Plant duplicatePlant = new Plant(null, "Monstera Deliciosa", "Monstera deliciosa duplicate", 40.00, 10);

            when(plantShopService.createPlant(any(Plant.class)))
                    .thenThrow(new IllegalArgumentException("Plant with name 'Monstera Deliciosa' already exists"));

            mockMvc.perform(post("/api/plants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(duplicatePlant)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("PUT Endpoint")
    class PutEndpointTests {

        @Test
        @DisplayName("Should update plant successfully")
        void shouldUpdatePlant() throws Exception {
            Plant updatedPlant = new Plant(1L, "Monstera Deliciosa", "Monstera deliciosa", 49.99, 30);

            when(plantShopService.updatePlant(eq(1L), any(Plant.class))).thenReturn(updatedPlant);

            mockMvc.perform(put("/api/plants/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updatedPlant)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("Monstera Deliciosa"))
                    .andExpect(jsonPath("$.price").value(49.99))
                    .andExpect(jsonPath("$.stockQuantity").value(30));
        }

        @Test
        @DisplayName("Should return 404 when updating non-existent plant")
        void shouldReturn404WhenUpdatingNonExistentPlant() throws Exception {
            Plant nonExistentPlant = new Plant(999L, "Non-existent", "Nonexistus testus", 25.00, 5);

            when(plantShopService.updatePlant(eq(999L), any(Plant.class)))
                    .thenThrow(new IllegalArgumentException("Plant not found with id: 999"));

            mockMvc.perform(put("/api/plants/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(nonExistentPlant)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("PATCH Endpoint")
    class PatchEndpointTests {

        @Test
        @DisplayName("Should update plant stock successfully")
        void shouldUpdatePlantStock() throws Exception {
            Plant updatedPlant = new Plant(1L, "Monstera Deliciosa", "Monstera deliciosa", 45.99, 45);
            String stockUpdateJson = "{\"stockQuantity\":45}";

            when(plantShopService.updatePlantStock(1L, 45)).thenReturn(updatedPlant);

            mockMvc.perform(patch("/api/plants/1/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(stockUpdateJson))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.stockQuantity").value(45));
        }

        @Test
        @DisplayName("Should return 400 when stock quantity missing in request")
        void shouldReturn400WhenStockQuantityMissing() throws Exception {
            String invalidStockUpdateJson = "{\"invalidField\":45}";

            mockMvc.perform(patch("/api/plants/1/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidStockUpdateJson))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should return 400 when updating stock with negative value")
        void shouldReturn400WhenUpdatingStockWithNegativeValue() throws Exception {
            String negativeStockJson = "{\"stockQuantity\":-5}";

            when(plantShopService.updatePlantStock(1L, -5))
                    .thenThrow(new IllegalArgumentException("Stock quantity cannot be negative"));

            mockMvc.perform(patch("/api/plants/1/stock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(negativeStockJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("DELETE Endpoint")
    class DeleteEndpointTests {

        @Test
        @DisplayName("Should delete plant successfully")
        void shouldDeletePlant() throws Exception {
            mockMvc.perform(delete("/api/plants/1"))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Should return 404 when deleting non-existent plant")
        void shouldReturn404WhenDeletingNonExistentPlant() throws Exception {
            doThrow(new IllegalArgumentException("Plant not found with id: 999"))
                    .when(plantShopService).deletePlant(999L);

            mockMvc.perform(delete("/api/plants/999"))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("Utility Endpoints")
    class UtilityEndpointsTests {

        @Test
        @DisplayName("Should return plant statistics")
        void shouldReturnPlantStatistics() throws Exception {
            when(plantShopService.getTotalPlantCount()).thenReturn(3L);
            when(plantShopService.getPlantsInStock()).thenReturn(allPlants);

            mockMvc.perform(get("/api/plants/stats"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.totalPlants").value(3))
                    .andExpect(jsonPath("$.plantsInStock").value(3));
        }

        @Test
        @DisplayName("Should return health check status")
        void shouldReturnHealthCheckStatus() throws Exception {
            mockMvc.perform(get("/api/plants/health"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.status").value("UP"))
                    .andExpect(jsonPath("$.service").value("Plant Shop API"));
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle IllegalArgumentException with proper error response")
        void shouldHandleIllegalArgumentException() throws Exception {
            when(plantShopService.createPlant(any(Plant.class)))
                    .thenThrow(new IllegalArgumentException("Custom error message"));

            Plant newPlant = new Plant(null, "Test Plant", "Testus plantus", 25.00, 10);

            mockMvc.perform(post("/api/plants")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(newPlant)))
                    .andExpect(status().isBadRequest());
        }
    }
}
