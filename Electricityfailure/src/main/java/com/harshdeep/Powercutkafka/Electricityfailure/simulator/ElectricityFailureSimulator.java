package com.harshdeep.Powercutkafka.Electricityfailure.simulator;

import com.harshdeep.Powercutkafka.Electricityfailure.dto.ElectricityFailureDTO;
import com.harshdeep.Powercutkafka.Electricityfailure.service.ElectricityFailureProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
@EnableScheduling
public class ElectricityFailureSimulator {

    private final ElectricityFailureProducer producer;
    private final Random random = new Random();

    // Store active failures to resolve them later
    private final ConcurrentMap<String, ElectricityFailureDTO> activeFailures = new ConcurrentHashMap<>();

    // Sample locations and regions
    private final List<String> locations = Arrays.asList("Manhattan", "Brooklyn", "Boston", "Chicago", "Los Angeles");
    private final List<String> regions = Arrays.asList("Northeast", "Northeast", "Northeast", "Midwest", "West");

    // Failure causes
    private final List<String> failureCauses = Arrays.asList(
            "GRID_FAILURE", "TRANSFORMER_DAMAGE", "WEATHER_RELATED", "SCHEDULED_MAINTENANCE", "UNKNOWN"
    );

    // Affected areas
    private final List<String> affectedAreas = Arrays.asList(
            "Downtown", "Residential District", "Industrial Zone", "Business Center", "Suburban Area"
    );

    @Autowired
    public ElectricityFailureSimulator(ElectricityFailureProducer producer) {
        this.producer = producer;
    }

    // Simulate a new failure every 30-120 seconds
    @Scheduled(fixedDelay = 30000)
    public void simulateRandomFailure() {
        // 50% chance to generate a new failure if we have less than 3 active ones
        if (activeFailures.size() < 3 && random.nextBoolean()) {
            generateNewFailure();
        }
    }

    // Try to resolve an existing failure every 60 seconds
    @Scheduled(fixedDelay = 60000)
    public void resolveRandomFailure() {
        // 70% chance to resolve one failure if we have any active
        if (!activeFailures.isEmpty() && random.nextDouble() < 0.7) {
            resolveFailure();
        }
    }

    private void generateNewFailure() {
        int locationIndex = random.nextInt(locations.size());
        String location = locations.get(locationIndex);
        String region = regions.get(locationIndex);
        String locationKey = location + "-" + region;

        // Skip if this location already has an active failure
        if (activeFailures.containsKey(locationKey)) {
            return;
        }

        ElectricityFailureDTO failure = new ElectricityFailureDTO();
        failure.setId(UUID.randomUUID());
        failure.setLocation(location);
        failure.setRegion(region);
        failure.setCause(failureCauses.get(random.nextInt(failureCauses.size())));
        failure.setEstimatedCustomersAffected(random.nextInt(5000) + 100);
        failure.setFailureTime(LocalDateTime.now());
        failure.setEstimatedRecoveryTime(LocalDateTime.now().plusHours(random.nextInt(6) + 1));
        failure.setAffectedAreas(affectedAreas.get(random.nextInt(affectedAreas.size())));
        failure.setStatus("ACTIVE");
        failure.setNotes(generateFailureNotes(failure));

        // Store in active failures map
        activeFailures.put(locationKey, failure);

        // Send to Kafka using the correct method name
        producer.publishFailureEvent(failure);

        System.out.println("Generated new failure in " + failure.getLocation() + ", " + failure.getRegion());
    }

    private void resolveFailure() {
        if (activeFailures.isEmpty()) {
            return;
        }

        // Pick a random active failure to resolve
        String locationKey = activeFailures.keySet().stream()
                .skip(random.nextInt(activeFailures.size()))
                .findFirst()
                .orElse(null);

        if (locationKey != null) {
            ElectricityFailureDTO failure = activeFailures.get(locationKey);
            failure.setActualRecoveryTime(LocalDateTime.now());
            failure.setStatus("RESOLVED");

            // Add resolution note
            String existingNotes = failure.getNotes();
            failure.setNotes(existingNotes + "\nResolved on " + LocalDateTime.now() +
                    ". Power restored to all affected customers.");

            // Remove from active failures
            activeFailures.remove(locationKey);

            // Send resolution event using the same method - your producer doesn't have a separate method
            // for resolution events, so we're using the same publish method
            producer.publishFailureEvent(failure);

            System.out.println("Resolved failure in " + failure.getLocation() + ", " + failure.getRegion());
        }
    }

    private String generateFailureNotes(ElectricityFailureDTO failure) {
        switch (failure.getCause()) {
            case "GRID_FAILURE":
                return "Grid failure affecting " + failure.getLocation() + " in " + failure.getRegion() +
                        ". Technicians dispatched. " + failure.getEstimatedCustomersAffected() +
                        " customers affected.";
            case "TRANSFORMER_DAMAGE":
                return "Transformer damage in " + failure.getLocation() + ". Replacement in progress. " +
                        "Estimated " + failure.getEstimatedCustomersAffected() + " customers without power.";
            case "WEATHER_RELATED":
                return "Weather-related outage in " + failure.getLocation() + " due to severe conditions. " +
                        "Crews working to restore power to approximately " +
                        failure.getEstimatedCustomersAffected() + " affected customers.";
            case "SCHEDULED_MAINTENANCE":
                return "Scheduled maintenance in " + failure.getLocation() + ". Service will resume by " +
                        failure.getEstimatedRecoveryTime() + ". Affecting " +
                        failure.getEstimatedCustomersAffected() + " customers.";
            default:
                return "Unknown issue affecting power in " + failure.getLocation() + ". Investigating. " +
                        "Approximately " + failure.getEstimatedCustomersAffected() +
                        " customers without power.";
        }
    }
}