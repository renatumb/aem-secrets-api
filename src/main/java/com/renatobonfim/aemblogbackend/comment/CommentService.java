package com.renatobonfim.aemblogbackend.comment;

import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import com.renatobonfim.aemblogbackend.post.Post;
import org.springframework.data.domain.Page;

public interface CommentService {
    void deleteById(Integer commentId);

    Comment createComment(Comment comment) throws PostNotFoundException;

    Page<Comment> findAllComments(int page, int size, String sort, String[] properties);

    Page<Comment> findAllCommentsByPost(int page, int size, String sort, String[] properties, String postId, String statusFilter);

    Comment findById(Integer commentId);

    Comment updateComment(Integer commentId, Comment comment);
}
