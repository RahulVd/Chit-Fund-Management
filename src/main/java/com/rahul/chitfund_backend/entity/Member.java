package com.rahul.chitfund_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String address;

    @ManyToOne
    @JoinColumn(name = "chit_group_id")
    private ChitGroup chitGroup;
}