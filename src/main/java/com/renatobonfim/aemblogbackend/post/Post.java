package com.renatobonfim.aemblogbackend.post;

import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.userx.User;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UuidGenerator;

@NamedQuery(name = "Post.findPostByIdOrPermalink", query = "SELECT ps FROM Post ps WHERE ps.id=:postPermalinkOrID OR ps.permalink=:postPermalinkOrID")
@NamedQuery(name = "Post.findAllPostByCategory", query = "SELECT ps FROM Post ps WHERE ps.category=:category")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "_post")
public class Post {

    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    private String id;

    private String permalink;
    private String title;
    private String description;
    private String thumbnail;

    @Column(columnDefinition = "text")
    private String content_en;

    private Date creationDate;
    private Date lastModificationDate;

    private boolean highlight;

    private List<String> tags;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
}
