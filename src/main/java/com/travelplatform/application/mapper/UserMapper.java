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

import java.util.UUID;
import java.util.List;

/**
 * Mapper for User domain entities and DTOs.
 */
@Mapper(componentModel = "cdi")
public interface UserMapper {

    // Entity to Response DTOs
    UserResponse toUserResponse(User user);

    List<UserResponse> toUserResponseList(List<User> users);

    ProfileResponse toProfileResponse(UserProfile profile);

    PreferencesResponse toPreferencesResponse(UserPreferences preferences);

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
            preferences.setBudgetRange(
                    com.travelplatform.domain.model.user.UserPreferences.BudgetRange.valueOf(request.getBudgetRange()));
        }
        if (request.getTravelStyle() != null) {
            preferences.setTravelStyle(
                    com.travelplatform.domain.model.user.UserPreferences.TravelStyle.valueOf(request.getTravelStyle()));
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

    @Named("userId")
    default UUID mapUserId(User user) {
        return user != null ? user.getId() : null;
    }

    @Named("userName")
    default String mapUserName(User user) {
        return user != null && user.getProfile() != null ? user.getProfile().getFullName() : null;
    }

    @Named("userPhotoUrl")
    default String mapUserPhotoUrl(User user) {
        return user != null && user.getProfile() != null ? user.getProfile().getPhotoUrl() : null;
    }
}
