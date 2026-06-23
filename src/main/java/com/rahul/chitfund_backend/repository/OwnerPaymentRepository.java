package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.OwnerPayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OwnerPaymentRepository extends JpaRepository<OwnerPayment, Long> {

    List<OwnerPayment> findByChitGroupId(Long chitGroupId);

    Optional<OwnerPayment> findByChitGroupIdAndMonthNumber(Long chitGroupId, Integer monthNumber);
}