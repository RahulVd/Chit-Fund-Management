package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.Auction;
import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.ChitGroupStatus;
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
import java.util.Map;
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
                                 Integer monthNumber, BigDecimal bidAmount,
                                 boolean isDoubleChitRequested) {

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        if (group.getStatus() == ChitGroupStatus.COMPLETED) {
            throw new CustomException("This chit group is already completed.");
        }

        Member winner = memberRepository.findById(winnerId)
                .orElseThrow(() -> new CustomException("Member not found"));

        // 1. Check duplicate auction for this month
        List<Auction> existingAuctions = auctionRepository
                .findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber);

        if (existingAuctions.size() >= 2) {
            throw new CustomException("Maximum 2 auctions allowed per month (double chit).");
        }

        // isDoubleChit now comes from the user's explicit choice, not inferred from count
        boolean isDoubleChit = isDoubleChitRequested;

        // Still guard against nonsense: you can't request double chit as the FIRST
        // auction of the month — there has to already be a first winner to "double" against
        if (isDoubleChit && existingAuctions.isEmpty()) {
            throw new CustomException("Cannot mark as double chit — no first auction recorded for this month yet.");
        }

        // If a first auction already exists for the month, the second one MUST be double chit
        // (this preserves your "max 2 per month, second is always double" rule)
        if (!isDoubleChit && existingAuctions.size() == 1) {
            throw new CustomException("A winner already exists for this month — the second auction must be marked as double chit.");
        }

        // Block double chit in owner's month
        if (isDoubleChit && monthNumber == 2) {
            throw new CustomException("Double chit is not allowed in owner's month (month 2).");
        }

        // Validate double chit eligibility — same 60% rule, now enforced as a GATE not a trigger
        if (isDoubleChit) {
            BigDecimal sixtyPercent = group.getTotalChitAmount().multiply(BigDecimal.valueOf(0.6));
            if (group.getOwnerBalance().compareTo(sixtyPercent) <= 0) {
                throw new CustomException("Owner balance must be more than 60% of pool for double chit.");
            }
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

        // 5. Calculate received amount — always from pool
        BigDecimal receivedAmount = group.getTotalChitAmount().subtract(bidAmount);

        // Update owner balance
        if (isDoubleChit) {
            group.setOwnerBalance(group.getOwnerBalance().subtract(group.getTotalChitAmount()).add(bidAmount));
        } else {
            group.setOwnerBalance(group.getOwnerBalance().add(bidAmount));
        }

        // 6. Validate month number
        long totalMembers = memberRepository.countByChitGroupId(chitGroupId);
        long totalWinners = auctionRepository.findByChitGroupId(chitGroupId).size();

        if (monthNumber > totalMembers) {
            throw new CustomException("Month number cannot exceed total members (" + totalMembers + ").");
        }

        chitGroupRepository.save(group);

        Auction auction = new Auction();
        auction.setChitGroup(group);
        auction.setWinner(winner);
        auction.setMonthNumber(monthNumber);
        auction.setBidAmount(bidAmount);
        auction.setReceivedAmount(receivedAmount);
        auction.setAuctionDate(LocalDate.now());
        auction.setIsDoubleChit(isDoubleChit);
        auction.setIsOwnerMonth(false);
        auction.setOwnerBalanceAfter(group.getOwnerBalance());

        Auction savedAuction = auctionRepository.save(auction);

        if ((totalWinners + 1) >= totalMembers) {
            group.setStatus(ChitGroupStatus.COMPLETED);
            chitGroupRepository.save(group);
        }

        return savedAuction;
    }

    public List<Auction> getAuctionsByGroup(Long chitGroupId) {
        return auctionRepository.findByChitGroupId(chitGroupId);
    }

    public BigDecimal getOwnerBalance(Long chitGroupId) {
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));
        return group.getOwnerBalance();
    }

    public Map<String, Object> getLastMonthPayout(Long chitGroupId) {
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        long totalMembers = memberRepository.countByChitGroupId(chitGroupId);
        BigDecimal dividend = group.getOwnerBalance()
                .divide(BigDecimal.valueOf(totalMembers), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal amountToPay = group.getMonthlyContribution().subtract(dividend);

        return Map.of(
                "ownerBalance", group.getOwnerBalance(),
                "totalMembers", totalMembers,
                "dividendPerMember", dividend,
                "amountEachMemberPays", amountToPay
        );
    }

}