package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.CustomDuplicateTitleException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final ModelMapper mapper;

    @Override
    public PostResponse getAllPosts(Integer pageNo, Integer pageSize, String sortBy, String sortDir) {

        var sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        var posts = postRepository.findAll(PageRequest.of(pageNo, pageSize, sort));
        var content = posts.getContent().stream().map(this::mapToDto).toList();
        var postResponse = new PostResponse(content, posts.getNumber(), posts.getSize(), posts.getSize(), posts.getTotalPages(), posts.isLast());

        return  postResponse;
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

        if (postRepository.existsByTitle(postDto.getTitle())) {
            throw new CustomDuplicateTitleException("A post with the title '" + postDto.getTitle() + "' already exists.");
        }

        if (post.getComments() != null) {
            post.getComments().forEach(comment -> comment.setPost(post));
        }
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

        return mapper.map(post, PostDto.class);
    }

    private Post mapToEntity(PostDto postDto) {

        return mapper.map(postDto, Post.class);
    }
}
