package com.rahul.chitfund_backend.repository;

import com.rahul.chitfund_backend.entity.Member;
import com.rahul.chitfund_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByChitGroupId(Long chitGroupId);

    Optional<Payment> findByChitGroupIdAndMemberIdAndMonthNumber(
            Long chitGroupId, Long memberId, Integer monthNumber
    );

    @Query("SELECT m FROM Member m WHERE m.chitGroup.id = :chitGroupId AND m.id NOT IN " +
            "(SELECT p.member.id FROM Payment p WHERE p.chitGroup.id = :chitGroupId AND p.monthNumber = :monthNumber)")
    List<Member> findMembersWhoHaveNotPaid(@Param("chitGroupId") Long chitGroupId,
                                           @Param("monthNumber") Integer monthNumber);


    List<Payment> findByChitGroupIdAndMonthNumber(Long chitGroupId, Integer monthNumber);
}



