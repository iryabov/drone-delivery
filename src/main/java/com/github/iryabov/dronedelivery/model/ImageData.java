package com.github.iryabov.dronedelivery.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageData extends ImageMetaInfo {
    private byte[] data;
}
