package com.thaihoc.miniinsta.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thaihoc.miniinsta.model.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "profile")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Profile extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  @JsonIgnore
  private User user;

  @Column(name = "username", unique = true)
  private String username;

  @Column(name = "display_name")
  private String displayName;

  @Column(name = "bio", length = 150)
  private String bio;

  @Column(name = "profile_picture_url")
  private String profilePictureUrl;

  @Column(name = "website")
  private String website;

  @Column(name = "phone_number")
  private String phoneNumber;

  @Column(name = "is_private")
  private boolean isPrivate;

  @Column(name = "is_verified")
  private boolean isVerified;

  @JsonIgnore
  @OneToMany(mappedBy = "createdBy")
  private List<Post> posts;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "profile_followers", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "follower_id"))
  @JsonIgnore
  private Set<Profile> followers;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "profile_followers", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "profile_id"))
  @JsonIgnore
  private Set<Profile> following;

  @Column(name = "followers_count")
  private int followersCount;

  @Column(name = "following_count")
  private int followingCount;

  @Column(name = "posts_count")
  private int postsCount;
}
