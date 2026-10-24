package com.techg.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {

    private static final long MAX_BYTES = 5L * 1024 * 1024;

    private final Cloudinary cloudinary;

    @Value("${cloudinary.folder:techg/products}")
    private String folder;

    public ImageUploadService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /** Uploads an image to Cloudinary and returns its https address. Throws a readable message on problems. */
    public String upload(MultipartFile file) throws IOException {
        return upload(file, folder);
    }

    /** Same as above but stores the image in a chosen Cloudinary folder (e.g. "techg/site"). */
    public String upload(MultipartFile file, String targetFolder) throws IOException {
        String type = file.getContentType();
        if (type == null || !type.startsWith("image/")) {
            throw new IllegalArgumentException("Please choose an image file (JPG, PNG, WebP...).");
        }
        if (file.getSize() > MAX_BYTES) {
            throw new IllegalArgumentException("Image is too large. Maximum size is 5 MB.");
        }
        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", targetFolder,
                "resource_type", "image"));
        Object url = result.get("secure_url");
        if (url == null) {
            throw new IOException("Cloudinary did not return an image address.");
        }
        return url.toString();
    }
}
