package com.smart_interview_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String name;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @JsonIgnore
        @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
        private CandidateProfile candidateProfile;

        @JsonIgnore
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
                cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Skill> skills = new ArrayList<>();

        @JsonIgnore
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
                cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Experience> experiences = new ArrayList<>();

        @JsonIgnore
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY,
                cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Education> educations = new ArrayList<>();
}
