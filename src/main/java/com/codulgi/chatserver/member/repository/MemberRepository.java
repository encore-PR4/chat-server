package com.codulgi.chatserver.member.repository;

import com.codulgi.chatserver.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {

    Optional<Member> findByEmail(String email);


    List<Member> findByEmailContainingIgnoreCase(String email);

    @Query("SELECT m FROM Member m WHERE UPPER(m.email) LIKE CONCAT('%', :email, '%')")
    List<Member> searchByEmail(@Param("email") String email);
}
