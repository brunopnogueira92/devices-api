package com.devices.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating a new device.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDeviceRequest {

    @NotBlank(message = "Device name is required")
    private String name;

    @NotBlank(message = "Device brand is required")
    private String brand;
}
