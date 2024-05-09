package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import org.springframework.data.domain.Page;

public interface PostService {

    void deletePostById(String PostId);

    Page<Post> findAllPosts(int page, int size, String sort, String[] properties);

    Page<Post> findAllPostByCategory(int page, int size, String sort, String[] properties, Category postCategory) throws CategoryNotFoundException;

    Post findPostByIdOrPermalink(String postPermalinkOrID) throws PostNotFoundException;

    Post updatePost(Post post, String postId) throws PostNotFoundException, CategoryNotFoundException;

    Post createPost(Post post) throws CategoryNotFoundException;
}
