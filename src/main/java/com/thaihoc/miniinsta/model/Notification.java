package com.thaihoc.miniinsta.model;

import com.thaihoc.miniinsta.model.base.BaseEntity;
import com.thaihoc.miniinsta.model.enums.NotificationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "notification")
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification extends BaseEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private int id;

  @ManyToOne
  @JoinColumn(name = "recipient_id", nullable = false)
  private Profile recipient;

  @ManyToOne
  @JoinColumn(name = "sender_id", nullable = false)
  private Profile sender;

  @Column(name = "content", nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false)
  private NotificationType type;

  @Column(name = "related_post_id")
  private Integer relatedPostId;

  @Column(name = "related_comment_id")
  private Integer relatedCommentId;

  @Column(name = "is_read")
  private boolean isRead;
}
