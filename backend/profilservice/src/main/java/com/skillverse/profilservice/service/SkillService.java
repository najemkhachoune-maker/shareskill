package com.skillverse.profilservice.service;

import com.skillverse.profilservice.model.Badge;
import com.skillverse.profilservice.model.Profile;
import com.skillverse.profilservice.model.Skill;
import com.skillverse.profilservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final ProfileService profileService;
    private final BadgeService badgeService;

    public Skill createSkill(Skill skill) {
        return skillRepository.save(skill);
    }

    public List<Skill> getAllSkills() {
        return skillRepository.findAll();
    }

    public Skill validateSkill(Long skillId, Long profileId) {
        Skill skill = skillRepository.findById(skillId).orElse(null);
        if (skill != null) {
            skill.setValidated(true);
            skillRepository.save(skill);

            // Créer un badge correspondant à la compétence
            Badge badge = new Badge();
            badge.setName(skill.getName() + " Badge");
            badge.setDescription("Awarded for completing " + skill.getName());
            badgeService.createBadge(badge);

            // Ajouter le badge au profil
            profileService.addBadgeToProfile(profileId, badge);

            return skill;
        }
        return null;
    }

    public void deleteSkill(Long id) {
        if (!skillRepository.existsById(id)) {
            throw new RuntimeException("Skill not found with id " + id);
        }
        skillRepository.deleteById(id);
    }
}
