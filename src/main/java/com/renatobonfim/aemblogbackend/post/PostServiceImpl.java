package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.category.CategoryRepository;
import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.InvalidFieldException;
import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    PostRespository postRespository;

    @Autowired
    CategoryRepository categoryRepository;

    @Override
    public Post createPost(Post post) throws CategoryNotFoundException {
        post.setCreationDate(new Date());
        post.setLastModificationDate(new Date());
        
        HashSet foundCategories = new HashSet();
        
        post.getCategories().forEach((category) -> {
            Category foundCategory = categoryRepository.findById(category.getId()).<CategoryNotFoundException>orElseThrow(() -> new CategoryNotFoundException(category.getId()));
            foundCategories.add(foundCategory);
        });

        post.setCategories(foundCategories);
        return postRespository.save(post);
    }

    @Override
    public void deletePostById(String postId) {
        postRespository.deleteById(postId);
    }

    @Override
    public Page<Post> findAllPosts(int page, int size, String sort, String[] properties) {
        Page<Post> posts = postRespository.findAllWithCategories(PageRequest.of(page, size, Sort.Direction.fromString(sort), properties));
        return posts;
    }


    @Override
    public Post findPostByIdOrPermalink(String postPermalinkOrID) throws PostNotFoundException {
        return postRespository.findPostByIdOrPermalink(postPermalinkOrID).orElseThrow(() -> new PostNotFoundException(String.format(Constants.POST_NOT_FOUND, postPermalinkOrID)));
    }

    @Override
    public Page<Post> findAllPostByCategory(int page, int size, String sort, String[] properties, Category postCategory) throws CategoryNotFoundException {
        Long categoryId = postCategory.getId();

        if (Objects.isNull(categoryId)) {
            return findAllPosts(page, size, sort, properties);
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sort), properties);
        List<Post> allPostByCategory = postRespository.findAllPostByCategory(category);

        long start = pageRequest.getOffset();
        long end = Math.min((start + pageRequest.getPageSize()), allPostByCategory.size());

        return new PageImpl<Post>(allPostByCategory.subList((int) start, (int) end), pageRequest, allPostByCategory.size());
    }

    @Override
    public Post updatePost(Post post, String postId) throws PostNotFoundException, CategoryNotFoundException {
        Post oldPost = postRespository.findById(postId).orElseThrow(() -> new PostNotFoundException(String.format(Constants.POST_NOT_FOUND_ID, postId)));

        String permalink = Objects.nonNull(post.getPermalink()) && !post.getPermalink().isEmpty() && !post.getPermalink().isBlank() ? post.getPermalink() : oldPost.getPermalink();
        String title = Objects.nonNull(post.getTitle()) && !post.getTitle().isEmpty() && !post.getTitle().isBlank() ? post.getTitle() : oldPost.getTitle();
        String description = Objects.nonNull(post.getDescription()) && !post.getDescription().isEmpty() && !post.getDescription().isBlank() ? post.getDescription() : oldPost.getDescription();
        String thumbnail = Objects.nonNull(post.getThumbnail()) && !post.getThumbnail().isEmpty() && !post.getThumbnail().isBlank() ? post.getThumbnail() : oldPost.getThumbnail();
        String content_en = Objects.nonNull(post.getContent_en()) && !post.getContent_en().isEmpty() && !post.getContent_en().isBlank() ? post.getContent_en() : oldPost.getContent_en();
        boolean highlight = post.isHighlight();
        List<String> tags = Objects.nonNull(post.getTags()) && !post.getTags().isEmpty() ? post.getTags() : oldPost.getTags();
        Category category = Objects.nonNull(post.getCategory()) ? post.getCategory() : oldPost.getCategory();

        oldPost.setPermalink(permalink);
        oldPost.setTitle(title);
        oldPost.setDescription(description);
        oldPost.setThumbnail(thumbnail);
        oldPost.setContent_en(content_en);
        oldPost.setCategory(category);
        oldPost.setHighlight(highlight);
        oldPost.setTags(tags);
        oldPost.setLastModificationDate(new Date());

        categoryRepository.findById(category.getId()).orElseThrow(() -> new CategoryNotFoundException(category.getId()));
        return postRespository.save(oldPost);
    }

    private void validatePost(Post post, boolean update) {

        try {
            Objects.requireNonNull(post);

            if (update) {

            }
        } catch (NullPointerException ex) {
            throw new InvalidFieldException(ex.getMessage());
        }

    }
}
