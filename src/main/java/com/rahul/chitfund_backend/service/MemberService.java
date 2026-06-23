package com.rahul.chitfund_backend.service;

import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.exception.CustomException;
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

    public Member updateMember(Long id, Member updatedMember) {
        Member existing = memberRepository.findById(id)
                .orElseThrow(() -> new CustomException("Member not found"));
        existing.setName(updatedMember.getName());
        existing.setPhone(updatedMember.getPhone());
        existing.setAddress(updatedMember.getAddress());
        return memberRepository.save(existing);
    }

    public void deleteMember(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new CustomException("Member not found");
        }
        memberRepository.deleteById(id);
    }
}