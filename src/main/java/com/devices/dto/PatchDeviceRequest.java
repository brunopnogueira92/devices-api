package com.devices.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for partially updating a device (all fields optional).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatchDeviceRequest {

    private String name;

    private String brand;

    private String state;
}
