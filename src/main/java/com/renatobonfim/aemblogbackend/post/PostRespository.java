package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRespository extends JpaRepository<Post, String> {

    Optional<Post> findPostByIdOrPermalink(String postPermalinkOrID);

    List<Post> findAllPostByCategory(Category category);
}
