package com.renatobonfim.aemblogbackend.post;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRespository extends JpaRepository<Post, String> {

    Optional<Post> findPostByIdOrPermalink(String postPermalinkOrID);
    //List<Post> findAllPostByCategory(Category category);
    
    @Query("SELECT p FROM Post p JOIN FETCH p.categories")
    Page<Post> findAllWithCategories(Pageable pageable);
}
