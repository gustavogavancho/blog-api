package com.springboot.blog.service;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse getAllPosts(Integer pageNo, Integer pageSize, String sortBy, String sortDir);
    PostDto getPostById(Long id);
    PostDto createPost(PostDto postDto);
    PostDto updatePost(Long id, PostDto postDto);
    void deletePost(Long id);
    List<PostDto> getPostsByCategory(Long id);
}
