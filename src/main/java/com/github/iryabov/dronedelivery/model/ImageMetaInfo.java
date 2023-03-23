package com.github.iryabov.dronedelivery.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ImageMetaInfo {
    @Schema(description = "Identifier of image in the image store", example = "1")
    private Long imageId;
    @Schema(description = "Image file name", example = "myimage.jpg")
    private String name;
    @Schema(description = "Image format", example = "image/jpeg")
    private String type;
    @Schema(description = "Relational link to download image", example = "/api/image/download/1")
    private String downloadUrl;
}
