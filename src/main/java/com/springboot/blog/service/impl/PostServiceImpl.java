package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.CustomDuplicateTitleException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
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

        if (postRepository.existsByTitle(postDto.getTitle())) {
            throw new CustomDuplicateTitleException("A post with the title '" + postDto.getTitle() + "' already exists.");
        }

        var category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        var post = mapToEntity(postDto);
        post.setCategory(category);
        var newPost = postRepository.save(post);
        var postDtoResponse = mapToDto(newPost);

        return postDtoResponse;
    }

    @Override
    public PostDto updatePost(Long id, PostDto postDto) {

        var post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        var category = categoryRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postDto.getCategoryId()));

        post.setCategory(category);
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setContent(postDto.getContent());

        var entitySaved = postRepository.save(post);

        return mapToDto(entitySaved);
    }

    @Override
    public void deletePost(Long id) {

        var postEntity = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));

        postRepository.delete(postEntity);
    }

    @Override
    public List<PostDto> getPostsByCategory(Long id) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        var postDtos = postRepository.findByCategoryId(id);

        return postDtos.stream().map((post) -> mapToDto(post)).toList();
    }

    private PostDto mapToDto(Post post) {

        return mapper.map(post, PostDto.class);
    }

    private Post mapToEntity(PostDto postDto) {

        return mapper.map(postDto, Post.class);
    }
}
