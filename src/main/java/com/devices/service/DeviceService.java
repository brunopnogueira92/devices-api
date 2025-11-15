package com.devices.service;

import com.devices.dto.CreateDeviceRequest;
import com.devices.dto.DeviceResponse;
import com.devices.dto.PatchDeviceRequest;
import com.devices.dto.UpdateDeviceRequest;
import com.devices.entity.Device;
import com.devices.entity.DeviceState;
import com.devices.exception.DeviceInvalidStateException;
import com.devices.exception.DeviceNotFoundException;
import com.devices.exception.InvalidDeviceOperationException;
import com.devices.repository.DeviceRepository;
import com.devices.util.DeviceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for Device operations.
 * Handles business logic and domain validations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    /**
     * Create a new device.
     */
    @Transactional
    public DeviceResponse create(CreateDeviceRequest request) {
        log.info("Creating new device with name: {} and brand: {}", request.getName(), request.getBrand());

        Device device = Device.builder()
                .name(request.getName().trim())
                .brand(request.getBrand().trim())
                .state(DeviceState.AVAILABLE)
                .build();

        Device savedDevice = deviceRepository.save(device);
        log.info("Device created successfully with id: {}", savedDevice.getId());

        return deviceMapper.toResponse(savedDevice);
    }

    /**
     * Retrieve a single device by ID.
     */
    @Transactional(readOnly = true)
    public DeviceResponse findById(Long id) {
        log.debug("Finding device with id: {}", id);

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        return deviceMapper.toResponse(device);
    }

    /**
     * Retrieve all devices with pagination.
     */
    @Transactional(readOnly = true)
    public Page<DeviceResponse> findAll(Pageable pageable) {
        log.debug("Finding all devices with pagination: {}", pageable);

        return deviceRepository.findAll(pageable)
                .map(deviceMapper::toResponse);
    }

    /**
     * Find devices by brand with pagination.
     */
    @Transactional(readOnly = true)
    public Page<DeviceResponse> findByBrand(String brand, Pageable pageable) {
        log.debug("Finding devices by brand: {}", brand);

        return deviceRepository.findByBrand(brand, pageable)
                .map(deviceMapper::toResponse);
    }

    /**
     * Find devices by state with pagination.
     */
    @Transactional(readOnly = true)
    public Page<DeviceResponse> findByState(String stateStr, Pageable pageable) {
        log.debug("Finding devices by state: {}", stateStr);

        DeviceState state = deviceMapper.stringToState(stateStr);
        return deviceRepository.findByState(state, pageable)
                .map(deviceMapper::toResponse);
    }

    /**
     * Fully update a device (all fields required).
     * Validates that creationTime cannot be updated and name/brand cannot change if device is IN_USE.
     */
    @Transactional
    public DeviceResponse fullUpdate(Long id, UpdateDeviceRequest request) {
        log.info("Fully updating device with id: {}", id);

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        // Validate that creationTime is not being changed
        // (It's immutable in the entity, but we ensure it here)
        validateNotInUseForNameBrandUpdate(device, request.getName(), request.getBrand());

        device.setName(request.getName().trim());
        device.setBrand(request.getBrand().trim());
        device.setState(deviceMapper.stringToState(request.getState()));

        Device updatedDevice = deviceRepository.save(device);
        log.info("Device with id: {} updated successfully", id);

        return deviceMapper.toResponse(updatedDevice);
    }

    /**
     * Partially update a device (only provided fields are updated).
     * Validates that creationTime cannot be updated and name/brand cannot change if device is IN_USE.
     */
    @Transactional
    public DeviceResponse partialUpdate(Long id, PatchDeviceRequest request) {
        log.info("Partially updating device with id: {}", id);

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        // Check if name or brand are being updated while device is IN_USE
        if ((request.getName() != null || request.getBrand() != null) && 
            device.getState() == DeviceState.IN_USE) {
            throw new InvalidDeviceOperationException(
                    "Cannot update name or brand of a device that is IN_USE");
        }

        if (request.getName() != null) {
            device.setName(request.getName().trim());
        }

        if (request.getBrand() != null) {
            device.setBrand(request.getBrand().trim());
        }

        if (request.getState() != null) {
            device.setState(deviceMapper.stringToState(request.getState()));
        }

        Device updatedDevice = deviceRepository.save(device);
        log.info("Device with id: {} partially updated successfully", id);

        return deviceMapper.toResponse(updatedDevice);
    }

    /**
     * Delete a device by ID.
     * A device cannot be deleted if it is IN_USE.
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deleting device with id: {}", id);

        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        if (device.getState() == DeviceState.IN_USE) {
            log.warn("Cannot delete device with id: {} because it is IN_USE", id);
            throw new InvalidDeviceOperationException(
                    "Cannot delete a device that is IN_USE");
        }

        deviceRepository.deleteById(id);
        log.info("Device with id: {} deleted successfully", id);
    }

    /**
     * Helper method to validate that name/brand cannot be updated if device is IN_USE.
     */
    private void validateNotInUseForNameBrandUpdate(Device device, String newName, String newBrand) {
        if (device.getState() == DeviceState.IN_USE) {
            boolean nameChanged = !device.getName().equals(newName.trim());
            boolean brandChanged = !device.getBrand().equals(newBrand.trim());

            if (nameChanged || brandChanged) {
                throw new InvalidDeviceOperationException(
                        "Cannot update name or brand of a device that is IN_USE");
            }
        }
    }
}
