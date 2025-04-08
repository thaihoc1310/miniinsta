package com.thaihoc.miniinsta.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thaihoc.miniinsta.model.base.BaseEntity;
import com.thaihoc.miniinsta.model.enums.GenderEnum;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "profiles")
@SQLRestriction("deleted = false")
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
  private int id;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  @JsonIgnore
  private User user;

  // @Column(name = "username", unique = true)
  // private String username;

  private String displayName;

  @Column(columnDefinition = "MEDIUMTEXT")
  private String bio;

  @Column(name = "profile_picture_url", length = 1000)
  private String profilePictureUrl;

  @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Website> websites;

  // @Column(name = "phone_number")
  // private String phoneNumber;

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
  private Set<Profile> followers;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "profile_followers", joinColumns = @JoinColumn(name = "follower_id"), inverseJoinColumns = @JoinColumn(name = "profile_id"))
  @JsonIgnore
  private Set<Profile> following;

  private int followersCount;

  private int followingCount;

  private int postsCount;
}
