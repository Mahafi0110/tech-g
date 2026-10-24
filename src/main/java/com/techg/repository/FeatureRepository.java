package com.techg.repository;

import com.techg.entity.FeatureItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<FeatureItem, Long> {
}
