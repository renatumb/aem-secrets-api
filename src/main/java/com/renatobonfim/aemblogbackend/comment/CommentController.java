package com.renatobonfim.aemblogbackend.comment;

import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@CrossOrigin()
public class CommentController {

    @Autowired
    CommentService commentService;

    @DeleteMapping("/{commentId}")
    public ResponseEntity deleteById(@PathVariable() Integer commentId) {
        commentService.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<Comment>> findAllByPostComments(@RequestParam(value = "page", defaultValue = "0") int page,
                                                         @RequestParam(value = "size", defaultValue = "5") int size,
                                                         @RequestParam(value = "sort", defaultValue = "asc") String sort,
                                                         @RequestParam(value = "orderBy", defaultValue = "creationDate") String properties,
                                                         @RequestParam(value = "postId", required = false  ) String postId,
                                                         @RequestParam(value = "statusFilter", required = false) String statusFilter
    ) {
        return ResponseEntity.ok(commentService.findAllCommentsByPost(page, size, sort, properties.split(","), postId, statusFilter));
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<Comment> findById(@PathVariable("commentId") Integer commentId) {
        return ResponseEntity.ok(commentService.findById(commentId));
    }

    @PostMapping
    public ResponseEntity<Comment> createComment(@Valid @RequestBody Comment comment) throws URISyntaxException, PostNotFoundException {
        Comment createdComment = commentService.createComment(comment);
        return ResponseEntity.created(new URI(createdComment.getId().toString())).body(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable("commentId") Integer commentId,
                                                 @RequestBody Comment comment) {
        return ResponseEntity.ok(commentService.updateComment(commentId, comment));
    }
}
