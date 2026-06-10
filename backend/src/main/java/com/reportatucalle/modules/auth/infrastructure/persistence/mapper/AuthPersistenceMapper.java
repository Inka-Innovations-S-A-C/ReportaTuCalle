package com.reportatucalle.modules.auth.infrastructure.persistence.mapper;
import com.reportatucalle.modules.auth.domain.entity.AuthAccount;
import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper: Convierte entre AuthAccountDomain (puro) y AuthAccountJpaEntity (JPA).
 * 
 * Barrera arquitectónica que evita contaminación del dominio.
 * Responsable de traducir entidades a través de las capas.
 */
@Component
public class AuthPersistenceMapper {
    
    /**
     * JPA Entity → Domain POJO puro.
     */
    public AuthAccount toDomain(AuthAccountJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        
        return AuthAccount.builder()
                .id(jpaEntity.getId())
                .email(jpaEntity.getEmail())
                .passwordHash(jpaEntity.getPassword())
                .role(jpaEntity.getRole())
                .createdAt(jpaEntity.getCreatedAt())
                .build();
    }
    
    /**
     * Domain POJO → JPA Entity.
     */
    public AuthAccountJpaEntity toJpaEntity(AuthAccount domain) {
        if (domain == null) return null;
        
        return AuthAccountJpaEntity.builder()
                .id(domain.getId())
                .email(domain.getEmail())
                .password(domain.getPasswordHash())
                .role(domain.getRole())
                .createdAt(domain.getCreatedAt())
                .build();
    }
    
    /**
     * Domain POJO → JPA Entity (crear nuevo, sin ID).
     */
    public AuthAccountJpaEntity toJpaEntityForCreation(AuthAccount domain) {
        if (domain == null) return null;
        
        return AuthAccountJpaEntity.builder()
                .email(domain.getEmail())
                .password(domain.getPasswordHash())
                .role(domain.getRole())
                .build();
    }
}
