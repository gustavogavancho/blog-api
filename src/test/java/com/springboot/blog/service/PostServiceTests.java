package com.springboot.blog.service;

import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.PostServiceImpl;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTests {

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostServiceImpl postService;

    private EasyRandom generator;

    @BeforeEach
    void setUp() {

        generator = new EasyRandom();
    }

    @Test
    void getPostsSuccessfully() {

        // Arrange
        Integer pageNo = 0, pageSize = 3;
        String sortBy = "id", sortDir = "ASC";
        var pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(sortBy).ascending());

        List<Post> mockPosts = generator.objects(Post.class, pageSize).collect(Collectors.toList());
        Page<Post> mockPage = new PageImpl<>(mockPosts, pageRequest, mockPosts.size());
        when(postRepository.findAll(pageRequest)).thenReturn(mockPage);

        // Act
        PostResponse sut = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        // Assert
        assertThat(sut.getContent()).hasSize(pageSize)
                .allMatch(item -> item instanceof PostDto, "All items should be instances of PostDto");
        assertThat(sut).isNotNull();
        assertThat(sut.getPageNo()).isEqualTo(pageNo);
        assertThat(sut.getPageSize()).isEqualTo(pageSize);
        assertThat(sut.getTotalElements()).isEqualTo(mockPosts.size());
        assertThat(sut.getTotalPages()).isEqualTo(mockPage.getTotalPages());
        assertThat(sut.isLast()).isEqualTo(mockPage.isLast());

        // Verify
        verify(postRepository).findAll(pageRequest);
    }

    @Test
    void getPostByIdSuccessfully() {

        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(generator.nextObject(Post.class)));

        // Act
        var sut = postService.getPostById(anyLong());

        // Assert
        assertThat(sut).isNotNull().isInstanceOf(PostDto.class);

        //Verify
        verify(postRepository).findById(anyLong());
    }

    @Test
    void getPostByIdThrowsException() {

        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act
        Throwable thrown = catchThrowable(() -> postService.getPostById(1L));

        // Assert
        assertThat(thrown).isInstanceOf(ResourceNotFoundException.class);

        //Verify
        verify(postRepository).findById(anyLong());
    }

    @Test
    void createPostSuccessfully() {

        // Arrange
        var inputPostDto = generator.nextObject(PostDto.class);
        Post mockPost = generator.nextObject(Post.class);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        // Act
        var sut = postService.createPost(inputPostDto);

        // Assert
        assertThat(sut).isNotNull().isInstanceOf(PostDto.class);

        // Verify
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePostSuccessfully() {

        // Arrange
        var existingPost = generator.nextObject(Post.class);
        var updatedPostDto = generator.nextObject(PostDto.class);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(existingPost));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var sut = postService.updatePost(anyLong(), updatedPostDto);

        // Assert
        assertThat(sut).isNotNull().isInstanceOf(PostDto.class);

        // Verify
        verify(postRepository).findById(anyLong());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePostThrowsException() {

        // Arrange
        var postDto = generator.nextObject(PostDto.class);
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> postService.updatePost(anyLong(), postDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found id: '0'");

        // Verify
        verify(postRepository).findById(anyLong());
    }

    @Test
    void deletePostSuccessfully() {

        // Arrange
        var post = Optional.of(generator.nextObject(Post.class));

        when(postRepository.findById(anyLong())).thenReturn(post);

        // Act
        postService.deletePost(anyLong());

        // Assert
        verify(postRepository).findById(anyLong());
        verify(postRepository).delete(post.get());
    }

    @Test
    void deletePostThrowsException() {

        // Arrange
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> postService.deletePost(anyLong()))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Post not found id: '0'");

        // Verify
        verify(postRepository).findById(anyLong());
    }
}
