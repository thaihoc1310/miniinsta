package com.thaihoc.miniinsta.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thaihoc.miniinsta.model.base.BaseEntity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "users")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    @Id
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String refreshToken;

    private String password;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String name;

    private String picture;

    private String phoneNumber;

    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    private LocalDate dateOfBirth;

    private String provider;

    private String providerId;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private Profile profile;
}
