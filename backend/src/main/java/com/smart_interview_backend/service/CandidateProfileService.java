package com.smart_interview_backend.service;

import com.smart_interview_backend.dto.CandidateProfileRequest;
import com.smart_interview_backend.dto.CandidateProfileResponse;
import com.smart_interview_backend.entity.CandidateProfile;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.CandidateProfileRepository;
import com.smart_interview_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CandidateProfileService {

    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;

    public CandidateProfileService(
            CandidateProfileRepository profileRepository,
            UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CandidateProfileResponse createOrUpdateProfile(
            CandidateProfileRequest request, String email) {

        User user = getUser(email);

        CandidateProfile profile = profileRepository.findByUser_Id(user.getId())
                .orElseGet(CandidateProfile::new);

        profile.setUser(user);
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setLocation(request.getLocation());
        profile.setSummary(request.getSummary());
        profile.setDateOfBirth(request.getDateOfBirth());

        return toResponse(profileRepository.save(profile));
    }

    @Transactional(readOnly = true)
    public CandidateProfileResponse getProfile(String email) {
        User user = getUser(email);

        CandidateProfile profile = profileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return toResponse(profile);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private CandidateProfileResponse toResponse(CandidateProfile p) {
        return new CandidateProfileResponse(
                p.getId(), p.getUser().getId(), p.getFullName(), p.getPhone(),
                p.getLocation(), p.getSummary(), p.getDateOfBirth());
    }
}
