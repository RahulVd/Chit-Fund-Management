package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.entity.OwnerMonth;
import com.rahul.chitfund_backend.service.OwnerMonthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/owner-month")
public class OwnerMonthController {

    @Autowired
    private OwnerMonthService ownerMonthService;

    @PostMapping("/trigger")
    public ResponseEntity<OwnerMonth> triggerOwnerMonth(@RequestBody Map<String, Object> request) {
        Long chitGroupId = Long.valueOf(request.get("chitGroupId").toString());
        Integer monthNumber = Integer.valueOf(request.get("monthNumber").toString());
        return ResponseEntity.ok(ownerMonthService.triggerOwnerMonth(chitGroupId, monthNumber));
    }

    @GetMapping("/group/{chitGroupId}")
    public ResponseEntity<List<OwnerMonth>> getOwnerMonths(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(ownerMonthService.getOwnerMonths(chitGroupId));
    }
}