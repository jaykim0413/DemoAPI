package com.demo.api.controllers;

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
import com.demo.api.models.Comment;
import com.demo.api.repositories.CommentRepository;
import com.demo.api.repositories.PostRepository;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/v1")
public class CommentController {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping("/posts/get/{id}/comments")
    public ResponseEntity<List<Comment>> getAllCommentsByPostId(@PathVariable(value = "postId") Long postId) {
        try {
            if (!postRepository.existsById(postId)) {
                throw new ResourceNotFoundException("Post with id = " + postId + " not found.");
            }

            return new ResponseEntity<>(commentRepository.findByPostId(postId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/comments/{id}")
    public ResponseEntity<Comment> getCommentById(@PathVariable(value = "id") Long id) {
        try {
            Comment comment = commentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment with id = " + id + " not found."));

            return new ResponseEntity<>(comment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable(value = "postId") Long postId,
            @RequestBody Comment comment) {
        try {
            Comment _comment = postRepository.findById(postId).map(post -> {
                comment.setPost(post);
                return commentRepository.save(comment);
            })
                    .orElseThrow(() -> new ResourceNotFoundException("Post with id = " + postId + " not found."));

            return new ResponseEntity<Comment>(_comment, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping("/comments/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable("id") Long id, @RequestBody Comment comment) {
        try {
            Comment _comment = commentRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment with id = " + id + " not found."));

            _comment.setContent(comment.getContent());
            return new ResponseEntity<Comment>(commentRepository.save(_comment), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<HttpStatus> deleteComment(@PathVariable("id") long id) {
        try {
            commentRepository.deleteById(id);

            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<HttpStatus>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tutorials/{tutorialId}/comments")
    public ResponseEntity<List<Comment>> deleteAllCommentsOfTutorial(
            @PathVariable(value = "tutorialId") Long tutorialId) {
        try {
            if (!postRepository.existsById(tutorialId)) {
                throw new ResourceNotFoundException("Not found Tutorial with id = " + tutorialId);
            }

            commentRepository.deleteByPostId(tutorialId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
