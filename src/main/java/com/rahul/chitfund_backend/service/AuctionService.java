package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.Auction;
import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.AuctionRepository;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionService {

    @Autowired
    private AuctionRepository auctionRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Auction recordAuction(Long chitGroupId, Long winnerId,
                                 Integer monthNumber, BigDecimal bidAmount) {

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        Member winner = memberRepository.findById(winnerId)
                .orElseThrow(() -> new CustomException("Member not found"));

        // 1. Check duplicate auction for this month
        Optional<Auction> existingAuction = auctionRepository
                .findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber);
        if (existingAuction.isPresent()) {
            throw new CustomException("Auction already recorded for this month.");
        }

        // 2. Check if member already won
        Optional<Auction> alreadyWon = auctionRepository
                .findByChitGroupIdAndWinnerId(chitGroupId, winnerId);
        if (alreadyWon.isPresent()) {
            throw new CustomException("This member has already won an auction in this group.");
        }

        // 3. Validate max bid (50% of pool)
        BigDecimal maxBid = group.getTotalChitAmount().multiply(BigDecimal.valueOf(0.5));
        if (bidAmount.compareTo(maxBid) > 0) {
            throw new CustomException("Bid amount cannot exceed 50% of total chit amount i.e. ₹" + maxBid);
        }

        // 4. Check if this is owner's month (month 2)
        boolean isOwnerMonth = monthNumber == 2;
        if (isOwnerMonth) {
            group.setOwnerBalance(group.getOwnerBalance().add(group.getTotalChitAmount()));
        }

        // 5. Check if double chit is applicable
        BigDecimal sixtyPercent = group.getTotalChitAmount().multiply(BigDecimal.valueOf(0.6));
        boolean isDoubleChit = group.getOwnerBalance().compareTo(sixtyPercent) > 0;

        // 6. Calculate received amount and update owner balance

        BigDecimal receivedAmount = group.getTotalChitAmount().subtract(bidAmount);
        group.setOwnerBalance(group.getOwnerBalance().add(bidAmount));

        // 7. Save updated group balance
        chitGroupRepository.save(group);

        // 8. Save auction
        Auction auction = new Auction();
        auction.setChitGroup(group);
        auction.setWinner(winner);
        auction.setMonthNumber(monthNumber);
        auction.setBidAmount(bidAmount);
        auction.setReceivedAmount(receivedAmount);
        auction.setAuctionDate(LocalDate.now());
        auction.setIsDoubleChit(isDoubleChit);
        auction.setIsOwnerMonth(isOwnerMonth);

        return auctionRepository.save(auction);
    }

    public List<Auction> getAuctionsByGroup(Long chitGroupId) {
        return auctionRepository.findByChitGroupId(chitGroupId);
    }

    public BigDecimal getOwnerBalance(Long chitGroupId) {
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));
        return group.getOwnerBalance();
    }
}