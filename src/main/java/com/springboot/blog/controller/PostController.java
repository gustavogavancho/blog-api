package com.springboot.blog.controller;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
import com.springboot.blog.utils.AppConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("api/posts")
@Tag(name = "CRUD REST APIs for Post Resource")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ResponseEntity<PostResponse> getAllPosts(
            @RequestParam(value = "pageNo", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ){

        return ResponseEntity.ok(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir));
    }

    @Operation(summary = "Get Post By Id REST API", description = "Get post by id REST API is used to get single post from the database")
    @ApiResponse(responseCode = "200", description = "Http Status 200 OK")
    @GetMapping("{id}")
    public ResponseEntity<PostDto> getPostById(@PathVariable Long id) {

        return ResponseEntity.ok(postService.getPostById(id));
    }

    @Operation(summary = "Create Post REST API", description = "Create post REST API is used to save post into database")
    @ApiResponse(responseCode = "201", description = "Http Status 201 CREATED")
    @SecurityRequirement(name = "Bear Authentication")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDto> createPost(@Valid @RequestBody  PostDto postDto) {

        return new ResponseEntity<>(postService.createPost(postDto), HttpStatus.CREATED);
    }

    @SecurityRequirement(name = "Bear Authentication")
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PostDto> updatePost(@PathVariable Long id, @Valid @RequestBody PostDto postDto) {

        return ResponseEntity.ok(postService.updatePost(id, postDto));
    }

    @SecurityRequirement(name = "Bear Authentication")
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {

        postService.deletePost(id);

        return ResponseEntity.ok("Entity deleted successfully");
    }

    @GetMapping("categories/{id}")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@PathVariable  Long id) {

        var postDtos = postService.getPostsByCategory(id);

        return ResponseEntity.ok(postDtos);
    }
}