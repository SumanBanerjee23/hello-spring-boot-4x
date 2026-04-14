package com.letschange.aot;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PlantShopService {
    
    private final PlantRepository plantRepository;
    
    public PlantShopService(PlantRepository plantRepository) {
        this.plantRepository = plantRepository;
    }
    
    @Transactional(readOnly = true)
    public List<Plant> getAllPlants() {
        return plantRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Plant> getPlantById(Long id) {
        return plantRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Optional<Plant> getPlantByName(String name) {
        return plantRepository.findByName(name);
    }
    
    @Transactional(readOnly = true)
    public List<Plant> searchPlantsByName(String name) {
        return plantRepository.findByNameContainingIgnoreCase(name);
    }
    
    @Transactional(readOnly = true)
    public List<Plant> searchPlantsByScientificName(String scientificName) {
        return plantRepository.findByScientificNameContainingIgnoreCase(scientificName);
    }
    
    @Transactional(readOnly = true)
    public List<Plant> searchPlantsByPriceRange(Double minPrice, Double maxPrice) {
        return plantRepository.findByPriceRange(minPrice, maxPrice);
    }
    
    @Transactional(readOnly = true)
    public List<Plant> getPlantsInStock() {
        return plantRepository.findByStockQuantityGreaterThan(0);
    }
    
    public Plant createPlant(@Valid Plant plant) {
        if (plantRepository.existsByName(plant.name())) {
            throw new IllegalArgumentException("Plant with name '" + plant.name() + "' already exists");
        }
        
        Plant plantToSave = new Plant(
            null,
            plant.name(),
            plant.scientificName(),
            plant.price(),
            plant.stockQuantity()
        );
        
        return plantRepository.save(plantToSave);
    }
    
    public Plant updatePlant(Long id, @Valid Plant plantDetails) {
        return plantRepository.findById(id)
            .map(existingPlant -> {
                if (!existingPlant.name().equals(plantDetails.name()) 
                    && plantRepository.existsByName(plantDetails.name())) {
                    throw new IllegalArgumentException("Plant with name '" + plantDetails.name() + "' already exists");
                }
                
                Plant updatedPlant = new Plant(
                    existingPlant.id(),
                    plantDetails.name(),
                    plantDetails.scientificName(),
                    plantDetails.price(),
                    plantDetails.stockQuantity()
                );
                
                return plantRepository.save(updatedPlant);
            })
            .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + id));
    }
    
    public Plant updatePlantStock(Long id, Integer newStockQuantity) {
        if (newStockQuantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        
        return plantRepository.findById(id)
            .map(existingPlant -> {
                Plant updatedPlant = new Plant(
                    existingPlant.id(),
                    existingPlant.name(),
                    existingPlant.scientificName(),
                    existingPlant.price(),
                    newStockQuantity
                );
                
                return plantRepository.save(updatedPlant);
            })
            .orElseThrow(() -> new IllegalArgumentException("Plant not found with id: " + id));
    }
    
    public void deletePlant(Long id) {
        if (!plantRepository.existsById(id)) {
            throw new IllegalArgumentException("Plant not found with id: " + id);
        }
        plantRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public boolean plantExists(Long id) {
        return plantRepository.existsById(id);
    }
    
    @Transactional(readOnly = true)
    public long getTotalPlantCount() {
        return plantRepository.count();
    }
}
