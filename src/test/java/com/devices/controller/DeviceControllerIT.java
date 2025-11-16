package com.devices.controller;

import com.devices.AbstractIntegrationTest;
import com.devices.dto.CreateDeviceRequest;
import com.devices.entity.DeviceState;
import com.devices.repository.DeviceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@DisplayName("DeviceController Integration Tests")
class DeviceControllerIT extends AbstractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @BeforeEach
    void setUp() {
        deviceRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create device via REST API")
    void testCreateDeviceIntegration() throws Exception {
        // Given
        CreateDeviceRequest request = CreateDeviceRequest.builder()
                .name("Laptop Pro")
                .brand("Apple")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Laptop Pro"))
                .andExpect(jsonPath("$.brand").value("Apple"))
                .andExpect(jsonPath("$.state").value("AVAILABLE"));

        // Verify device is in database
        assertThat(deviceRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should fetch device by ID via REST API")
    void testGetDeviceByIdIntegration() throws Exception {
        // Given
        var device = deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.AVAILABLE, null)
        );

        // When & Then
        mockMvc.perform(get("/api/v1/devices/" + device.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(device.getId()))
                .andExpect(jsonPath("$.name").value("Monitor"));
    }

    @Test
    @DisplayName("Should fetch all devices via REST API")
    void testGetAllDevicesIntegration() throws Exception {
        // Given
        deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.AVAILABLE, null)
        );
        deviceRepository.save(
                new com.devices.entity.Device(null, "Keyboard", "Logitech", DeviceState.AVAILABLE, null)
        );

        // When & Then
        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("Should filter devices by brand via REST API")
    void testGetDevicesByBrandIntegration() throws Exception {
        // Given
        deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.AVAILABLE, null)
        );
        deviceRepository.save(
                new com.devices.entity.Device(null, "Keyboard", "Samsung", DeviceState.AVAILABLE, null)
        );
        deviceRepository.save(
                new com.devices.entity.Device(null, "Mouse", "Logitech", DeviceState.AVAILABLE, null)
        );

        // When & Then
        mockMvc.perform(get("/api/v1/devices/brand/Samsung"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    @DisplayName("Should filter devices by state via REST API")
    void testGetDevicesByStateIntegration() throws Exception {
        // Given
        deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.AVAILABLE, null)
        );
        deviceRepository.save(
                new com.devices.entity.Device(null, "Keyboard", "Logitech", DeviceState.IN_USE, null)
        );

        // When & Then
        mockMvc.perform(get("/api/v1/devices/state/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    @DisplayName("Should delete device via REST API")
    void testDeleteDeviceIntegration() throws Exception {
        // Given
        var device = deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.AVAILABLE, null)
        );

        // When & Then
        mockMvc.perform(delete("/api/v1/devices/" + device.getId()))
                .andExpect(status().isNoContent());

        // Verify device is deleted
        assertThat(deviceRepository.existsById(device.getId())).isFalse();
    }

    @Test
    @DisplayName("Should not delete IN_USE device via REST API")
    void testDeleteInUseDeviceShouldFail() throws Exception {
        // Given
        var device = deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.IN_USE, null)
        );

        // When & Then
        mockMvc.perform(delete("/api/v1/devices/" + device.getId()))
                .andExpect(status().isBadRequest());

        // Verify device is not deleted
        assertThat(deviceRepository.existsById(device.getId())).isTrue();
    }

    @Test
    @DisplayName("Should not update name of IN_USE device via REST API")
    void testUpdateInUseDeviceNameShouldFail() throws Exception {
        // Given
        var device = deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.IN_USE, null)
        );

        var updateRequest = new com.devices.dto.UpdateDeviceRequest("Monitor Changed", "Samsung", "IN_USE");

        // When & Then
        mockMvc.perform(put("/api/v1/devices/" + device.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should partially update device via REST API")
    void testPatchDeviceIntegration() throws Exception {
        // Given
        var device = deviceRepository.save(
                new com.devices.entity.Device(null, "Monitor", "Samsung", DeviceState.AVAILABLE, null)
        );

        var patchRequest = new com.devices.dto.PatchDeviceRequest(null, null, "INACTIVE");

        // When & Then
        mockMvc.perform(patch("/api/v1/devices/" + device.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("INACTIVE"));
    }
}
