package com.skillverse.profilservice.controller;

import com.skillverse.profilservice.model.Skill;
import com.skillverse.profilservice.service.SkillService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @PostMapping
    public Skill createSkill(@RequestBody Skill skill) {
        return skillService.createSkill(skill);
    }

    @GetMapping
    public List<Skill> getAllSkills() {
        return skillService.getAllSkills();
    }

   @PutMapping("/{skillId}/validate/{profileId}")
   public Skill validateSkill(@PathVariable Long skillId, @PathVariable Long profileId) {
       return skillService.validateSkill(skillId, profileId);
   }
   @DeleteMapping("/{id}")
   public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
       skillService.deleteSkill(id);
       return ResponseEntity.noContent().build();
   }
}
