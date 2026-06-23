package com.rahul.chitfund_backend.controller;

import com.rahul.chitfund_backend.dto.DashboardSummary;
import com.rahul.chitfund_backend.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/group/{chitGroupId}")
    public ResponseEntity<DashboardSummary> getGroupDashboard(@PathVariable Long chitGroupId) {
        return ResponseEntity.ok(dashboardService.getGroupDashboard(chitGroupId));
    }
}