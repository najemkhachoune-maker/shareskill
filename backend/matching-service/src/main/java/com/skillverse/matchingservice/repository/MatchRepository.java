package com.skillverse.matchingservice.repository;

import com.skillverse.matchingservice.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByUserId1OrUserId2(Long userId1, Long userId2);
}
