package com.github.iryabov.dronedelivery.service;

import com.github.iryabov.dronedelivery.model.ImageData;
import com.github.iryabov.dronedelivery.model.ImageMetaInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Service for storing images
 */
public interface ImageStorageService {
    /**
     * Upload and resize image
     * @param file File of image
     * @return Meta information about uploaded image
     * @throws IOException Cannot upload an image
     */
    ImageMetaInfo uploadImage(MultipartFile file) throws IOException;

    /**
     * Download image from image storage
     * @param imageId Image identifier
     * @return Image data and metadata
     */
    ImageData downloadImage(Long imageId);
}
