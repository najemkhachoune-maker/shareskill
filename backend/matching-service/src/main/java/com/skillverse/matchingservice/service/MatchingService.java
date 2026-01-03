package com.skillverse.matchingservice.service;

import com.skillverse.matchingservice.dto.ProfileDTO;
import com.skillverse.matchingservice.entity.Match;
import com.skillverse.matchingservice.repository.MatchRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    private final MatchRepository matchRepository;

    public MatchingService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public List<Match> findMatches(ProfileDTO userProfile, List<ProfileDTO> allProfiles) {
        List<Match> matches = new ArrayList<>();

        for (ProfileDTO other : allProfiles) {
            if (other.getUserId().equals(userProfile.getUserId()))
                continue;

            List<String> commonSkills = userProfile.getSkills().stream()
                    .filter(skill -> other.getSkills().contains(skill))
                    .collect(Collectors.toList());

            List<String> complementarySkills = userProfile.getSkills().stream()
                    .filter(skill -> other.getLearningInterests().contains(skill))
                    .collect(Collectors.toList());

            if (!commonSkills.isEmpty() || !complementarySkills.isEmpty()) {
                Match match = Match.builder()
                        .userId1(userProfile.getUserId())
                        .userId2(other.getUserId())
                        .sharedSkills(String.join(", ", commonSkills))
                        .complementarySkills(String.join(", ", complementarySkills))
                        .createdAt(LocalDateTime.now())
                        .build();
                matches.add(matchRepository.save(match));
            }
        }

        return matches;
    }

    public List<Match> getUserMatches(Long userId) {
        return matchRepository.findByUserId1OrUserId2(userId, userId);
    }
}
