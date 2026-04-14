package com.letschange.aot;

import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlantRepository extends ListCrudRepository<Plant, Long> {
    
    Optional<Plant> findByName(String name);
    
    List<Plant> findByScientificNameContainingIgnoreCase(String scientificName);
    
    List<Plant> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT p FROM Plant p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<Plant> findByPriceRange(@Param("minPrice") Double minPrice, @Param("maxPrice") Double maxPrice);
    
    List<Plant> findByStockQuantityGreaterThan(Integer stockQuantity);
    
    boolean existsByName(String name);
}
