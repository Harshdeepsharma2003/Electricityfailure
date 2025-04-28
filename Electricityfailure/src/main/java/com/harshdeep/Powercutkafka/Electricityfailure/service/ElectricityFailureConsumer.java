package com.harshdeep.Powercutkafka.Electricityfailure.service;


import com.harshdeep.Powercutkafka.Electricityfailure.dto.ElectricityFailureDTO;
import com.harshdeep.Powercutkafka.Electricityfailure.service.ElectricityFailureService;
import com.harshdeep.Powercutkafka.Electricityfailure.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class ElectricityFailureConsumer {

    private static final Logger logger = LoggerFactory.getLogger(ElectricityFailureConsumer.class);

    @Autowired
    private ElectricityFailureService electricityFailureService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // If you have a notification service
    @Autowired(required = false)
    private NotificationService notificationService;

    /**
     * Listen for electricity failure events from Kafka topic
     *
     * @param failureDTO The received electricity failure data
     */
    @KafkaListener(topics = "${kafka.topic.electricity-failures}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeFailureEvent(ElectricityFailureDTO failureDTO) {
        logger.info("Received electricity failure event for location: {}", failureDTO.getLocation());

        try {
            // Save to database
            ElectricityFailureDTO savedFailure = electricityFailureService.createFailure(failureDTO);
            logger.info("Saved electricity failure: {}", savedFailure.getId());

            // Send to WebSocket for real-time updates
            messagingTemplate.convertAndSend("/topic/electricity-failures", savedFailure);

            // Send notifications if notification service is available
            if (notificationService != null) {
                notificationService.sendFailureNotifications(savedFailure);
            }

        } catch (Exception e) {
            logger.error("Error processing electricity failure event: {}", e.getMessage(), e);
        }
    }
}
