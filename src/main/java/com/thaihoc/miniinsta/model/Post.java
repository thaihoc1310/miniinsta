package com.thaihoc.miniinsta.model;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thaihoc.miniinsta.model.base.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "post")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profile_id", nullable = false)
  @JsonProperty("createdBy")
  private Profile createdBy;

  @Column(name = "image_url")
  private String imageUrl;

  @Column(name = "caption", length = 2200)
  private String caption;

  @Column(name = "location")
  private String location;

  @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonProperty("comments")
  private List<Comment> comments;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "post_likes", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "profile_id"))
  private Set<Profile> userLikes;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "post_hashtags", joinColumns = @JoinColumn(name = "post_id"), inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
  private Set<Hashtag> hashtags;

  @Column(name = "like_count")
  private int likeCount;

  @Column(name = "comment_count")
  private int commentCount;
}
