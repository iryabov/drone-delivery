package com.github.iryabov.droneservice.controller;

import com.github.iryabov.droneservice.model.ImageData;
import com.github.iryabov.droneservice.model.ImageMetaInfo;
import com.github.iryabov.droneservice.service.ImageStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/image")
public class ImageStorageController {

    private ImageStorageService service;

    @Operation(summary = "Upload and resize image to the image store")
    @ApiResponse(responseCode = "200", description = "The image was uploaded")
    @PostMapping("/upload")
    public ImageMetaInfo uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return service.uploadImage(file);
    }

    @Operation(summary = "Download resized image from the image store")
    @ApiResponse(responseCode = "200", description = "The image is being downloaded")
    @GetMapping("/download/{imageId}")
    public @ResponseBody ResponseEntity<byte[]> downloadFile(@Parameter(description = "Identifier of image in the image store") @PathVariable Long imageId) {
        ImageData image = service.downloadImage(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(image.getType()))
                .body(image.getData());
    }
}
