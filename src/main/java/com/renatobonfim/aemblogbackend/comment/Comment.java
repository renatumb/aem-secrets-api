package com.renatobonfim.aemblogbackend.comment;

import com.renatobonfim.aemblogbackend.post.Post;
import jakarta.persistence.*;
import java.util.Date;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@NamedQuery(name = "Comment.findAllCommentsByPost", query = "SELECT cm FROM Comment cm WHERE cm.post =: post")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "_comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String content;
    private String nameAuthor;
    private String emailAuthor;
    private String webSiteAuthor;
    private Date creationDate;
    private boolean approved;
    private Date approvalDate;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

}
