package com.thaihoc.miniinsta.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.security.core.GrantedAuthority;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class Authority implements GrantedAuthority {

    @Id
    @JdbcTypeCode(java.sql.Types.VARCHAR)
    private UUID id;

    private String authority;
}
