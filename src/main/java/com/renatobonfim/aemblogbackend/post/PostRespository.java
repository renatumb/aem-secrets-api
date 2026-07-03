package com.renatobonfim.aemblogbackend.post;

import java.util.List;
import java.util.Optional;

import com.renatobonfim.aemblogbackend.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRespository extends JpaRepository<Post, String> {

    Optional<Post> findPostById(String postID);
    Optional<Post> findPostByPermalink(String postID);
    
    List<Post> findByCategories(Category category);
    
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByHighlight(Boolean highlight, Pageable pageable);
}
