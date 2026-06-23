package com.rahul.chitfund_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "auctions")
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chit_group_id", nullable = false)
    private ChitGroup chitGroup;

    @ManyToOne
    @JoinColumn(name = "winner_member_id", nullable = false)
    private Member winner;

    @Column(nullable = false)
    private Integer monthNumber;

    @Column(nullable = false)
    private BigDecimal bidAmount;

    @Column(nullable = false)
    private LocalDate auctionDate;

    private BigDecimal receivedAmount;

    private Boolean isDoubleChit = false;

    private Boolean isOwnerMonth = false;

    private BigDecimal ownerBalanceAfter;

}