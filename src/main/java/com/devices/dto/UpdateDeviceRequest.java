package com.devices.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for fully updating a device (all fields required).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateDeviceRequest {

    @NotNull(message = "Device name cannot be null")
    private String name;

    @NotNull(message = "Device brand cannot be null")
    private String brand;

    @NotNull(message = "Device state cannot be null")
    private String state;
}
