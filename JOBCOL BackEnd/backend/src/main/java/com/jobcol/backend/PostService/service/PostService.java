package com.jobcol.backend.PostService.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.jobcol.backend.shared.dto.PostDTO;

public interface PostService {
    PostDTO createPost(Long userId,String description,List<MultipartFile> files);

    List<PostDTO> getAllPosts();

    PostDTO getPostById(Long postId);

    void likePost(Long postId, Long userId);

    void commentPost(Long postId, Long userId, String content);
}
