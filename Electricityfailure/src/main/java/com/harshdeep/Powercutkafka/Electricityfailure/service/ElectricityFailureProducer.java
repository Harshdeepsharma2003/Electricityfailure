package com.harshdeep.Powercutkafka.Electricityfailure.service;


import com.harshdeep.Powercutkafka.Electricityfailure.dto.ElectricityFailureDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class ElectricityFailureProducer {

    private static final Logger logger = LoggerFactory.getLogger(ElectricityFailureProducer.class);

    @Value("${kafka.topic.electricity-failures}")
    private String electricityFailuresTopic;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish a new electricity failure event to Kafka
     *
     * @param failureDTO The electricity failure data to publish
     * @return CompletableFuture of the send result
     */
    public CompletableFuture<SendResult<String, Object>> publishFailureEvent(ElectricityFailureDTO failureDTO) {
        logger.info("Publishing electricity failure event for location: {}", failureDTO.getLocation());

        // Use the region as the key for partitioning (failures in same region go to same partition)
        String key = failureDTO.getRegion();

        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(electricityFailuresTopic, key, failureDTO);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Sent electricity failure event [key={}, location={}] with offset=[{}]",
                        key, failureDTO.getLocation(), result.getRecordMetadata().offset());
            } else {
                logger.error("Unable to send electricity failure event [key={}, location={}] due to : {}",
                        key, failureDTO.getLocation(), ex.getMessage(), ex);
            }
        });

        return future;
    }
}