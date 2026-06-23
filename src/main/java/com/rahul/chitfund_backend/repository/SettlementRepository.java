package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findByChitGroupId(Long chitGroupId);

    boolean existsByChitGroupId(Long chitGroupId);
}