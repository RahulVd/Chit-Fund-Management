package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    List<Meeting> findByChitGroupId(Long chitGroupId);
    Optional<Meeting> findByChitGroupIdAndMonthNumber(Long chitGroupId, Integer monthNumber);
}
