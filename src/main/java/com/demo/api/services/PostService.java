package com.demo.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Service;

import com.demo.api.models.Post;
import com.demo.api.repositories.PostRepository;

@Service
@EnableCaching
public class PostService {
    @Autowired
    PostRepository postRepository;

    @Cacheable(value = "posts")
    public List<Post> findAll() {
        doLongRunningTask();

        return postRepository.findAll();
    }

    @Cacheable(value = "posts")
    public List<Post> findByTitleContaining(String title) {
        doLongRunningTask();

        return postRepository.findByTitleContaining(title);
    }

    @Cacheable(value = "posts")
    public List<Post> findByAuthor(String author) {
        doLongRunningTask();

        return postRepository.findByAuthor(author);
    }

    @Cacheable(value = "posts")
    public List<Post> findByCategoryId(Long categoryId) {
        doLongRunningTask();

        return postRepository.findByCategoryId(categoryId);
    }

    @Cacheable(value = "post")
    public Optional<Post> findById(Long id) {
        doLongRunningTask();

        return postRepository.findById(id);
    }

    @Cacheable(value = "published_posts")
    public List<Post> findByPublished(boolean isPublished) {
        doLongRunningTask();

        return postRepository.findByPublished(isPublished);
    }

    @CacheEvict(value = "post", key = "#post.id", condition = "#post.published")
    public Post updatePost(Post post) {
        return postRepository.save(post);
    }

    @CacheEvict(value = "post", key = "#id")
    public void deleteById(Long id) {
        postRepository.deleteById(id);
    }

    @CacheEvict(value = { "post", "posts", "published_posts" }, allEntries = true)
    public void deleteAll() {
        postRepository.deleteAll();
    }

    private void doLongRunningTask() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
