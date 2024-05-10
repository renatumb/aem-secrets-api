package com.renatobonfim.aemblogbackend.comment;

import com.renatobonfim.aemblogbackend.post.Post;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findAllCommentsByPost(Post post);
}
