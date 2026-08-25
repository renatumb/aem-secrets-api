package com.renatobonfim.aemblogbackend.userx;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UuidGenerator;

@NamedQuery( name= "User.findPhoto", query="select us.photoProfile from User us where us.id=:userID" )

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@DynamicUpdate
@Table(name = "_user")
public class User {

    private static final long serialVersionUID = 1L;

    @Id
    @UuidGenerator(style = UuidGenerator.Style.AUTO)
    @Column(name = "id", unique = true, updatable = false)
    private String id;

    private String name;

    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String about;

    private String photoProfile;

    @Enumerated (EnumType.STRING)
    private AccessLevel accessLevel;

    private Boolean accountLocked;
}
