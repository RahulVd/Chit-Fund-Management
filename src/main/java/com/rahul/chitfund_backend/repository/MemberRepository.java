package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByChitGroupId(Long chitGroupId);
}