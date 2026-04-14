package com.letschange.aot;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/plants")
@CrossOrigin(origins = "*")
public class PlantShopController {
    
    private final PlantShopService plantShopService;
    
    public PlantShopController(PlantShopService plantShopService) {
        this.plantShopService = plantShopService;
    }
    
    @GetMapping
    public ResponseEntity<List<Plant>> getAllPlants() {
        List<Plant> plants = plantShopService.getAllPlants();
        return ResponseEntity.ok(plants);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Plant> getPlantById(@PathVariable Long id) {
        return plantShopService.getPlantById(id)
            .map(plant -> ResponseEntity.ok(plant))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/name/{name}")
    public ResponseEntity<Plant> getPlantByName(@PathVariable String name) {
        return plantShopService.getPlantByName(name)
            .map(plant -> ResponseEntity.ok(plant))
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Plant>> searchPlants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String scientificName,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "false") boolean inStockOnly) {
        
        List<Plant> plants;
        
        if (name != null && !name.trim().isEmpty()) {
            plants = plantShopService.searchPlantsByName(name);
        } else if (scientificName != null && !scientificName.trim().isEmpty()) {
            plants = plantShopService.searchPlantsByScientificName(scientificName);
        } else if (minPrice != null && maxPrice != null) {
            plants = plantShopService.searchPlantsByPriceRange(minPrice, maxPrice);
        } else if (inStockOnly) {
            plants = plantShopService.getPlantsInStock();
        } else {
            plants = plantShopService.getAllPlants();
        }
        
        return ResponseEntity.ok(plants);
    }
    
    @PostMapping
    public ResponseEntity<Plant> createPlant(@Valid @RequestBody Plant plant) {
        try {
            Plant createdPlant = plantShopService.createPlant(plant);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Plant> updatePlant(
            @PathVariable Long id, 
            @Valid @RequestBody Plant plantDetails) {
        try {
            Plant updatedPlant = plantShopService.updatePlant(id, plantDetails);
            return ResponseEntity.ok(updatedPlant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Plant> updatePlantStock(
            @PathVariable Long id, 
            @RequestBody Map<String, Integer> stockUpdate) {
        
        if (!stockUpdate.containsKey("stockQuantity")) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Integer newStock = stockUpdate.get("stockQuantity");
            Plant updatedPlant = plantShopService.updatePlantStock(id, newStock);
            return ResponseEntity.ok(updatedPlant);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlant(@PathVariable Long id) {
        try {
            plantShopService.deletePlant(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPlantStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPlants", plantShopService.getTotalPlantCount());
        stats.put("plantsInStock", plantShopService.getPlantsInStock().size());
        
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Plant Shop API");
        return ResponseEntity.ok(status);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(error);
    }
}
