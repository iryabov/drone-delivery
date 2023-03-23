package com.github.iryabov.dronedelivery.repository;

import com.github.iryabov.dronedelivery.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDataRepository extends JpaRepository<Image, Long> {
}
