package com.devices.controller;

import com.devices.dto.CreateDeviceRequest;
import com.devices.dto.DeviceResponse;
import com.devices.dto.PatchDeviceRequest;
import com.devices.dto.UpdateDeviceRequest;
import com.devices.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Device resource operations.
 * Provides endpoints for CRUD operations and device filtering.
 */
@RestController
@RequestMapping("/api/v1/devices")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Devices", description = "Device resource management endpoints")
public class DeviceController {

    private final DeviceService deviceService;

    /**
     * Create a new device.
     */
    @PostMapping
    @Operation(summary = "Create a new device", description = "Creates a new device with AVAILABLE state")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Device created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<DeviceResponse> createDevice(@Valid @RequestBody CreateDeviceRequest request) {
        log.info("POST /api/v1/devices - Creating new device");
        DeviceResponse response = deviceService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve a single device by ID.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get device by ID", description = "Retrieves a single device by its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    public ResponseEntity<DeviceResponse> getDeviceById(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id) {
        log.info("GET /api/v1/devices/{} - Retrieving device", id);
        DeviceResponse response = deviceService.findById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all devices with pagination.
     */
    @GetMapping
    @Operation(summary = "Get all devices", description = "Retrieves all devices with pagination support")
    @ApiResponse(responseCode = "200", description = "List of devices")
    public ResponseEntity<Page<DeviceResponse>> getAllDevices(Pageable pageable) {
        log.info("GET /api/v1/devices - Retrieving all devices with pagination");
        Page<DeviceResponse> response = deviceService.findAll(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve devices by brand with pagination.
     */
    @GetMapping("/brand/{brand}")
    @Operation(summary = "Get devices by brand", description = "Retrieves all devices matching the specified brand")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of devices"),
            @ApiResponse(responseCode = "400", description = "Invalid query parameters")
    })
    public ResponseEntity<Page<DeviceResponse>> getDevicesByBrand(
            @Parameter(description = "Device brand", required = true)
            @PathVariable String brand,
            Pageable pageable) {
        log.info("GET /api/v1/devices/brand/{} - Retrieving devices by brand", brand);
        Page<DeviceResponse> response = deviceService.findByBrand(brand, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve devices by state with pagination.
     */
    @GetMapping("/state/{state}")
    @Operation(summary = "Get devices by state", description = "Retrieves all devices in the specified state (AVAILABLE, IN_USE, INACTIVE)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of devices"),
            @ApiResponse(responseCode = "400", description = "Invalid state value")
    })
    public ResponseEntity<Page<DeviceResponse>> getDevicesByState(
            @Parameter(description = "Device state (AVAILABLE, IN_USE, INACTIVE)", required = true)
            @PathVariable String state,
            Pageable pageable) {
        log.info("GET /api/v1/devices/state/{} - Retrieving devices by state", state);
        Page<DeviceResponse> response = deviceService.findByState(state, pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Fully update a device (PUT - all fields required).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update device (full update)", description = "Fully updates all fields of a device. Cannot update name/brand if IN_USE or creationTime.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update request or business rule violation")
    })
    public ResponseEntity<DeviceResponse> updateDevice(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateDeviceRequest request) {
        log.info("PUT /api/v1/devices/{} - Fully updating device", id);
        DeviceResponse response = deviceService.fullUpdate(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Partially update a device (PATCH - only provided fields are updated).
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Update device (partial update)", description = "Partially updates only the provided fields. Cannot update name/brand if IN_USE.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update request or business rule violation")
    })
    public ResponseEntity<DeviceResponse> patchDevice(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id,
            @RequestBody PatchDeviceRequest request) {
        log.info("PATCH /api/v1/devices/{} - Partially updating device", id);
        DeviceResponse response = deviceService.partialUpdate(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a device by ID.
     * A device cannot be deleted if it is IN_USE.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete device", description = "Deletes a device. Cannot delete if device is IN_USE.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "400", description = "Cannot delete device in IN_USE state")
    })
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "Device ID", required = true)
            @PathVariable Long id) {
        log.info("DELETE /api/v1/devices/{} - Deleting device", id);
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
