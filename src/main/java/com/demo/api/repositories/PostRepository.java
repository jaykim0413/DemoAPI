package com.demo.api.repositories;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.demo.api.models.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByTitleContaining(String title);

    List<Post> findByAuthor(String author);

    List<Post> findByPublished(boolean published);

    List<Post> findByCategoryId(Long categoryId);
}
