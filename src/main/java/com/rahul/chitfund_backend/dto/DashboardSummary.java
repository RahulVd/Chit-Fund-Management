package com.rahul.chitfund_backend.dto;

import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
public class DashboardSummary {
    private String chitGroupName;
    private Integer totalMembers;
    private BigDecimal monthlyContribution;
    private BigDecimal totalChitAmount;
    private int currentMonth;
    private BigDecimal totalCollectedSoFar;
    private int membersWhoWonAuction;
    private int membersYetToWin;

    private List<String> unpaidMemberNamesThisMonth;

    public DashboardSummary(String chitGroupName, Integer totalMembers,
                            BigDecimal monthlyContribution, BigDecimal totalChitAmount,
                            int currentMonth, BigDecimal totalCollectedSoFar,
                            int membersWhoWonAuction, List<String> unpaidMemberNamesThisMonth) {
        this.chitGroupName = chitGroupName;
        this.totalMembers = totalMembers;
        this.monthlyContribution = monthlyContribution;
        this.totalChitAmount = totalChitAmount;
        this.currentMonth = currentMonth;
        this.totalCollectedSoFar = totalCollectedSoFar;
        this.membersWhoWonAuction = membersWhoWonAuction;
        this.membersYetToWin = totalMembers - membersWhoWonAuction;
        this.unpaidMemberNamesThisMonth = unpaidMemberNamesThisMonth;
    }

}