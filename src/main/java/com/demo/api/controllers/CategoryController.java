package com.demo.api.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.demo.api.exception.ResourceNotFoundException;
import com.demo.api.models.Category;
import com.demo.api.models.Post;
import com.demo.api.repositories.CategoryRepository;
import com.demo.api.services.PostService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/categories")
public class CategoryController {
    @Autowired
    private PostService postService;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/get")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = new ArrayList<>();

        categoryRepository.findAll().forEach(categories::add);

        if (categories.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<List<Category>>(categories, HttpStatus.OK);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable(value = "id") Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category with id = " + id + " not found."));

        return new ResponseEntity<>(category, HttpStatus.OK);
    }

    @GetMapping("/get/{categoryId}/posts")
    public ResponseEntity<List<Post>> getPostsByCategoryId(@PathVariable(value = "categoryId") Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category with id = " + categoryId + " not found.");
        }

        return new ResponseEntity<>(postService.findByCategoryId(categoryId), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        try {
            if (!categoryRepository.existsByName(category.getName())) {
                Category _category = new Category();

                _category.setName(category.getName());

                return new ResponseEntity<>(categoryRepository.save(_category), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable(value = "id") Long id,
            @RequestBody Category category) {
        try {
            Category _category = categoryRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with id = " + id + " not found."));

            _category.setName(category.getName());

            return new ResponseEntity<>(categoryRepository.save(_category), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable(value = "id") Long id) {
        try {
            categoryRepository.deleteById(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
