package com.renatobonfim.aemblogbackend.comment;

import com.renatobonfim.aemblogbackend.config.Constants;
import com.renatobonfim.aemblogbackend.customExceptions.InvalidFieldException;
import com.renatobonfim.aemblogbackend.customExceptions.NoCommentFoundException;
import com.renatobonfim.aemblogbackend.customExceptions.PostNotFoundException;
import com.renatobonfim.aemblogbackend.post.Post;
import com.renatobonfim.aemblogbackend.post.PostService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    PostService postService;

    @Override
    public void deleteById(Integer commentId) {
        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment createComment(Comment comment) throws PostNotFoundException {

        comment.setApproved(false);
        comment.setCreationDate(new Date());
        comment.setPost(postService.findPostByIdOrPermalink(comment.getPost().getId()));

        return commentRepository.save(comment);
    }

    @Override
    public Page<Comment> findAllComments(int page, int size, String sort, String[] properties) {
        return commentRepository.findAll(PageRequest.of(page, size, Sort.Direction.fromString(sort), properties));
    }

    @Override
    public Page<Comment> findAllCommentsByPost(int page, int size, String sort, String[] properties, String postId) {
        if (Objects.isNull(postId)) {
            return findAllComments(page, size, sort, properties);
        }

        Post postFound;
        try {
            postFound = postService.findPostByIdOrPermalink(postId);
        } catch (PostNotFoundException ex) {
            throw new InvalidFieldException(ex.getMessage());
        }
        List<Comment> allCommentsByPost = commentRepository.findAllCommentsByPost(postFound);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.Direction.fromString(sort), properties);
        long start = pageRequest.getOffset();
        long end = Math.min((start + pageRequest.getPageSize()), allCommentsByPost.size());

        return new PageImpl<Comment>(allCommentsByPost.subList((int) start, (int) end), pageRequest, allCommentsByPost.size());
    }

    @Override
    public Comment findById(Integer commentId) {
        return commentRepository.findById(commentId).orElseThrow(() -> new NoCommentFoundException(String.format(Constants.COMMENT_NOT_FOUND, commentId)));
    }

    @Override
    public Comment updateComment(Integer commentId, Comment comment) {
        Comment foundComment = findById(commentId);
        foundComment.setApproved(comment.isApproved());
        foundComment.setApprovalDate(new Date());
        return commentRepository.save(foundComment);
    }
}
