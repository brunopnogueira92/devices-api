package com.devices.repository;

import com.devices.entity.Device;
import com.devices.entity.DeviceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Device entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    /**
     * Find all devices by brand with pagination.
     * 
     * @param brand the brand to search for
     * @param pageable pagination information
     * @return a page of devices matching the brand
     */
    @Query("SELECT d FROM Device d WHERE LOWER(d.brand) = LOWER(:brand)")
    Page<Device> findByBrand(@Param("brand") String brand, Pageable pageable);

    /**
     * Find all devices by state with pagination.
     * 
     * @param state the device state to search for
     * @param pageable pagination information
     * @return a page of devices matching the state
     */
    @Query("SELECT d FROM Device d WHERE d.state = :state")
    Page<Device> findByState(@Param("state") DeviceState state, Pageable pageable);

    /**
     * Check if a device exists by ID.
     * 
     * @param id the device ID
     * @return true if device exists, false otherwise
     */
    boolean existsById(Long id);
}
