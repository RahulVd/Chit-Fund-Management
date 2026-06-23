package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.Auction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionRepository extends JpaRepository<Auction, Long> {
    List<Auction> findByChitGroupIdAndMonthNumber(Long chitGroupId, Integer monthNumber);
    List<Auction> findByChitGroupId(Long chitGroupId);
    Optional<Auction> findByChitGroupIdAndWinnerId(Long chitGroupId, Long winnerId);


}