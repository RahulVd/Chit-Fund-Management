package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.dto.AuctionRequest;
import com.rahul.chitfund_backend.entity.Auction;
import com.rahul.chitfund_backend.service.AuctionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auctions")
public class AuctionController {

    @Autowired
    private AuctionService auctionService;

    @PostMapping("/record")
    public ResponseEntity<Auction> recordAuction(@Valid @RequestBody AuctionRequest request) {
        Auction auction = auctionService.recordAuction(
                request.getChitGroupId(),
                request.getWinnerId(),
                request.getMonthNumber(),
                request.getBidAmount(),
                request.isDoubleChit()
        );
        return ResponseEntity.ok(auction);
    }

    @GetMapping("/group/{chitGroupId}")
    public ResponseEntity<List<Auction>> getAuctions(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(auctionService.getAuctionsByGroup(chitGroupId));
    }

    @GetMapping("/group/{chitGroupId}/owner-balance")
    public ResponseEntity<BigDecimal> getOwnerBalance(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(auctionService.getOwnerBalance(chitGroupId));
    }

    @GetMapping("/group/{chitGroupId}/last-month-payout")
    public ResponseEntity<Map<String, Object>> getLastMonthPayout(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(auctionService.getLastMonthPayout(chitGroupId));
    }
}