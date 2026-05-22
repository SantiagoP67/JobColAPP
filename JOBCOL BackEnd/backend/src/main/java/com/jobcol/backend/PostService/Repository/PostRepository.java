package com.jobcol.backend.PostService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.PostService.model.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

    
}
