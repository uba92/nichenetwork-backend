package com.nichenetwork.nichenetwork_backend.user;

import com.nichenetwork.nichenetwork_backend.comment.Comment;
import com.nichenetwork.nichenetwork_backend.communityMember.CommunityMember;
import com.nichenetwork.nichenetwork_backend.follow.Follow;
import com.nichenetwork.nichenetwork_backend.like.Like;
import com.nichenetwork.nichenetwork_backend.post.Post;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username is required")
    @Column(nullable=false, unique = true, length = 50)
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Column(nullable=false, unique = true, length = 50)
    private String email;

    @Size(max = 50, message = "First name must be at most 50 characters long")
    @Column(length = 50)
    private String firstName;

    @Size(max = 50, message = "Last name must be at most 50 characters long")
    @Column(length = 50)
    private String lastName;

    private String avatar;

    @Size(max = 255, message = "Bio must be at most 255 characters long")
    @Column(length = 255)
    private String bio;

    @NotNull(message = "Date of creation is required")
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull(message = "Date of last update is required")
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    @ToString.Exclude
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<CommunityMember> communityMemberships;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Like> likes;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Follow> followingUsers; //Lista di utenti seguiti

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Follow> followers; //Lista di utenti che mi seguono


    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
