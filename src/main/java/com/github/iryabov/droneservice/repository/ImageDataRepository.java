package com.github.iryabov.droneservice.repository;

import com.github.iryabov.droneservice.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageDataRepository extends JpaRepository<Image, Long> {
}
