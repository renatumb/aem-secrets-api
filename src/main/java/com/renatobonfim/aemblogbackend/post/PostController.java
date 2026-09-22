package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.customExceptions.CategoryNotFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.UserNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

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
                                                   @RequestParam(value = "orderBy", defaultValue = "title") String orderByFields,
                                                   @RequestParam(value = "categoryFilter", required = false) Long categoryId,
                                                   @RequestParam(value = "highlight", required = false) Boolean highlight,
                                                   @RequestParam(value = "tagFilter", required = false) String tagFilter,
                                                   @RequestParam(value = "statusPost", defaultValue = "PUBLISHED") List<StatusPost> statusPost
    ) throws CategoryNotFoundException {

        Category category = Category.builder().build();
        category.setId(categoryId);

        if (statusPost == null || statusPost.isEmpty()) {
            statusPost = List.of(StatusPost.PUBLISHED);
        }

        Page<Post> allPosts = postService.findAllPosts(page, size, sort, orderByFields.split(","), category, highlight, tagFilter, statusPost);

        return ResponseEntity.ok().body(allPosts);
    }

    @Operation(summary = "find a post by its #Id or Permalink .................... findPostByIdOrPermalink(@PathVariable String postPermalinkOrID) ")
    @GetMapping("/{postPermalinkOrID}")
    public ResponseEntity<Post> findPostByIdOrPermalink(@PathVariable String postPermalinkOrID) throws PostNotFoundException {
        return ResponseEntity.ok().body(postService.findPostByIdOrPermalink(postPermalinkOrID));
    }

    @Operation(summary = "Create a Post.................... createPost(@RequestBody Post post) ")
    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post, Principal subject) throws URISyntaxException, CategoryNotFoundException, UserNotFoundException {

        post.setStatusPost( StatusPost.DRAFT );
        post.setHighlight(false);

        Post postCreated = postService.createPost(post, subject.getName());
        return ResponseEntity.created(new URI(postCreated.getId().toString())).body(postCreated);
    }

    @PostMapping("/image")
    public ResponseEntity uploadImage(@RequestPart("postId") String postId,
                                      @RequestPart("isCover") String isCover,
                                      @RequestPart("selected_file") MultipartFile multipartFile
                                      ) {

        String imageLocation = postService.uploadImage(postId, multipartFile, Boolean.parseBoolean(isCover) );

        ResponseEntity<Map<String, String>> imageUrl = ResponseEntity.ok().body(Map.of("imageUrl", imageLocation));
        return imageUrl;
    }

    @GetMapping(value = "/image", produces = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> downloadImage(@RequestParam("postID") String postID,
                                                @RequestParam("filename") String fileName) throws IOException {
        byte[] bytes = postService.downloadImage(postID, fileName);
        return ResponseEntity.ok(bytes);
    }

    @Operation(summary = "Update a Post.................... updatePost(@RequestBody Post post, @PathVariable(value = \"postId\") ")
    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@RequestBody Post post, @PathVariable(value = "postId") String postId) throws PostNotFoundException, CategoryNotFoundException {
        return ResponseEntity.ok().body(postService.updatePost(post, postId));
    }
}
