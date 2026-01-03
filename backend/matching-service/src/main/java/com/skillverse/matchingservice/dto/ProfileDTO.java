package com.skillverse.matchingservice.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileDTO {
    private Long userId;
    private String name;
    private List<String> skills;
    private List<String> learningInterests;
}
