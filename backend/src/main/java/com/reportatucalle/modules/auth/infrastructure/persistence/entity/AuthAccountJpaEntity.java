package com.reportatucalle.modules.auth.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.reportatucalle.modules.auth.domain.entity.Role;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Entidad JPA: Persistencia de autenticación.
 * 
 * ✅ SOLO para infraestructura: Contiene @Entity, @Table, @Column, JPA lifecycle
 * ✅ Convertida DESDE AuthAccountDomain mediante AuthMapper
 * ✅ Implementa UserDetails para Spring Security (bridge)
 * ✅ Separada del dominio puro para no contaminarlo
 */
@Entity
@Table(name = "auth_accounts")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthAccountJpaEntity implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column(nullable = false)
    private String password; // Hash encriptado
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // --- Spring Security UserDetails Bridge ---
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    
    @Override
    public String getUsername() {
        return this.email;
    }
    
    @Override
    public String getPassword() {
        return this.password;
    }
    
    @Override
    public boolean isAccountNonExpired() { return true; }
    
    @Override
    public boolean isAccountNonLocked() { return true; }
    
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    
    @Override
    public boolean isEnabled() { return true; }
}
