package com.devices.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * JPA Entity representing a Device resource.
 * 
 * A device has the following attributes:
 * - id: Unique identifier (auto-generated)
 * - name: Device name
 * - brand: Device brand/manufacturer
 * - state: Current state (AVAILABLE, IN_USE, INACTIVE)
 * - creationTime: Immutable timestamp of device creation
 */
@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceState state;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationTime;

    /**
     * Initializes creationTime before persisting the entity.
     */
    @PrePersist
    protected void onCreate() {
        if (creationTime == null) {
            creationTime = LocalDateTime.now();
        }
    }
}
