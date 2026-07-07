package com.reportatucalle.modules.media.infrastructure.adapter;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.reportatucalle.modules.media.domain.portsout.StoragePort;
import com.reportatucalle.shared.exception.BusinessException;

import java.io.InputStream;
import java.util.Map;

/**
 * Implementación del StoragePort usando Cloudinary.
 */
public class CloudinaryStorageAdapter implements StoragePort {

    private final Cloudinary cloudinary;

    public CloudinaryStorageAdapter(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(String fileName, byte[] fileData) {
        try {
            // Upload to Cloudinary with the provided fileName as public_id
            Map uploadResult = cloudinary.uploader().upload(fileData, ObjectUtils.asMap(
                    "public_id", "reportatucalle/" + fileName,
                    "resource_type", "auto"
            ));
            
            // Return the secure URL provided by Cloudinary
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new BusinessException("Error subiendo el archivo a Cloudinary", "CLOUDINARY_UPLOAD_ERROR");
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // Extract public_id from the URL to delete it
            // For example: https://res.cloudinary.com/demo/image/upload/v1234567/reportatucalle/file-name.jpg
            // The public_id is "reportatucalle/file-name"
            String publicId = extractPublicId(fileUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            throw new BusinessException("Error eliminando el archivo de Cloudinary", "CLOUDINARY_DELETE_ERROR");
        }
    }
    
    private String extractPublicId(String url) {
        try {
            // Basic extraction: find the last segment and remove extension, prepended with folder
            String[] parts = url.split("/");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                int dotIndex = lastPart.lastIndexOf('.');
                String fileName = dotIndex > 0 ? lastPart.substring(0, dotIndex) : lastPart;
                
                // If it's in our folder, include the folder name
                if (url.contains("/reportatucalle/")) {
                    return "reportatucalle/" + fileName;
                }
                return fileName;
            }
        } catch (Exception e) {
            // Ignore parse errors
        }
        return null;
    }
}
