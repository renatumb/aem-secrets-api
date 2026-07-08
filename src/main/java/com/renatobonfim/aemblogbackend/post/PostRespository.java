package com.renatobonfim.aemblogbackend.post;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.renatobonfim.aemblogbackend.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRespository extends JpaRepository<Post, String> {

    Optional<Post> findPostById(String postID);
    Optional<Post> findPostByPermalink(String postID);
    
    List<Post> findByCategories(Category category);
    
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByHighlight(Boolean highlight, Pageable pageable);

//     Native (PostgreSQL only): SELECT * FROM _post WHERE :tagValue = ANY (tags)
//    @Query(value = "SELECT * FROM _post WHERE :tagValue = ANY (tags)", nativeQuery = true)
//    Page<Post> findByTagsIn(@Param("tagValue") String tag, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags tag WHERE LOWER(tag) IN :tags")
    Page<Post> findByTagsIn(@Param("tags") Collection<String> tags, Pageable pageable);
}
