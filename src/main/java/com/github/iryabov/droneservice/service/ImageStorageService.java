package com.github.iryabov.droneservice.service;

import com.github.iryabov.droneservice.model.ImageData;
import com.github.iryabov.droneservice.model.ImageMetaInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {
    ImageMetaInfo uploadImage(MultipartFile file) throws IOException;
    ImageData downloadImage(Long imageId);
}
