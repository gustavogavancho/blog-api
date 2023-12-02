package com.springboot.blog.payload;

import lombok.*;

import java.util.Set;

@Data
public class PostDto {

    private String title;
    private String description;
    private String content;
    private Set<CommentDto> comments;
}
