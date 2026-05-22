package com.jobcol.backend.PostService.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobcol.backend.PostService.service.PostService;
import com.jobcol.backend.shared.dto.PostDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostDTO> createPost(
            @RequestParam Long userId,
            @RequestParam String description,
            @RequestParam(required = false) List<MultipartFile> files
    ) {

        return ResponseEntity.ok(
                postService.createPost(userId, description, files)
        );
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getAllPosts() {

        return ResponseEntity.ok(postService.getAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long id) {

        return ResponseEntity.ok(postService.getPostById(id));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {

        postService.likePost(id, userId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Void> commentPost(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam String content
    ) {

        postService.commentPost(id, userId, content);

        return ResponseEntity.ok().build();
    }
}