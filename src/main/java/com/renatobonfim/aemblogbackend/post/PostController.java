package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    PostService postService;

    @Operation(summary = "Delete a post by its #Id .................... deletePostById(@PathVariable(\"postId\") String postId) ")
    @DeleteMapping("/{postId}")
    public ResponseEntity deletePostById(@PathVariable("postId") String postId) {
        postService.deletePostById(postId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Post>> findAllPosts(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "5") int size,
                                                   @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                   @RequestParam(value = "fields", defaultValue = "title") String properties,
                                                   @RequestParam(value = "category", required = false) Long categoryId
    ) throws CategoryNotFoundException {

        Category category = new Category();
        category.setId(categoryId);

        return ResponseEntity.ok().body(postService.findAllPostByCategory(page, size, sort, properties.split(","), category));
    }

    @Operation(summary = "find a post by its #Id or Permalink .................... findPostByIdOrPermalink(@PathVariable String postPermalinkOrID) ")
    @GetMapping("/{postPermalinkOrID}")
    public ResponseEntity<Post> findPostByIdOrPermalink(@PathVariable String postPermalinkOrID) throws PostNotFoundException {
        return ResponseEntity.ok().body(postService.findPostByIdOrPermalink(postPermalinkOrID));
    }

    @Operation(summary = "Create a Post.................... createPost(@RequestBody Post post) ")
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) throws URISyntaxException, CategoryNotFoundException {
        Post postCreated = postService.createPost(post);
        return ResponseEntity.created(new URI(postCreated.getId().toString())).body(postCreated);
    }

    @Operation(summary = "Update a Post.................... updatePost(@RequestBody Post post, @PathVariable(value = \"postId\") ")
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, @PathVariable(value = "postId") String postId) throws PostNotFoundException, CategoryNotFoundException {
        return ResponseEntity.ok().body(postService.updatePost(post, postId));
    }
}
