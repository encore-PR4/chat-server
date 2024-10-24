package com.codulgi.chatserver.repository;

import com.codulgi.chatserver.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
}
