package com.github.iryabov.droneservice.model;

import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;

@Getter
@Setter
public class ImageData extends ImageMetaInfo {
    private byte[] data;
}
