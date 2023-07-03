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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.api.exception.ResourceNotFoundException;
import com.demo.api.models.Category;
import com.demo.api.models.Post;
import com.demo.api.repositories.CategoryRepository;
import com.demo.api.repositories.PostRepository;
import com.demo.api.services.PostService;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1/posts")
public class PostController {
    @Autowired
    PostRepository postRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    PostService postService;

    @GetMapping("/get")
    public ResponseEntity<List<Post>> getAllPosts(@RequestParam(required = false) String title) {
        List<Post> posts = new ArrayList<>();

        if (title == null || title.length() <= 0) {
            postService.findAll().forEach(posts::add);
        } else {
            postService.findByTitleContaining(title).forEach(posts::add);
        }

        if (posts.isEmpty()) {
            return new ResponseEntity<List<Post>>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
        }
    }

    @GetMapping("/get/{author}")
    public ResponseEntity<List<Post>> getPostByAuthor(@PathVariable("author") String author) {
        List<Post> posts = new ArrayList<>();

        try {
            postService.findByAuthor(author);

            return new ResponseEntity<List<Post>>(posts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}/categories")
    public ResponseEntity<List<Category>> getCategoriesByPostId(@PathVariable("id") Long id) {
        if (!postRepository.existsById(id)) {
            throw new ResourceNotFoundException("Post with id = " + id + " not found.");
        }

        return new ResponseEntity<>(categoryRepository.findCategoriesByPostId(id), HttpStatus.OK);
    }

    @GetMapping("/get/published")
    public ResponseEntity<List<Post>> findByPublished() {
        try {
            return new ResponseEntity<>(postService.findByPublished(true), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Post> findById(@PathVariable("id") Long id) {
        Post post = postService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post with id = " + id + " not found."));

        return new ResponseEntity<Post>(post, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        try {
            Post _post = new Post(post.getTitle(), post.getContent(), post.getAuthor());

            return new ResponseEntity<>(postRepository.save(_post), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<Post>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}/published")
    public ResponseEntity<String> updatePublished(@PathVariable("id") Long id) {
        try {
            Post _post = postService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Post with id = " + id + " not found."));

            if (_post.isPublished()) {
                _post.setPublished(false);
                postService.updatePost(_post);
                return new ResponseEntity<>("Post is unpublished", HttpStatus.OK);
            } else {
                _post.setPublished(true);
                postService.updatePost(_post);
                return new ResponseEntity<String>("Post is now published", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable("id") Long id, @RequestBody Post post) {
        try {
            Post _post = postService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Post with id = " + id + " not found."));

            _post.setTitle(post.getTitle());
            _post.setContent(post.getContent());

            postService.updatePost(_post);
            return new ResponseEntity<>(_post, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{id}/categories/add")
    public ResponseEntity<Post> addCategoryToPost(@PathVariable(value = "id") Long id, @RequestBody String name) {
        try {
            Post _post = postService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Post with id = " + id + " not found."));

            Category _category = categoryRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + name + " not found."));

            if (!_post.getCategories().contains(_category)) {
                _post.getCategories().add(_category);
                _category.getPosts().add(_post);
                categoryRepository.save(_category);
                return new ResponseEntity<Post>(postRepository.save(_post), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("update/{id}/categories/remove")
    public ResponseEntity<Post> removeCategoryFromPost(@PathVariable(value = "id") Long id, @RequestBody String name) {
        try {
            Post _post = postService.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Post with id = " + id + " not found."));

            Category _category = categoryRepository.findByName(name)
                    .orElseThrow(() -> new ResourceNotFoundException("Category with name: " + name + " not found."));

            if (_post.getCategories().contains(_category)) {
                _post.removeCategory(_category.getId());
                _category.getPosts().remove(_post);
                categoryRepository.save(_category);
                return new ResponseEntity<>(postRepository.save(_post), HttpStatus.OK);
            } else {
                throw new ResourceNotFoundException("Post with id = " + id + " is not categorized by category " + name);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> deleteAllPosts() {
        try {
            postService.deleteAll();

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deletePostById(@PathVariable("id") Long id) {
        try {
            postService.deleteById(id);

            return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
