package com.renatobonfim.aemblogbackend.comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.renatobonfim.aemblogbackend.post.Post;
import jakarta.persistence.*;
import java.util.Date;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    
    @NotBlank(message = "'content' must not be blank")
    @Size(min = 10, message = "'content' must be at least 10 characters")
    private String content;
    
    @NotBlank(message = "'author' must not be blank")
    @Size(min = 3, max = 20, message = "'author name ' must be 3-20 characters long")
    private String nameAuthor;
    
    @Email(message = "'email' is not valid")
    private String emailAuthor;
    
    private String webSiteAuthor;
    
    private Date creationDate;
    private boolean approved;
    private Date approvalDate;

    @NotNull(message = "Post ID must be informed" )
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

}
