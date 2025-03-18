package com.harshdeep.Powercutkafka.Electricityfailure.repository;

import com.harshdeep.Powercutkafka.Electricityfailure.entity.OutageMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutageRepository extends JpaRepository<OutageMessage, Long> {
}
