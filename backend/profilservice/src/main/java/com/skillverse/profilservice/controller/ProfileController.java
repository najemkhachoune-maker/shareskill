package com.skillverse.profilservice.controller;

import com.skillverse.profilservice.model.Badge;
import com.skillverse.profilservice.model.Profile;
import com.skillverse.profilservice.model.Skill;
import com.skillverse.profilservice.service.ProfileService;
import com.skillverse.profilservice.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final SkillService skillService;

    @GetMapping("/health")
    public String health() {
        return "Profile Service is up and running";
    }

    // Créer un profil
    @PostMapping
    public Profile createProfile(@RequestBody Profile profile) {
        return profileService.createProfile(profile);
    }

    // Récupérer un profil par id
    @GetMapping("/{id}")
    public Profile getProfile(@PathVariable Long id) {
        return profileService.getProfileById(id);
    }

    // Récupérer un profil par email
    @GetMapping("/email/{email}")
    public Profile getProfileByEmail(@PathVariable String email) {
        return profileService.getProfileByEmail(email);
    }

    // Récupérer tous les profils
    @GetMapping
    public List<Profile> getAllProfiles() {
        return profileService.getAllProfiles();
    }

    // Mettre à jour un profil
    @PutMapping("/{id}")
    public Profile updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
        return profileService.updateProfile(id, profile);
    }

    // Supprimer un profil
    @DeleteMapping("/{id}")
    public void deleteProfile(@PathVariable Long id) {
        profileService.deleteProfile(id);
    }

    // Ajouter une compétence au profil (nouvelle version avec skillId)
    @PostMapping("/{id}/skills")
    public Profile addSkill(@PathVariable Long id, @RequestBody Map<String, Long> skillIdMap) {
        Long skillId = skillIdMap.get("id"); // récupérer l'id depuis le body
        return profileService.addSkillToProfile(id, skillId);
    }

    // Ajouter un badge au profil
    @PostMapping("/{id}/badges")
    public Profile addBadge(@PathVariable Long id, @RequestBody Badge badge) {
        return profileService.addBadgeToProfile(id, badge);
    }

    // Valider une compétence et ajouter automatiquement un badge
    @PutMapping("/skills/{skillId}/validate")
    public Skill validateSkill(
            @PathVariable Long skillId,
            @RequestParam Long profileId) { // profileId en paramètre
        return skillService.validateSkill(skillId, profileId);
    }
}
