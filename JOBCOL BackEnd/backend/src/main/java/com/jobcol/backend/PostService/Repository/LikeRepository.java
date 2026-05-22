package com.jobcol.backend.PostService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.PostService.model.Like;
import com.jobcol.backend.PostService.model.Post;
import com.jobcol.backend.UserService.model.User;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByUsuarioAndPost(User usuario, Post post);

    int countByPost(Post post);
    
}
