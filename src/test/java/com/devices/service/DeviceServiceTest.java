package com.devices.service;

import com.devices.dto.CreateDeviceRequest;
import com.devices.dto.DeviceResponse;
import com.devices.dto.PatchDeviceRequest;
import com.devices.dto.UpdateDeviceRequest;
import com.devices.entity.Device;
import com.devices.entity.DeviceState;
import com.devices.exception.DeviceNotFoundException;
import com.devices.exception.InvalidDeviceOperationException;
import com.devices.repository.DeviceRepository;
import com.devices.util.DeviceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("DeviceService Tests")
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceMapper deviceMapper;

    @InjectMocks
    private DeviceService deviceService;

    private Device testDevice;
    private DeviceResponse testResponse;
    private CreateDeviceRequest createRequest;

    @BeforeEach
    void setUp() {
        testDevice = Device.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state(DeviceState.AVAILABLE)
                .creationTime(LocalDateTime.now())
                .build();

        testResponse = DeviceResponse.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state("AVAILABLE")
                .creationTime(testDevice.getCreationTime())
                .build();

        createRequest = CreateDeviceRequest.builder()
                .name("Laptop")
                .brand("Dell")
                .build();
    }

    @Test
    @DisplayName("Should create device successfully")
    void testCreateDevice() {
        // Given
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);
        when(deviceMapper.toResponse(testDevice)).thenReturn(testResponse);

        // When
        DeviceResponse result = deviceService.create(createRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Laptop");
        assertThat(result.getBrand()).isEqualTo("Dell");
        assertThat(result.getState()).isEqualTo("AVAILABLE");
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    @DisplayName("Should find device by ID successfully")
    void testFindById() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceMapper.toResponse(testDevice)).thenReturn(testResponse);

        // When
        DeviceResponse result = deviceService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(deviceRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw DeviceNotFoundException when device not found by ID")
    void testFindById_NotFound() {
        // Given
        when(deviceRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> deviceService.findById(999L))
                .isInstanceOf(DeviceNotFoundException.class)
                .hasMessageContaining("Device not found");
    }

    @Test
    @DisplayName("Should find all devices with pagination")
    void testFindAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> devicePage = new PageImpl<>(Collections.singletonList(testDevice), pageable, 1);
        when(deviceRepository.findAll(pageable)).thenReturn(devicePage);
        when(deviceMapper.toResponse(testDevice)).thenReturn(testResponse);

        // When
        Page<DeviceResponse> result = deviceService.findAll(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(deviceRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Should find devices by brand with pagination")
    void testFindByBrand() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> devicePage = new PageImpl<>(Collections.singletonList(testDevice), pageable, 1);
        when(deviceRepository.findByBrand("Dell", pageable)).thenReturn(devicePage);
        when(deviceMapper.toResponse(testDevice)).thenReturn(testResponse);

        // When
        Page<DeviceResponse> result = deviceService.findByBrand("Dell", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(deviceRepository, times(1)).findByBrand("Dell", pageable);
    }

    @Test
    @DisplayName("Should find devices by state with pagination")
    void testFindByState() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Device> devicePage = new PageImpl<>(Collections.singletonList(testDevice), pageable, 1);
        when(deviceRepository.findByState(DeviceState.AVAILABLE, pageable)).thenReturn(devicePage);
        when(deviceMapper.stringToState("AVAILABLE")).thenReturn(DeviceState.AVAILABLE);
        when(deviceMapper.toResponse(testDevice)).thenReturn(testResponse);

        // When
        Page<DeviceResponse> result = deviceService.findByState("AVAILABLE", pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(deviceRepository, times(1)).findByState(DeviceState.AVAILABLE, pageable);
    }

    @Test
    @DisplayName("Should fully update device successfully")
    void testFullUpdate() {
        // Given
        UpdateDeviceRequest updateRequest = UpdateDeviceRequest.builder()
                .name("Laptop Updated")
                .brand("Dell")
                .state("AVAILABLE")
                .build();

        Device updatedDevice = Device.builder()
                .id(1L)
                .name("Laptop Updated")
                .brand("Dell")
                .state(DeviceState.AVAILABLE)
                .creationTime(testDevice.getCreationTime())
                .build();

        DeviceResponse updatedResponse = DeviceResponse.builder()
                .id(1L)
                .name("Laptop Updated")
                .brand("Dell")
                .state("AVAILABLE")
                .creationTime(testDevice.getCreationTime())
                .build();

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceMapper.stringToState("AVAILABLE")).thenReturn(DeviceState.AVAILABLE);
        when(deviceRepository.save(any(Device.class))).thenReturn(updatedDevice);
        when(deviceMapper.toResponse(updatedDevice)).thenReturn(updatedResponse);

        // When
        DeviceResponse result = deviceService.fullUpdate(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Laptop Updated");
        verify(deviceRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    @DisplayName("Should throw exception when updating name of IN_USE device")
    void testFullUpdate_InUseDevice_NameChange() {
        // Given
        Device inUseDevice = Device.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state(DeviceState.IN_USE)
                .creationTime(LocalDateTime.now())
                .build();

        UpdateDeviceRequest updateRequest = UpdateDeviceRequest.builder()
                .name("Laptop Changed")
                .brand("Dell")
                .state("IN_USE")
                .build();

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.fullUpdate(1L, updateRequest))
                .isInstanceOf(InvalidDeviceOperationException.class)
                .hasMessageContaining("Cannot update name or brand");
    }

    @Test
    @DisplayName("Should partially update device successfully")
    void testPartialUpdate() {
        // Given
        PatchDeviceRequest patchRequest = PatchDeviceRequest.builder()
                .state("INACTIVE")
                .build();

        Device patchedDevice = Device.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state(DeviceState.INACTIVE)
                .creationTime(testDevice.getCreationTime())
                .build();

        DeviceResponse patchedResponse = DeviceResponse.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state("INACTIVE")
                .creationTime(testDevice.getCreationTime())
                .build();

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));
        when(deviceMapper.stringToState("INACTIVE")).thenReturn(DeviceState.INACTIVE);
        when(deviceRepository.save(any(Device.class))).thenReturn(patchedDevice);
        when(deviceMapper.toResponse(patchedDevice)).thenReturn(patchedResponse);

        // When
        DeviceResponse result = deviceService.partialUpdate(1L, patchRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getState()).isEqualTo("INACTIVE");
        verify(deviceRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    @DisplayName("Should delete device successfully")
    void testDelete() {
        // Given
        when(deviceRepository.findById(1L)).thenReturn(Optional.of(testDevice));

        // When
        deviceService.delete(1L);

        // Then
        verify(deviceRepository, times(1)).findById(1L);
        verify(deviceRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting IN_USE device")
    void testDelete_InUseDevice() {
        // Given
        Device inUseDevice = Device.builder()
                .id(1L)
                .name("Laptop")
                .brand("Dell")
                .state(DeviceState.IN_USE)
                .creationTime(LocalDateTime.now())
                .build();

        when(deviceRepository.findById(1L)).thenReturn(Optional.of(inUseDevice));

        // When & Then
        assertThatThrownBy(() -> deviceService.delete(1L))
                .isInstanceOf(InvalidDeviceOperationException.class)
                .hasMessageContaining("Cannot delete a device that is IN_USE");

        verify(deviceRepository, never()).deleteById(1L);
    }
}
