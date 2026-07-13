package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.ChitGroupStatus;
import com.rahul.chitfund_backend.entity.OwnerMonth;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.AuctionRepository;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import com.rahul.chitfund_backend.repository.OwnerMonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class OwnerMonthService {

    @Autowired
    private OwnerMonthRepository ownerMonthRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private AuctionRepository auctionRepository;

        @Transactional
        public OwnerMonth triggerOwnerMonth(Long chitGroupId, Integer monthNumber) {

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        // Block if already triggered for this month
        ownerMonthRepository.findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber)
                .ifPresent(o -> { throw new CustomException("Owner month already triggered for month " + monthNumber); });

        // Block if owner has already won ANY month in this group —
        // owner, like any member, can only win the pool once per group.
        List<OwnerMonth> existingOwnerMonths = ownerMonthRepository.findByChitGroupId(chitGroupId);
        if (!existingOwnerMonths.isEmpty()) {
            throw new CustomException("Owner has already won a month in this group (month " +
                    existingOwnerMonths.get(0).getMonthNumber() + "). Owner can only win once.");
        }

        // Block if group is completed
        if (group.getStatus() == ChitGroupStatus.COMPLETED) {
            throw new CustomException("This chit group is already completed.");
        }

        // Owner-month: the owner takes the full pot for the month. This is a clean
        // payout to the owner -- it does NOT add to the dividend pool (chitGroupBalance).
        // The dividend pool only grows from auction discounts, not from owner-months.
        // So we do NOT mutate chitGroupBalance here. We only record the owner-month row.
        chitGroupRepository.save(group);

        // Save owner month record FIRST, so counts below include it
        OwnerMonth ownerMonth = new OwnerMonth();
        ownerMonth.setChitGroup(group);
        ownerMonth.setMonthNumber(monthNumber);
        ownerMonth.setAmountAdded(group.getTotalChitAmount());
        ownerMonth.setTriggeredDate(LocalDate.now());
        OwnerMonth savedOwnerMonth = ownerMonthRepository.save(ownerMonth);

        // Now check total winners across BOTH auctions and owner months.
        // Data-driven completion: totalMembers is the actual member row count;
        // +1 is the owner slot (owner wins one month but isn't in members table).
        long totalMembers = memberRepository.countByChitGroupId(chitGroupId);
        long totalAuctionWinners = auctionRepository.findByChitGroupId(chitGroupId).size();
        long totalOwnerWinners = ownerMonthRepository.findByChitGroupId(chitGroupId).size();

        if ((totalAuctionWinners + totalOwnerWinners) >= totalMembers + 1) {
            group.setStatus(ChitGroupStatus.COMPLETED);
            chitGroupRepository.save(group);
        }
        return savedOwnerMonth;
    }

    public List<OwnerMonth> getOwnerMonths(Long chitGroupId) {
        return ownerMonthRepository.findByChitGroupId(chitGroupId);
    }
}