package com.skillverse.profilservice.service;

import com.skillverse.profilservice.model.Badge;
import com.skillverse.profilservice.model.Profile;
import com.skillverse.profilservice.model.Skill;
import com.skillverse.profilservice.repository.ProfileRepository;
import com.skillverse.profilservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final SkillRepository skillRepository; // <-- ajouter ici

    public Profile createProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id).orElse(null);
    }

    public Profile getProfileByEmail(String email) {
        return profileRepository.findByEmail(email);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile updateProfile(Long id, Profile profileDetails) {
        Profile profile = profileRepository.findById(id).orElse(null);
        if (profile != null) {
            profile.setUsername(profileDetails.getUsername());
            profile.setEmail(profileDetails.getEmail());
            profile.setRole(profileDetails.getRole());
            profile.setBio(profileDetails.getBio());
            profile.setPhotoUrl(profileDetails.getPhotoUrl());
            // Update userId if provided (for linking purposes)
            if (profileDetails.getUserId() != null) {
                profile.setUserId(profileDetails.getUserId());
            }
            return profileRepository.save(profile);
        }
        return null;
    }

    public void deleteProfile(Long id) {
        profileRepository.deleteById(id);
    }

    // ---------- CORRECTION ICI ----------
    public Profile addSkillToProfile(Long profileId, Long skillId) {
        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile != null) {
            Skill skill = skillRepository.findById(skillId).orElse(null);
            if (skill != null) {
                profile.getSkills().add(skill);
                return profileRepository.save(profile);
            }
        }
        return null;
    }

    public Profile addBadgeToProfile(Long profileId, Badge badge) {
        Profile profile = profileRepository.findById(profileId).orElse(null);
        if (profile != null) {
            profile.getBadges().add(badge);
            return profileRepository.save(profile);
        }
        return null;
    }
}
