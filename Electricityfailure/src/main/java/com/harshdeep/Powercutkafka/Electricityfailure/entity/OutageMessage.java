package com.harshdeep.Powercutkafka.Electricityfailure.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Entity
@Table(name="outagemessages")
public class OutageMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String location; // Location of the power cut

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber; // Contact number for notifications

    @Column(name = "power_cut_time", nullable = false)
    private LocalDateTime powerCutTime; // Time when the power cut occurred

    @Column(name = "expected_resolved_time")
    private LocalDateTime expectedResolvedTime; // Expected time for resolution

    @Column(columnDefinition = "TEXT")
    private String description; // Description of the outage message

}