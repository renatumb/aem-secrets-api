package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PostService {

    void deletePostById(String PostId);

    Page<Post> findAllPosts(int page, int size, String sort, String[] properties, Category category, Boolean highlight, String tagFilter);

    Page<Post> findAllPostByCategory(int page, int size, String sort, String[] properties, Category postCategory) throws CategoryNotFoundException;

    Post findPostByIdOrPermalink(String postPermalinkOrID) throws PostNotFoundException;

    Post updatePost(Post post, String postId) throws PostNotFoundException, CategoryNotFoundException;

    Post createPost(Post post) throws CategoryNotFoundException;

    String uploadImage(String postId, MultipartFile file, Boolean isCover);

    byte [] downloadImage( String postId, String fileName) throws IOException;
}
