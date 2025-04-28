package com.harshdeep.Powercutkafka.Electricityfailure.service;


import com.harshdeep.Powercutkafka.Electricityfailure.dto.ElectricityFailureDTO;
import com.harshdeep.Powercutkafka.Electricityfailure.entity.ElectricityFailure;
import com.harshdeep.Powercutkafka.Electricityfailure.repository.ElectricityFailureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service class for electricity failure business logic
 */
@Service
public class ElectricityFailureService {

    private static final Logger logger = LoggerFactory.getLogger(ElectricityFailureService.class);

    @Autowired
    private ElectricityFailureRepository failureRepository;

    /**
     * Create a new electricity failure event
     */
    @Transactional
    public ElectricityFailureDTO createFailure(ElectricityFailureDTO failureDTO) {
        logger.info("Creating new electricity failure for location: {}", failureDTO.getLocation());
        ElectricityFailure entity = failureDTO.toEntity();

        // Important: initialize version if null
        if (entity.getVersion() == null) {
            entity.setVersion(0L); // assuming version field is of type Long
        }

        entity = failureRepository.save(entity);
        return new ElectricityFailureDTO(entity);
    }


    /**
     * Get a failure by its ID
     */
    @Transactional(readOnly = true)
    public Optional<ElectricityFailureDTO> getFailureById(UUID id) {
        return failureRepository.findById(id)
                .map(ElectricityFailureDTO::new);
    }

    /**
     * Update an existing failure
     */
    @Transactional
    public Optional<ElectricityFailureDTO> updateFailure(UUID id, ElectricityFailureDTO updatedFailureDTO) {
        return failureRepository.findById(id)
                .map(existingFailure -> {
                    // Update fields from DTO
                    if (updatedFailureDTO.getLocation() != null) {
                        existingFailure.setLocation(updatedFailureDTO.getLocation());
                    }
                    if (updatedFailureDTO.getRegion() != null) {
                        existingFailure.setRegion(updatedFailureDTO.getRegion());
                    }
                    if (updatedFailureDTO.getFailureTime() != null) {
                        existingFailure.setFailureTime(updatedFailureDTO.getFailureTime());
                    }
                    if (updatedFailureDTO.getEstimatedRecoveryTime() != null) {
                        existingFailure.setEstimatedRecoveryTime(updatedFailureDTO.getEstimatedRecoveryTime());
                    }
                    if (updatedFailureDTO.getActualRecoveryTime() != null) {
                        existingFailure.setActualRecoveryTime(updatedFailureDTO.getActualRecoveryTime());
                    }
                    if (updatedFailureDTO.getStatus() != null) {
                        existingFailure.setStatus(ElectricityFailure.Status.valueOf(updatedFailureDTO.getStatus()));
                    }
                    if (updatedFailureDTO.getAffectedAreas() != null) {
                        existingFailure.setAffectedAreas(updatedFailureDTO.getAffectedAreas());
                    }
                    if (updatedFailureDTO.getEstimatedCustomersAffected() != null) {
                        existingFailure.setEstimatedCustomersAffected(updatedFailureDTO.getEstimatedCustomersAffected());
                    }
                    if (updatedFailureDTO.getCause() != null) {
                        existingFailure.setCause(updatedFailureDTO.getCause());
                    }
                    if (updatedFailureDTO.getNotes() != null) {
                        existingFailure.setNotes(updatedFailureDTO.getNotes());
                    }

                    existingFailure.updateTimestamp();
                    existingFailure = failureRepository.save(existingFailure);
                    logger.info("Updated electricity failure: {}", existingFailure.getId());
                    return new ElectricityFailureDTO(existingFailure);
                });
    }

    /**
     * Delete a failure by ID
     */
    @Transactional
    public void deleteFailure(UUID id) {
        logger.info("Deleting electricity failure: {}", id);
        failureRepository.deleteById(id);
    }

    /**
     * Resolve an active failure
     */
    @Transactional
    public Optional<ElectricityFailureDTO> resolveFailure(UUID id) {
        return failureRepository.findById(id)
                .map(failure -> {
                    failure.resolve();
                    failure = failureRepository.save(failure);
                    logger.info("Resolved electricity failure: {}", id);
                    return new ElectricityFailureDTO(failure);
                });
    }

    /**
     * Get all active failures
     */
    @Transactional(readOnly = true)
    public List<ElectricityFailureDTO> getAllActiveFailures() {
        List<ElectricityFailure.Status> activeStatuses = Arrays.asList(ElectricityFailure.Status.ACTIVE, ElectricityFailure.Status.INVESTIGATING);
        return failureRepository.findByStatusIn(activeStatuses)
                .stream()
                .map(ElectricityFailureDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get active failures by region
     */
    @Transactional(readOnly = true)
    public List<ElectricityFailureDTO> getActiveFailuresByRegion(String region) {
        List<ElectricityFailure.Status> activeStatuses = Arrays.asList(ElectricityFailure.Status.ACTIVE, ElectricityFailure.Status.INVESTIGATING);
        return failureRepository.findByRegionAndStatusIn(region, activeStatuses)
                .stream()
                .map(ElectricityFailureDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get failures that occurred after a specific time
     */
    @Transactional(readOnly = true)
    public List<ElectricityFailureDTO> getFailuresAfterTime(LocalDateTime time) {
        return failureRepository.findByFailureTimeAfter(time)
                .stream()
                .map(ElectricityFailureDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get most recent failures, limited by count
     */
    @Transactional(readOnly = true)
    public List<ElectricityFailureDTO> getMostRecentFailures(int limit) {
        return failureRepository.findMostRecentFailures(limit)
                .stream()
                .map(ElectricityFailureDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get failures that have gone past their estimated recovery time but are still active
     */
    @Transactional(readOnly = true)
    public List<ElectricityFailureDTO> getOverdueFailures() {
        return failureRepository.findOverdueFailures(ElectricityFailure.Status.ACTIVE, LocalDateTime.now())
                .stream()
                .map(ElectricityFailureDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get the most severe active failures (affecting most customers)
     */
    @Transactional(readOnly = true)
    public List<ElectricityFailureDTO> getMostSevereActiveFailures() {
        return failureRepository.findMostSevereActiveFailures(ElectricityFailure.Status.ACTIVE)
                .stream()
                .map(ElectricityFailureDTO::new)
                .collect(Collectors.toList());
    }
}