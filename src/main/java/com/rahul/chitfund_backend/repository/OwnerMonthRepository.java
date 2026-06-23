package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.OwnerMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface OwnerMonthRepository extends JpaRepository<OwnerMonth, Long> {
    Optional<OwnerMonth> findByChitGroupIdAndMonthNumber(Long chitGroupId, Integer monthNumber);
    List<OwnerMonth> findByChitGroupId(Long chitGroupId);
}