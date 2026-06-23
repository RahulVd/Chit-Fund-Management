package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.entity.Settlement;
import com.rahul.chitfund_backend.service.SettlementService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/settlements")
public class SettlementController {

    private final SettlementService settlementService;

    public SettlementController(SettlementService settlementService) {
        this.settlementService = settlementService;
    }

    @PostMapping("/group/{chitGroupId}/settle")
    public List<Settlement> settleGroup(@PathVariable Long chitGroupId) {
        return settlementService.settleGroup(chitGroupId);
    }

    @GetMapping("/group/{chitGroupId}")
    public List<Settlement> getSettlements(@PathVariable Long chitGroupId) {
        return settlementService.getSettlements(chitGroupId);
    }
}