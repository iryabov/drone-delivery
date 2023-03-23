package com.github.iryabov.dronedelivery.service.impl;

import com.github.iryabov.dronedelivery.entity.Image;
import com.github.iryabov.dronedelivery.exception.DroneDeliveryException;
import com.github.iryabov.dronedelivery.mapper.ImageMapper;
import com.github.iryabov.dronedelivery.model.ImageData;
import com.github.iryabov.dronedelivery.model.ImageMetaInfo;
import com.github.iryabov.dronedelivery.repository.ImageDataRepository;
import com.github.iryabov.dronedelivery.service.ImageStorageService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Transactional
@AllArgsConstructor
public class ImageStorageServiceImpl implements ImageStorageService {
    private static final Integer RESIZE_WIDTH = 300;
    private ImageDataRepository repo;
    private ImageMapper mapper;
    public ImageMetaInfo uploadImage(MultipartFile file) throws IOException {
        Image imageData = mapper.toEntity(file);

        BufferedImage image = ImageIO.read(file.getInputStream());
        BufferedImage resizedImage = resizeImage(image, RESIZE_WIDTH, RESIZE_WIDTH);
        imageData.setData(toByteArray(resizedImage, getFormat(file.getContentType())));

        Image createdImage = repo.save(imageData);
        return mapper.toImageMetaInfo(createdImage);
    }

    public ImageData downloadImage(Long imageId) {
        Image entity = repo.findById(imageId).orElseThrow();
        return mapper.toImageData(entity);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        java.awt.Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, java.awt.Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return outputImage;
    }

    private byte[] toByteArray(BufferedImage bi, String format)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bi, format, baos);
        return baos.toByteArray();
    }

    private String getFormat(String contentType) {
        MediaType mediaType = MediaType.parseMediaType(contentType);
        if (!mediaType.getType().equals("image"))
            throw new DroneDeliveryException("Incorrect content type");
        return mediaType.getSubtype();
    }
}
