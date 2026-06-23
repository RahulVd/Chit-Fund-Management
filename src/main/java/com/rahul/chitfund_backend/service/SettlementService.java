package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.ChitGroupStatus;
import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.Settlement;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.MemberRepository;
import com.rahul.chitfund_backend.repository.SettlementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SettlementService {

    private final ChitGroupRepository chitGroupRepository;
    private final MemberRepository memberRepository;
    private final SettlementRepository settlementRepository;

    public SettlementService(ChitGroupRepository chitGroupRepository,
                             MemberRepository memberRepository,
                             SettlementRepository settlementRepository) {
        this.chitGroupRepository = chitGroupRepository;
        this.memberRepository = memberRepository;
        this.settlementRepository = settlementRepository;
    }

    @Transactional
    public List<Settlement> settleGroup(Long chitGroupId) {

        // 1. Group must exist
        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found."));

        // 2. Group must be COMPLETED before settling
        if (group.getStatus() != ChitGroupStatus.COMPLETED) {
            throw new CustomException("Cannot settle a group that is not yet completed.");
        }

        // 3. Cannot settle twice
        if (settlementRepository.existsByChitGroupId(chitGroupId)) {
            throw new CustomException("This group has already been settled.");
        }

        // 4. Get all members
        List<Member> members = memberRepository.findByChitGroupId(chitGroupId);
        if (members.isEmpty()) {
            throw new CustomException("No members found for this group.");
        }

        // 5. Calculate dividend per member
        BigDecimal ownerBalance = group.getOwnerBalance();
        BigDecimal totalMembers = BigDecimal.valueOf(members.size());
        BigDecimal dividendPerMember = ownerBalance.divide(totalMembers, 2, RoundingMode.HALF_UP);

        // 6. Create one settlement row per member
        LocalDate today = LocalDate.now();
        List<Settlement> settlements = members.stream().map(member -> {
            Settlement settlement = new Settlement();
            settlement.setChitGroup(group);
            settlement.setMember(member);
            settlement.setDividendAmount(dividendPerMember);
            settlement.setSettledDate(today);
            return settlement;
        }).collect(Collectors.toList());

        return settlementRepository.saveAll(settlements);
    }

    public List<Settlement> getSettlements(Long chitGroupId) {
        return settlementRepository.findByChitGroupId(chitGroupId);
    }
}