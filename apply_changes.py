import sys
import os

def replace_in_file(file_path, start_marker, end_marker, replacement):
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    start_index = content.find(start_marker)
    if start_index == -1:
        print(f"Start marker not found in {file_path}")
        return False
    
    end_index = content.find(end_marker, start_index + len(start_marker))
    if end_index == -1:
        print(f"End marker not found in {file_path}")
        return False
    
    # We want to replace from start of start_marker to end of end_marker
    new_content = content[:start_index] + replacement + content[end_index + len(end_marker):]
    
    with open(file_path, 'w', encoding='utf-8', newline='\n') as f:
        f.write(new_content)
    print(f"Successfully updated {file_path}")
    return True

# UserMapper.java replacements
user_mapper_path = r'd:\Traveling-Project\backend-Travel-booking\backend\src\main\java\com\travelplatform\application\mapper\UserMapper.java'

to_profile_response_old_start = "    default ProfileResponse toProfileResponse(UserProfile profile) {"
to_profile_response_old_end = "        return response;\n    }"
to_profile_response_new = """    default ProfileResponse toProfileResponse(UserProfile profile) {
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
        response.setStripeConnectAccountId(profile.getStripeConnectAccountId());
        response.setBankName(profile.getBankName());
        response.setBankAccountIban(profile.getBankAccountIban());
        response.setBankAccountBic(profile.getBankAccountBic());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }"""

update_user_profile_old_start = "    default void updateUserProfileFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile) {"
update_user_profile_old_end = '                    com.travelplatform.domain.model.user.WorkStatus.valueOf(request.getOccupation()));\n        }\n    }'
update_user_profile_new = """    default void updateUserProfileFromRequest(UpdateProfileRequest request, @MappingTarget UserProfile profile) {
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
        if (request.getBankName() != null) {
            profile.setBankName(request.getBankName());
        }
        if (request.getBankAccountIban() != null) {
            profile.setBankAccountIban(request.getBankAccountIban());
        }
        if (request.getBankAccountBic() != null) {
            profile.setBankAccountBic(request.getBankAccountBic());
        }
    }"""

replace_in_file(user_mapper_path, to_profile_response_old_start, to_profile_response_old_end, to_profile_response_new)
replace_in_file(user_mapper_path, update_user_profile_old_start, update_user_profile_old_end, update_user_profile_new)

# UserService.java replacements
user_service_path = r'd:\Traveling-Project\backend-Travel-booking\backend\src\main\java\com\travelplatform\application\service\user\UserService.java'

update_profile_old_start = "    @Transactional\n    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {"
update_profile_old_end = "        return userMapper.toProfileResponse(user.getProfile());\n    }"
update_profile_new = """    @Transactional
    public ProfileResponse updateProfile(UUID userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Validate update request
        userValidator.validateProfileUpdate(request);

        // Update profile
        UserProfile profile = user.getProfile();
        if (profile == null) {
            profile = new UserProfile(user.getId());
            user.setProfile(profile);
        }

        userMapper.updateProfileFromRequest(request, profile);

        // Security: Only Suppliers or Association Managers can set bank details
        if (request.getBankName() != null || request.getBankAccountIban() != null || request.getBankAccountBic() != null) {
            if (user.getRole() != UserRole.SUPPLIER_SUBSCRIBER && user.getRole() != UserRole.ASSOCIATION_MANAGER) {
                // Mandatory restriction as per RBAC v3.0 strategy
                profile.setBankName(null);
                profile.setBankAccountIban(null);
                profile.setBankAccountBic(null);
                throw new IllegalArgumentException("Only Suppliers and Association Managers can configure payout bank details");
            }
        }

        // Save updated user
        userRepository.save(user);

        return userMapper.toProfileResponse(user.getProfile());
    }"""

replace_in_file(user_service_path, update_profile_old_start, update_profile_old_end, update_profile_new)
