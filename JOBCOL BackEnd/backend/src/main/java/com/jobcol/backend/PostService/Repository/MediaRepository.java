package com.jobcol.backend.PostService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.PostService.model.Media;

public interface MediaRepository extends JpaRepository<Media, Long> {
    
}
