package com.harshdeep.Powercutkafka.Electricityfailure.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.harshdeep.Powercutkafka.Electricityfailure.entity.ElectricityFailure;
import com.harshdeep.Powercutkafka.Electricityfailure.entity.ElectricityFailure.Status;

/**
 * Repository interface for ElectricityFailure entities
 * Extends JpaRepository to inherit basic CRUD operations
 */
@Repository
public interface ElectricityFailureRepository extends JpaRepository<ElectricityFailure, UUID> {

    /**
     * Find all electricity failures with specific statuses
     */
    List<ElectricityFailure> findByStatusIn(List<ElectricityFailure.Status> statuses);

    /**
     * Find failures by region and statuses
     */
    List<ElectricityFailure> findByRegionAndStatusIn(String region, List<ElectricityFailure.Status> statuses);

    /**
     * Find failures by location containing the given text (case insensitive)
     */
    @Query("SELECT ef FROM ElectricityFailure ef WHERE LOWER(ef.location) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<ElectricityFailure> findByLocationContainingIgnoreCase(@Param("searchTerm") String searchTerm);

    /**
     * Find failures that occurred after a specific time
     */
    List<ElectricityFailure> findByFailureTimeAfter(LocalDateTime time);

    /**
     * Find failures that occurred between two times
     */
    List<ElectricityFailure> findByFailureTimeBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Find failures with estimated recovery time before a specific time
     */
    List<ElectricityFailure> findByEstimatedRecoveryTimeBefore(LocalDateTime time);

    /**
     * Count active failures by region
     */
    @Query("SELECT ef.region, COUNT(ef) FROM ElectricityFailure ef WHERE ef.status = :status GROUP BY ef.region")
    List<Object[]> countByRegionAndStatus(@Param("status") ElectricityFailure.Status status);

    /**
     * Find failures that have gone past their estimated recovery time but are still active
     */
    @Query("SELECT ef FROM ElectricityFailure ef WHERE ef.status = :activeStatus AND ef.estimatedRecoveryTime < :currentTime")
    List<ElectricityFailure> findOverdueFailures(@Param("activeStatus") ElectricityFailure.Status activeStatus, @Param("currentTime") LocalDateTime currentTime);

    /**
     * Find most recent failures, limited by count
     */
    @Query(value = "SELECT * FROM electricity_failures ORDER BY failure_time DESC LIMIT :limit", nativeQuery = true)
    List<ElectricityFailure> findMostRecentFailures(@Param("limit") int limit);

    /**
     * Get active failures affecting the most customers
     */
    @Query("SELECT ef FROM ElectricityFailure ef WHERE ef.status = :activeStatus ORDER BY ef.estimatedCustomersAffected DESC")
    List<ElectricityFailure> findMostSevereActiveFailures(@Param("activeStatus") ElectricityFailure.Status activeStatus);
}