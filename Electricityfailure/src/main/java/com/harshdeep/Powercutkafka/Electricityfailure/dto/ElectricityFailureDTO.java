package com.harshdeep.Powercutkafka.Electricityfailure.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.harshdeep.Powercutkafka.Electricityfailure.entity.ElectricityFailure;
/**
 * Data Transfer Object for ElectricityFailure
 * Used for Kafka messages and API responses
 */
public class ElectricityFailureDTO {

    private UUID id;
    private String location;
    private String region;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime failureTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime estimatedRecoveryTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime actualRecoveryTime;

    private String status;
    private String affectedAreas;
    private Integer estimatedCustomersAffected;
    private String cause;
    private String notes;

    // Default constructor needed for Jackson
    public ElectricityFailureDTO() {
    }

    // Constructor from entity
    public ElectricityFailureDTO(ElectricityFailure entity) {
        this.id = entity.getId();
        this.location = entity.getLocation();
        this.region = entity.getRegion();
        this.failureTime = entity.getFailureTime();
        this.estimatedRecoveryTime = entity.getEstimatedRecoveryTime();
        this.actualRecoveryTime = entity.getActualRecoveryTime();
        this.status = entity.getStatus().name();
        this.affectedAreas = entity.getAffectedAreas();
        this.estimatedCustomersAffected = entity.getEstimatedCustomersAffected();
        this.cause = entity.getCause();
        this.notes = entity.getNotes();
    }

    // Convert DTO to entity
    public ElectricityFailure toEntity() {
        ElectricityFailure entity = new ElectricityFailure();
        entity.setId(this.id);
        entity.setLocation(this.location);
        entity.setRegion(this.region);
        entity.setFailureTime(this.failureTime);
        entity.setEstimatedRecoveryTime(this.estimatedRecoveryTime);
        entity.setActualRecoveryTime(this.actualRecoveryTime);
        entity.setStatus(ElectricityFailure.Status.valueOf(this.status));
        entity.setAffectedAreas(this.affectedAreas);
        entity.setEstimatedCustomersAffected(this.estimatedCustomersAffected);
        entity.setCause(this.cause);
        entity.setNotes(this.notes);
        return entity;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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
}
