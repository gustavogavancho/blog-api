package com.springboot.blog.service.impl;

import com.springboot.blog.entity.Category;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Override
    public CategoryDto addCategory(CategoryDto categoryDto) {

        var category = modelMapper.map(categoryDto, Category.class);
        var categorySaved = categoryRepository.save(category);

        return modelMapper.map(categorySaved, CategoryDto.class);
    }

    @Override
    public CategoryDto getCategory(Long id) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        return modelMapper.map(category, CategoryDto.class);
    }

    @Override
    public List<CategoryDto> getAllCategories() {

        var categories = categoryRepository.findAll();

        return categories.stream().map((category) -> modelMapper.map(category, CategoryDto.class)).toList();
    }

    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());

        return modelMapper.map(categoryRepository.save(category), CategoryDto.class);
    }

    @Override
    public void deleteCategory(Long id) {

        var category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        categoryRepository.delete(category);
    }
}
