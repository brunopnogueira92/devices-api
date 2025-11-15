package com.devices.util;

import com.devices.dto.DeviceResponse;
import com.devices.entity.Device;
import com.devices.entity.DeviceState;
import org.springframework.stereotype.Component;

/**
 * Mapper utility for converting between Device entity and DTOs.
 */
@Component
public class DeviceMapper {

    /**
     * Convert Device entity to DeviceResponse DTO.
     */
    public DeviceResponse toResponse(Device device) {
        if (device == null) {
            return null;
        }

        return DeviceResponse.builder()
                .id(device.getId())
                .name(device.getName())
                .brand(device.getBrand())
                .state(device.getState().name())
                .creationTime(device.getCreationTime())
                .build();
    }

    /**
     * Convert DeviceState string to enum.
     */
    public DeviceState stringToState(String state) {
        try {
            return DeviceState.valueOf(state.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid device state: " + state);
        }
    }
}
