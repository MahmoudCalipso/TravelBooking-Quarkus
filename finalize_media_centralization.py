import sys

def update_file(path, injections):
    with open(path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # Sort injections by line number in descending order to avoid index issues
    for line_num, content in sorted(injections.items(), key=lambda x: x[0], reverse=True):
        if line_num == -1: # End of file
            # Find the last closing brace
            for i in range(len(lines) - 1, -1, -1):
                if lines[i].strip() == "}":
                    lines[i] = content + "}\n"
                    break
        else:
            lines.insert(line_num, content)
            
    with open(path, 'w', encoding='utf-8', newline='\n') as f:
        f.writelines(lines)

# AccommodationService
acc_path = r'd:\Traveling-Project\backend-Travel-booking\backend\src\main\java\com\travelplatform\application\service\accommodation\AccommodationService.java'
acc_injections = {
    53: "    @Inject\n    com.travelplatform.domain.repository.MediaAssetRepository mediaAssetRepository;\n\n",
    133: """                String imageUrl = request.getImages().get(i);
                validateMediaAsset(imageUrl, com.travelplatform.domain.model.media.MediaAsset.OwnerType.ACCOMMODATION);
""",
    -1: """    private void validateMediaAsset(String publicUrl, com.travelplatform.domain.model.media.MediaAsset.OwnerType ownerType) {
        if (publicUrl == null) return;
        
        var asset = mediaAssetRepository.findByPublicUrl(publicUrl);
        if (asset.isEmpty()) {
            if (publicUrl.contains("storage.googleapis.com")) {
                 throw new IllegalArgumentException("Media asset must be confirmed in the system before use: " + publicUrl);
            } else {
                 throw new IllegalArgumentException("Only Firebase Storage URLs are allowed for media: " + publicUrl);
            }
        }
    }
"""
}

# ReelService
reel_path = r'd:\Traveling-Project\backend-Travel-booking\backend\src\main\java\com\travelplatform\application\service\reel\ReelService.java'
reel_injections = {
    44: "    @Inject\n    com.travelplatform.domain.repository.MediaAssetRepository mediaAssetRepository;\n\n",
    82: """        // Centralization Check: Ensure video and thumbnail are valid MediaAssets
        validateMediaAsset(request.getVideoUrl(), com.travelplatform.domain.model.media.MediaAsset.OwnerType.TRAVEL_REEL);
        validateMediaAsset(request.getThumbnailUrl(), com.travelplatform.domain.model.media.MediaAsset.OwnerType.TRAVEL_REEL);
""",
    -1: """    private void validateMediaAsset(String publicUrl, com.travelplatform.domain.model.media.MediaAsset.OwnerType ownerType) {
        if (publicUrl == null) return;
        
        var asset = mediaAssetRepository.findByPublicUrl(publicUrl);
        if (asset.isEmpty()) {
            if (publicUrl.contains("storage.googleapis.com")) {
                 throw new IllegalArgumentException("Media asset must be confirmed in the system before use: " + publicUrl);
            } else {
                 throw new IllegalArgumentException("Only Firebase Storage URLs are allowed for media: " + publicUrl);
            }
        }
    }
"""
}

# Need to adjust for existing lines in createAccommodation
# Original line 134 was AccommodationImage image = new AccommodationImage(accommodation.getId(), request.getImages().get(i), i, i == 0, request.getTitle());
# I want to change request.getImages().get(i) to imageUrl

update_file(acc_path, acc_injections)
update_file(reel_path, reel_injections)

# Also need to fix the call in createAccommodation to use imageUrl
with open(acc_path, 'r', encoding='utf-8') as f:
    content = f.read()
content = content.replace('request.getImages().get(i),', 'imageUrl,')
with open(acc_path, 'w', encoding='utf-8', newline='\n') as f:
    f.write(content)

print("Successfully updated services.")
