package com.thaihoc.miniinsta.model;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "comment")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "profile_id", nullable = false)
  @JsonProperty("createdBy")
  private Profile createdBy;

  @NotNull
  @Column(name = "comment", length = 1000, nullable = false)
  private String comment;

  @ManyToOne
  @JoinColumn(name = "post_id", nullable = false)
  @JsonIgnore
  private Post post;

  @ManyToOne
  @JoinColumn(name = "parent_comment_id")
  @JsonIgnore
  private Comment parentComment;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "comment_likes", joinColumns = @JoinColumn(name = "comment_id"), inverseJoinColumns = @JoinColumn(name = "profile_id"))
  private Set<Profile> likes;

  @Column(name = "like_count")
  private int likeCount;

}
