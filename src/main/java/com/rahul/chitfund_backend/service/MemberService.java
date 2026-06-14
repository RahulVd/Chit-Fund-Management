package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public Member addMember(Member member) {
        return memberRepository.save(member);
    }

    public List<Member> getMembersByGroup(Long chitGroupId) {
        return memberRepository.findByChitGroupId(chitGroupId);
    }
}