package com.skillverse.profilservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId; // Link to Auth Service (Keycloak/User UUID)
    private String username;
    private String email;
    private String role; // apprenant, enseignant, mentor
    private String bio;
    private String photoUrl;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profile_skills", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "skill_id"))
    private List<Skill> skills;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "profile_badges", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "badge_id"))
    private List<Badge> badges;
}
