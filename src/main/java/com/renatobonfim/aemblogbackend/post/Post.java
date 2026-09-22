package com.renatobonfim.aemblogbackend.post;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.renatobonfim.aemblogbackend.category.Category;
import com.renatobonfim.aemblogbackend.comment.Comment;
import com.renatobonfim.aemblogbackend.userx.User;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UuidGenerator;

@NamedQuery(name = "Post.findPostByIdOrPermalink", query = "SELECT ps FROM Post ps WHERE   ps.permalink=:postPermalinkOrID")
//@NamedQuery(name = "Post.findAllPostByCategory", query = "SELECT ps FROM Post ps WHERE ps.category=:category")

@Data
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "_post")
public class Post {

    private static final long serialVersionUID = 2L;

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

    private Boolean highlight;

    // Stores multiple tags per post in a separate join table (portable across PostgreSQL, MySQL, etc.).
    @ElementCollection(fetch = FetchType.LAZY) // Load tags only when accessed; enables JPQL JOIN p.tags for filtering.
    @CollectionTable(name = "_post_tags", joinColumns = @JoinColumn(name = "post_id")) // One row per tag, linked back to _post.
    @Column(name = "tag") // Column name for each tag value inside _post_tags.
    private List<String> tags = new ArrayList<>(); // Default empty list avoids null checks when no tags are set.
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "_post_category",
            joinColumns = @JoinColumn(name="fk_post", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name="fk_category", referencedColumnName = "id", nullable = false)
    )
    private Set<Category> categories;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"id", "email", "password", "accessLevel", "accountLocked"})
    private User author;
    
    @JsonManagedReference
    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comment;


    @Enumerated (EnumType.STRING)
    private StatusPost statusPost;
    /* */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

