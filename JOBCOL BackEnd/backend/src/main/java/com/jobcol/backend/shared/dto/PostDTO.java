package com.jobcol.backend.shared.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDTO {
    public Long id;
    public String description;
    public String username;
    public LocalDateTime createdAt;
    public List<String> mediaUrls;
    public int likes;
}
