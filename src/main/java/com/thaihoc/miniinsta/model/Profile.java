package com.thaihoc.miniinsta.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thaihoc.miniinsta.model.base.BaseEntity;
import com.thaihoc.miniinsta.model.enums.GenderEnum;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "profiles")
// @SQLRestriction("deleted = false")
@Getter
@Setter
// @ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  @JsonIgnore
  private User user;

  @NotBlank(message = "Username is required")
  @Column(unique = true, nullable = false)
  private String username;

  @NotBlank(message = "Display name is required")
  @Column(nullable = false)
  private String displayName;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String bio;

  @Column(name = "profile_picture_url", length = 1000)
  private String profilePictureUrl;

  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Website> websites;

  private boolean isPrivate;

  @Enumerated(EnumType.STRING)
  private GenderEnum gender;

  // private boolean isVerified;

  @JsonIgnore
  @OneToMany(mappedBy = "createdBy")
  private List<Post> posts;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "profile_followers", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
  @JsonIgnore
  private List<Profile> followers;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "profile_followers", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "profile_id"))
  @JsonIgnore
  private List<Profile> following;

  private int followersCount;

  private int followingCount;

  private int postsCount;
}
