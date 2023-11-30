package com.springboot.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
public class PostControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    private EasyRandom generator;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {

        generator = new EasyRandom();
        mapper = new ObjectMapper();
    }

    @Test
    public void getPostsSuccessfully() throws Exception {
        // Arrange
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "createdAt";
        String sortDir = "asc";

        var mockPosts = generator.objects(PostDto.class, 3).collect(Collectors.toList());
        var postResponse = new PostResponse();
        postResponse.setContent(mockPosts);
        when(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir)).thenReturn(postResponse);

        var postResponseJson = mapper.writeValueAsString(postResponse);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts")
                        .param("pageNo", String.valueOf(pageNo))
                        .param("pageSize", String.valueOf(pageSize))
                        .param("sortBy", sortBy)
                        .param("sortDir", sortDir)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(postResponseJson));
    }

    @Test
    public void getPostByIdSuccessfully() throws Exception {

        // Arrange
        var postDto = generator.nextObject(PostDto.class);
        when(postService.getPostById(anyLong())).thenReturn(postDto);
        var postDtoJson = mapper.writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postDtoJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(postDtoJson));
    }

    @Test
    public void createPostSuccessfully() throws Exception {

        // Arrange
        PostDto postDto = generator.nextObject(PostDto.class);
        when(postService.createPost(any(PostDto.class))).thenReturn(postDto);
        String postDtoJson = new ObjectMapper().writeValueAsString(postDto);

        // Act & Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postDtoJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(postDtoJson));
    }

    @Test
    public void updatePostTest() throws Exception {

        // Arrange
        var originalPostDto = generator.nextObject(PostDto.class);
        var updatedPostDto = generator.nextObject(PostDto.class);
        when(postService.updatePost(anyLong(), any(PostDto.class))).thenReturn(updatedPostDto);
        var postDtoJson = new ObjectMapper().writeValueAsString(originalPostDto);

        // Act & Assert
        mockMvc.perform(put("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(postDtoJson))
                        .andExpect(status().isOk())
                        .andDo(result -> {
                            PostDto returnedPostDto = new ObjectMapper().readValue(result.getResponse().getContentAsString(), PostDto.class);
                            assertThat(returnedPostDto).isEqualToComparingFieldByField(updatedPostDto);
                        });
    }

    @Test
    public void deletePostSuccessfully() throws Exception {

        // Arrange

        // Act & Assert
        mockMvc.perform(delete("/api/posts/1")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.content().string("Entity deleted successfully"));

        //Verify
        verify(postService).deletePost(anyLong());
    }
}
