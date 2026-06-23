package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.ChitGroup;
import com.rahul.chitfund_backend.entity.OwnerMonth;
import com.rahul.chitfund_backend.exception.CustomException;
import com.rahul.chitfund_backend.repository.ChitGroupRepository;
import com.rahul.chitfund_backend.repository.OwnerMonthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class OwnerMonthService {

    @Autowired
    private OwnerMonthRepository ownerMonthRepository;

    @Autowired
    private ChitGroupRepository chitGroupRepository;

    public OwnerMonth triggerOwnerMonth(Long chitGroupId, Integer monthNumber) {

        ChitGroup group = chitGroupRepository.findById(chitGroupId)
                .orElseThrow(() -> new CustomException("Chit group not found"));

        // Block if already triggered for this month
        ownerMonthRepository.findByChitGroupIdAndMonthNumber(chitGroupId, monthNumber)
                .ifPresent(o -> { throw new CustomException("Owner month already triggered for month " + monthNumber); });

        // Block if group is completed
        if (group.getStatus().name().equals("COMPLETED")) {
            throw new CustomException("This chit group is already completed.");
        }

        // Add pool amount to owner balance
        group.setOwnerBalance(group.getOwnerBalance().add(group.getTotalChitAmount()));
        chitGroupRepository.save(group);

        // Save owner month record
        OwnerMonth ownerMonth = new OwnerMonth();
        ownerMonth.setChitGroup(group);
        ownerMonth.setMonthNumber(monthNumber);
        ownerMonth.setAmountAdded(group.getTotalChitAmount());
        ownerMonth.setTriggeredDate(LocalDate.now());

        return ownerMonthRepository.save(ownerMonth);
    }

    public List<OwnerMonth> getOwnerMonths(Long chitGroupId) {
        return ownerMonthRepository.findByChitGroupId(chitGroupId);
    }
}