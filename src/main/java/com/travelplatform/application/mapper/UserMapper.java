package com.travelplatform.application.mapper;

import com.travelplatform.application.dto.request.user.RegisterUserRequest;
import com.travelplatform.application.dto.request.user.UpdateProfileRequest;
import com.travelplatform.application.dto.request.user.UpdatePreferencesRequest;
import com.travelplatform.application.dto.response.user.ProfileResponse;
import com.travelplatform.application.dto.response.user.PreferencesResponse;
import com.travelplatform.application.dto.response.user.UserResponse;
import com.travelplatform.domain.model.user.User;
import com.travelplatform.domain.model.user.UserPreferences;
import com.travelplatform.domain.model.user.UserProfile;
import com.travelplatform.domain.enums.UserRole;
import com.travelplatform.domain.enums.UserStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;

/**
 * Mapper for User domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface UserMapper {

    // Entity to Response DTOs
    default UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole() != null ? user.getRole().name() : null);
        response.setStatus(user.getStatus() != null ? user.getStatus().name() : null);
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }

    List<UserResponse> toUserResponseList(List<User> users);

    default ProfileResponse toProfileResponse(UserProfile profile) {
        if (profile == null) {
            return null;
        }
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setUserId(profile.getUserId());
        response.setFullName(profile.getFullName());
        response.setPhotoUrl(profile.getPhotoUrl());
        response.setBirthDate(profile.getBirthDate());
        response.setGender(profile.getGender() != null ? profile.getGender().name() : null);
        response.setBio(profile.getBio());
        response.setLocation(profile.getLocation());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setDrivingLicenseCategory(profile.getDrivingLicenseCategory() != null ? profile.getDrivingLicenseCategory().name() : null);
        response.setOccupation(profile.getOccupation() != null ? profile.getOccupation().name() : null);
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }

    default PreferencesResponse toPreferencesResponse(UserPreferences preferences) {
        if (preferences == null) {
            return null;
        }
        PreferencesResponse response = new PreferencesResponse();
        response.setId(preferences.getId());
        response.setUserId(preferences.getUserId());
        response.setPreferredDestinations(preferences.getPreferredDestinations());
        response.setBudgetRange(preferences.getBudgetRange() != null ? preferences.getBudgetRange().name() : null);
        response.setTravelStyle(preferences.getTravelStyle() != null ? preferences.getTravelStyle().name() : null);
        response.setInterests(preferences.getInterests());
        response.setEmailNotifications(preferences.isEmailNotifications());
        response.setPushNotifications(preferences.isPushNotifications());
        response.setSmsNotifications(preferences.isSmsNotifications());
        response.setNotificationTypes(stringToMap(preferences.getNotificationTypes()));
        response.setCreatedAt(preferences.getCreatedAt());
        response.setUpdatedAt(preferences.getUpdatedAt());
        return response;
    }

    // Custom mapping method for String to Map<String, Object>
    @Named("stringToMap")
    default Map<String, Object> stringToMap(String json) {
        if (json == null || json.isEmpty()) {
            return new HashMap<>();
        }
        // Simple parsing for notification types string
        // In production, use JSON parser like Jackson or Gson
        Map<String, Object> map = new HashMap<>();
        // This is a placeholder - actual implementation would parse JSON
        return map;
    }

    @Named("mapToString")
    default String mapToString(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "{}";
        }
        // Simple conversion - in production use JSON library
        return map.toString();
    }

    // Request DTOs to Entity
    default User toUserFromRegisterRequest(RegisterUserRequest request, String passwordHash) {
        return new User(
                request.getEmail(),
                passwordHash,
                UserRole.valueOf(request.getRole()));
    }

    default void updateUserProfileFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile) {
        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getPhotoUrl() != null) {
            profile.setPhotoUrl(request.getPhotoUrl());
        }
        if (request.getBirthDate() != null) {
            profile.setBirthDate(request.getBirthDate());
        }
        if (request.getGender() != null) {
            profile.setGender(com.travelplatform.domain.model.user.Gender.valueOf(request.getGender()));
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }
        if (request.getLocation() != null) {
            profile.setLocation(request.getLocation());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDrivingLicenseCategory() != null) {
            profile.setDrivingLicenseCategory(
                    com.travelplatform.domain.model.user.DrivingLicenseCategory
                            .valueOf(request.getDrivingLicenseCategory()));
        }
        if (request.getOccupation() != null) {
            profile.setOccupation(
                    com.travelplatform.domain.model.user.WorkStatus.valueOf(request.getOccupation()));
        }
    }

    default void updateUserPreferencesFromRequest(UpdatePreferencesRequest request,
            @MappingTarget UserPreferences preferences) {
        if (request.getPreferredDestinations() != null) {
            preferences.setPreferredDestinations(request.getPreferredDestinations());
        }
        if (request.getBudgetRange() != null) {
            preferences.setBudgetRange(request.getBudgetRange());
        }
        if (request.getTravelStyle() != null) {
            preferences.setTravelStyle(request.getTravelStyle());
        }
        if (request.getInterests() != null) {
            preferences.setInterests(request.getInterests());
        }
        if (request.getEmailNotifications() != null) {
            preferences.setEmailNotifications(request.getEmailNotifications());
        }
        if (request.getPushNotifications() != null) {
            preferences.setPushNotifications(request.getPushNotifications());
        }
        if (request.getSmsNotifications() != null) {
            preferences.setSmsNotifications(request.getSmsNotifications());
        }
    }
}
