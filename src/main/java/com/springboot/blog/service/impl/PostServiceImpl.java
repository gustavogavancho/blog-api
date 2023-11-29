package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;

    @Override
    public List<PostDto> getAllPosts() {

        var posts = postRepository.findAll();

        return posts.stream().map(this::mapToDto).toList();
    }

    @Override
    public PostDto getPostById(Long id) {

        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        return mapToDto(post);
    }

    @Override
    public PostDto createPost(PostDto postDto) {

        var post = mapToEntity(postDto);
        var newPost = postRepository.save(post);
        var postDtoResponse = mapToDto(newPost);

        return postDtoResponse;
    }

    @Override
    public PostDto updatePost(Long id, PostDto postDto) {

        var postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postEntity.setTitle(postDto.getTitle());
        postEntity.setDescription(postDto.getDescription());
        postEntity.setContent(postDto.getContent());

        var entitySaved = postRepository.save(postEntity);

        return mapToDto(entitySaved);
    }

    @Override
    public void deletePost(Long id) {

        var postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(postEntity);
    }

    private PostDto mapToDto(Post post) {

        var postDto = new PostDto();
        postDto.setTitle(post.getTitle());
        postDto.setDescription(post.getDescription());
        postDto.setContent(post.getContent());

        return postDto;
    }

    private Post mapToEntity(PostDto postDto) {

        var post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        return post;
    }
}
