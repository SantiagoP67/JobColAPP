package com.jobcol.backend.PostService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.PostService.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    
}
