package com.skillverse.matchingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindMatchesRequest {
    private ProfileDTO userProfile;
    private List<ProfileDTO> allProfiles;
}
