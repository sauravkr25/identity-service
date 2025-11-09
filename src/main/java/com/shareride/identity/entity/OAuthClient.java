package com.shareride.identity.entity;

import com.shareride.identity.enums.OAuthClientStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Builder
@Table(name = "oauth_clients")
@NoArgsConstructor
@AllArgsConstructor
public class OAuthClient {

    @Id
    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret_hash", nullable = false)
    private String clientSecretHash;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OAuthClientStatus status = OAuthClientStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * Defines the many-to-many relationship between clients and roles.
     * A client can have multiple roles, and a role can be assigned to multiple clients.
     */
    @ManyToMany(fetch = FetchType.EAGER) // EAGER fetch is okay here as we always need the roles for token creation.
    @JoinTable(
            name = "oauth_client_roles", // The name of the join table in the database.
            joinColumns = @JoinColumn(name = "client_id"), // The foreign key column in the join table for this entity.
            inverseJoinColumns = @JoinColumn(name = "role_id") // The foreign key column for the other entity (Role).
    )
    private Set<Role> roles = new HashSet<>();
}
