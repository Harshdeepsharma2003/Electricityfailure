package com.harshdeep.Powercutkafka.Electricityfailure.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity class representing an electricity failure event
 */
@Entity
@Table(name = "electricity_failures")
public class ElectricityFailure {

    public enum Status {
        ACTIVE,
        RESOLVED,
        SCHEDULED,
        INVESTIGATING
    }

    @Id
    @GeneratedValue
    private UUID id;

    @Version
    private Long version;  // Add this version field for optimistic locking

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private LocalDateTime failureTime;

    @Column
    private LocalDateTime estimatedRecoveryTime;

    @Column
    private LocalDateTime actualRecoveryTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column
    private String affectedAreas;

    @Column
    private Integer estimatedCustomersAffected;

    @Column
    private String cause;

    @Column
    private String notes;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Default constructor
    public ElectricityFailure() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = Status.INVESTIGATING;
    }

    // Constructor with essential fields
    public ElectricityFailure(String location, String region, LocalDateTime failureTime, Status status) {
        this();
        this.location = location;
        this.region = region;
        this.failureTime = failureTime;
        this.status = status;
    }

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public LocalDateTime getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(LocalDateTime failureTime) {
        this.failureTime = failureTime;
    }

    public LocalDateTime getEstimatedRecoveryTime() {
        return estimatedRecoveryTime;
    }

    public void setEstimatedRecoveryTime(LocalDateTime estimatedRecoveryTime) {
        this.estimatedRecoveryTime = estimatedRecoveryTime;
    }

    public LocalDateTime getActualRecoveryTime() {
        return actualRecoveryTime;
    }

    public void setActualRecoveryTime(LocalDateTime actualRecoveryTime) {
        this.actualRecoveryTime = actualRecoveryTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAffectedAreas() {
        return affectedAreas;
    }

    public void setAffectedAreas(String affectedAreas) {
        this.affectedAreas = affectedAreas;
    }

    public Integer getEstimatedCustomersAffected() {
        return estimatedCustomersAffected;
    }

    public void setEstimatedCustomersAffected(Integer estimatedCustomersAffected) {
        this.estimatedCustomersAffected = estimatedCustomersAffected;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }


    // Method to mark when updating the entity
    public void updateTimestamp() {
        this.updatedAt = LocalDateTime.now();
    }

    // Method to mark as resolved
    public void resolve() {
        this.status = Status.RESOLVED;
        this.actualRecoveryTime = LocalDateTime.now();
        this.updateTimestamp();
    }

    @Override
    public String toString() {
        return "ElectricityFailure{" +
                "id=" + id +
                ", location='" + location + '\'' +
                ", region='" + region + '\'' +
                ", failureTime=" + failureTime +
                ", status=" + status +
                ", estimatedRecoveryTime=" + estimatedRecoveryTime +
                '}';
    }
}