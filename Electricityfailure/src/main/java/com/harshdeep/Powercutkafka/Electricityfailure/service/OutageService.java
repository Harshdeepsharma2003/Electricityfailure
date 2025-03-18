package com.harshdeep.Powercutkafka.Electricityfailure.service;

import com.harshdeep.Powercutkafka.Electricityfailure.entity.OutageMessage;
import com.harshdeep.Powercutkafka.Electricityfailure.repository.OutageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutageService {

    private final OutageRepository outageRepository;

    @Autowired
    public OutageService(OutageRepository outageRepository) {
        this.outageRepository = outageRepository;
    }

    public OutageMessage saveOutage(OutageMessage outageMessage) {
        return outageRepository.save(outageMessage);
    }

    public List<OutageMessage> getAllOutages() {
        return outageRepository.findAll();
    }
}