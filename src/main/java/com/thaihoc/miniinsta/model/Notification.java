package com.thaihoc.miniinsta.model;

import com.thaihoc.miniinsta.model.base.BaseEntity;
import com.thaihoc.miniinsta.model.enums.EntityType;
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
@Table(name = "notifications")
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
  private long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Profile recipient;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Profile actor;

  @Column(nullable = false)
  private String content;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private NotificationType type;

  private long entityId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private EntityType entityType;

  private boolean isRead;
}
