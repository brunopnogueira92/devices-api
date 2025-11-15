package com.devices.controller;

import com.devices.dto.CreateDeviceRequest;
import com.devices.dto.DeviceResponse;
import com.devices.dto.PatchDeviceRequest;
import com.devices.dto.UpdateDeviceRequest;
import com.devices.exception.DeviceNotFoundException;
import com.devices.exception.InvalidDeviceOperationException;
import com.devices.service.DeviceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DeviceController.class)
@DisplayName("DeviceController Tests")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DeviceService deviceService;

    private DeviceResponse testResponse;
    private CreateDeviceRequest createRequest;
    private UpdateDeviceRequest updateRequest;
    private PatchDeviceRequest patchRequest;

    @BeforeEach
    void setUp() {
        LocalDateTime now = LocalDateTime.now();

        testResponse = DeviceResponse.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state("AVAILABLE")
                .creationTime(now)
                .build();

        createRequest = CreateDeviceRequest.builder()
                .name("Laptop")
                .brand("Dell")
                .build();

        updateRequest = UpdateDeviceRequest.builder()
                .name("Laptop")
                .brand("Dell")
                .state("AVAILABLE")
                .build();

        patchRequest = PatchDeviceRequest.builder()
                .state("INACTIVE")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/devices - Should create device and return 201")
    void testCreateDevice() throws Exception {
        // Given
        when(deviceService.create(any(CreateDeviceRequest.class))).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")))
                .andExpect(jsonPath("$.brand", is("Dell")))
                .andExpect(jsonPath("$.state", is("AVAILABLE")));

        verify(deviceService, times(1)).create(any(CreateDeviceRequest.class));
    }

    @Test
    @DisplayName("GET /api/v1/devices/{id} - Should return device when found")
    void testGetDeviceById() throws Exception {
        // Given
        when(deviceService.findById(1L)).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));

        verify(deviceService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/devices/{id} - Should return 404 when device not found")
    void testGetDeviceById_NotFound() throws Exception {
        // Given
        when(deviceService.findById(999L)).thenThrow(new DeviceNotFoundException(999L));

        // When & Then
        mockMvc.perform(get("/api/v1/devices/999"))
                .andExpect(status().isNotFound());

        verify(deviceService, times(1)).findById(999L);
    }

    @Test
    @DisplayName("GET /api/v1/devices - Should return all devices with pagination")
    void testGetAllDevices() throws Exception {
        // Given
        Page<DeviceResponse> page = new PageImpl<>(
                Collections.singletonList(testResponse),
                PageRequest.of(0, 10),
                1
        );
        when(deviceService.findAll(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("Laptop")));

        verify(deviceService, times(1)).findAll(any());
    }

    @Test
    @DisplayName("GET /api/v1/devices/brand/{brand} - Should return devices by brand")
    void testGetDevicesByBrand() throws Exception {
        // Given
        Page<DeviceResponse> page = new PageImpl<>(
                Collections.singletonList(testResponse),
                PageRequest.of(0, 10),
                1
        );
        when(deviceService.findByBrand(anyString(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/devices/brand/Dell"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].brand", is("Dell")));

        verify(deviceService, times(1)).findByBrand(anyString(), any());
    }

    @Test
    @DisplayName("GET /api/v1/devices/state/{state} - Should return devices by state")
    void testGetDevicesByState() throws Exception {
        // Given
        Page<DeviceResponse> page = new PageImpl<>(
                Collections.singletonList(testResponse),
                PageRequest.of(0, 10),
                1
        );
        when(deviceService.findByState(anyString(), any())).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/devices/state/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].state", is("AVAILABLE")));

        verify(deviceService, times(1)).findByState(anyString(), any());
    }

    @Test
    @DisplayName("PUT /api/v1/devices/{id} - Should fully update device")
    void testUpdateDevice() throws Exception {
        // Given
        when(deviceService.fullUpdate(eq(1L), any(UpdateDeviceRequest.class))).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/devices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Laptop")));

        verify(deviceService, times(1)).fullUpdate(eq(1L), any(UpdateDeviceRequest.class));
    }

    @Test
    @DisplayName("PUT /api/v1/devices/{id} - Should return 400 when updating IN_USE device name")
    void testUpdateDevice_InvalidOperation() throws Exception {
        // Given
        when(deviceService.fullUpdate(eq(1L), any(UpdateDeviceRequest.class)))
                .thenThrow(new InvalidDeviceOperationException("Cannot update name of IN_USE device"));

        // When & Then
        mockMvc.perform(put("/api/v1/devices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isBadRequest());

        verify(deviceService, times(1)).fullUpdate(eq(1L), any(UpdateDeviceRequest.class));
    }

    @Test
    @DisplayName("PATCH /api/v1/devices/{id} - Should partially update device")
    void testPatchDevice() throws Exception {
        // Given
        when(deviceService.partialUpdate(eq(1L), any(PatchDeviceRequest.class))).thenReturn(testResponse);

        // When & Then
        mockMvc.perform(patch("/api/v1/devices/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));

        verify(deviceService, times(1)).partialUpdate(eq(1L), any(PatchDeviceRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/devices/{id} - Should delete device and return 204")
    void testDeleteDevice() throws Exception {
        // Given
        doNothing().when(deviceService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/devices/1"))
                .andExpect(status().isNoContent());

        verify(deviceService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("DELETE /api/v1/devices/{id} - Should return 400 when deleting IN_USE device")
    void testDeleteDevice_InUseDevice() throws Exception {
        // Given
        doThrow(new InvalidDeviceOperationException("Cannot delete IN_USE device"))
                .when(deviceService).delete(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/devices/1"))
                .andExpect(status().isBadRequest());

        verify(deviceService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("POST /api/v1/devices - Should return 400 when name is blank")
    void testCreateDevice_ValidationError() throws Exception {
        // Given
        CreateDeviceRequest invalidRequest = CreateDeviceRequest.builder()
                .name("")
                .brand("Dell")
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(deviceService, never()).create(any(CreateDeviceRequest.class));
    }
}
