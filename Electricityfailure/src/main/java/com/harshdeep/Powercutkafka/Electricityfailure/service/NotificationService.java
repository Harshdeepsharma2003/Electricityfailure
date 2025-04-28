package com.harshdeep.Powercutkafka.Electricityfailure.service;

import com.harshdeep.Powercutkafka.Electricityfailure.dto.ElectricityFailureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for sending notifications about electricity failures
 */
@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    /**
     * Send notifications about a new electricity failure
     *
     * @param failureDTO The electricity failure data
     */
    public void sendFailureNotifications(ElectricityFailureDTO failureDTO) {
        logger.info("Sending notifications for electricity failure in: {}", failureDTO.getLocation());

        // Here you would implement the actual notification sending logic
        // Examples include:
        // - Email notifications
        // - SMS notifications
        // - Push notifications
        // - External API calls

        // For now, we'll just log the notification
        logger.info("NOTIFICATION: Electricity failure in {}. Estimated recovery: {}",
                failureDTO.getLocation(), failureDTO.getEstimatedRecoveryTime());
    }
}