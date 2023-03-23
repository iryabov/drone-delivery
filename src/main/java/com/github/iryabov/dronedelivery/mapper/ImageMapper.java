package com.github.iryabov.dronedelivery.mapper;

import com.github.iryabov.dronedelivery.entity.Image;
import com.github.iryabov.dronedelivery.model.ImageData;
import com.github.iryabov.dronedelivery.model.ImageMetaInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageMapper {
    public Image toEntity(MultipartFile file) {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setType(file.getContentType());
        return image;
    }

    public ImageMetaInfo toImageMetaInfo(Image entity) {
        ImageMetaInfo info = new ImageMetaInfo();
        toImageMetaInfo(entity, info);
        return info;
    }

    public ImageData toImageData(Image entity) {
        ImageData imageData = new ImageData();
        toImageMetaInfo(entity, imageData);
        imageData.setData(entity.getData());
        return imageData;
    }

    private void toImageMetaInfo(Image entity, ImageMetaInfo model) {
        model.setImageId(entity.getId());
        model.setName(entity.getName());
        model.setType(entity.getType());
        model.setDownloadUrl("/api/image/download/" + entity.getId());
    }
}
