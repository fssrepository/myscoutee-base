package com.raxim.myscoutee.domain.media.api;

import org.springframework.web.multipart.MultipartFile;

public interface MediaStorage extends MediaCleanup, ImageUploads {
    String storeImage(String ownerId, String entityId, MultipartFile image);
    String storeAudio(String ownerId, String entityId, MultipartFile audio);
    String storeDocument(String ownerId, String entityId, MultipartFile file);
    String importImage(String ownerId, String entityId, String imageUrl);
    StoredImageSet importImageSet(String ownerId, String entityId, String imageUrl);
    String storeImageBytes(
            String ownerId,
            String entityId,
            String originalFilename,
            String contentTypeValue,
            byte[] bytes);
    StoredImageSet storeImageBytesSet(
            String ownerId,
            String entityId,
            String originalFilename,
            String contentTypeValue,
            byte[] bytes);
    StoredMediaObject loadPublicObject(String key);
    boolean isEnabled();
    String managedImageSetGroupKey(String mediaUrlOrKey);
    record StoredMediaObject(
            byte[] bytes,
            String contentType) {
        @Override
        public boolean equals(Object value) {
            return this == value || value instanceof StoredMediaObject other
                    && java.util.Arrays.equals(bytes, other.bytes)
                    && java.util.Objects.equals(contentType, other.contentType);
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = 31 * result + java.util.Arrays.hashCode(bytes);
            result = 31 * result + java.util.Objects.hashCode(contentType);
            return result;
        }

        @Override
        public String toString() {
            return "StoredMediaObject[redacted]";
        }
    }

    record StoredImageSet(
            String imageUrl,
            String originalUrl,
            String smallUrl,
            String mediumUrl,
            String largeUrl) {
    }

}
