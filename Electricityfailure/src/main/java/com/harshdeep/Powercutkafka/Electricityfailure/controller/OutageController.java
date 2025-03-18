package com.harshdeep.Powercutkafka.Electricityfailure.controller;

import com.harshdeep.Powercutkafka.Electricityfailure.entity.OutageMessage;
import com.harshdeep.Powercutkafka.Electricityfailure.service.OutageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outages")
public class OutageController {

    private final OutageService outageService;

    @Autowired
    public OutageController(OutageService outageService) {
        this.outageService = outageService;
    }

    @PostMapping("/report")
    public ResponseEntity<OutageMessage> reportOutage(@RequestBody OutageMessage outageMessage) {
        OutageMessage savedOutage = outageService.saveOutage(outageMessage);
        return ResponseEntity.ok(savedOutage);
    }

    @GetMapping("/")
    public ResponseEntity<List<OutageMessage>> getAllOutages() {
        List<OutageMessage> outages = outageService.getAllOutages();
        return ResponseEntity.ok(outages);
    }
}

