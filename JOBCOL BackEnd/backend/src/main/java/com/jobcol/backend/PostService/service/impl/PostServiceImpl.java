package com.jobcol.backend.PostService.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jobcol.backend.PostService.Repository.CommentRepository;
import com.jobcol.backend.PostService.Repository.LikeRepository;
import com.jobcol.backend.PostService.Repository.MediaRepository;
import com.jobcol.backend.PostService.Repository.PostRepository;
import com.jobcol.backend.PostService.model.Comment;
import com.jobcol.backend.PostService.model.Like;
import com.jobcol.backend.PostService.model.Media;
import com.jobcol.backend.PostService.model.Post;
import com.jobcol.backend.PostService.service.PostService;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.PostDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository usuarioRepository;
    private final MediaRepository mediaRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;

    @Override
    public PostDTO createPost(
            Long userId,
            String description,
            List<MultipartFile> files
    ) {

        User user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Post post = new Post();

        post.setDescription(description);
        post.setCreatedAt(LocalDateTime.now());
        post.setUsuario(user);

        post = postRepository.save(post);

        List<Media> mediaList = new ArrayList<>();

        if (files != null && !files.isEmpty()) {

            for (MultipartFile file : files) {

                try {

                    if (file.isEmpty()) {
                        continue;
                    }

                    String fileName = System.currentTimeMillis()
                            + "_" + file.getOriginalFilename();

                    Path uploadPath = Paths.get("uploads");

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    Path filePath = uploadPath.resolve(fileName);

                    Files.copy(
                            file.getInputStream(),
                            filePath,
                            StandardCopyOption.REPLACE_EXISTING
                    );

                    Media media = new Media();

                    media.setUrl("http://localhost:8080/uploads/" + fileName);

                    media.setType(
                            file.getContentType() != null
                                    ? file.getContentType()
                                    : "IMAGE"
                    );

                    media.setPost(post);

                    mediaList.add(media);

                } catch (Exception e) {
                    throw new RuntimeException("Error subiendo archivo");
                }
            }

            mediaRepository.saveAll(mediaList);
        }

        post.setMediaList(mediaList);

        return mapToDTO(post);
    }

    @Override
    public List<PostDTO> getAllPosts() {

        return postRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    @Override
    public PostDTO getPostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        return mapToDTO(post);
    }

    @Override
    public void likePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        User user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        boolean exists = likeRepository.existsByUsuarioAndPost(user, post);

        if (!exists) {

            Like like = new Like();

            like.setUsuario(user);
            like.setPost(post);

            likeRepository.save(like);
        }
    }

    @Override
    public void commentPost(Long postId, Long userId, String content) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post no encontrado"));

        User user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comment comment = new Comment();

        comment.setContent(content);
        comment.setUsuario(user);
        comment.setPost(post);

        commentRepository.save(comment);
    }

    private PostDTO mapToDTO(Post post) {

        PostDTO dto = new PostDTO();

        dto.id = post.getId();
        dto.description = post.getDescription();
        dto.username = post.getUsuario().getEmail();
        dto.createdAt = post.getCreatedAt();

        if (post.getMediaList() != null) {

            dto.mediaUrls = post.getMediaList()
                    .stream()
                    .map(Media::getUrl)
                    .toList();

        } else {

            dto.mediaUrls = new ArrayList<>();
        }

        dto.likes = likeRepository.countByPost(post);

        return dto;
    }
}