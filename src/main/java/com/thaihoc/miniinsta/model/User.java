package com.thaihoc.miniinsta.model;

import com.thaihoc.miniinsta.model.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLRestriction;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_user_username", columnList = "username"),
        @Index(name = "idx_user_provider_id", columnList = "provider, provider_id")
})
@SQLRestriction("deleted = false")
@EqualsAndHashCode(callSuper = true)
public class User extends BaseEntity {

    @Id
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @OneToMany
    private Set<Authority> authorities;

    private String password;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    private String picture;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    @Column(unique = true, nullable = false)
    private String username;

    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    private boolean enabled = true;
}
