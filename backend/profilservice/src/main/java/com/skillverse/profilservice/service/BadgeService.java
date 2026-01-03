package com.skillverse.profilservice.service;

import com.skillverse.profilservice.model.Badge;
import com.skillverse.profilservice.repository.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;

    public Badge createBadge(Badge badge) {
        return badgeRepository.save(badge);
    }

    public List<Badge> getAllBadges() {
        return badgeRepository.findAll();
    }

    public void deleteBadge(Long id) {
        if (!badgeRepository.existsById(id)) {
            throw new RuntimeException("Badge not found with id " + id);
        }
        badgeRepository.deleteById(id);
    }
}
