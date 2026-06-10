package com.reportatucalle.modules.user.infrastructure.persistence.mapper;

import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.infrastructure.persistence.entity.UserProfileJpaEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper: Convierte entre UserProfileDomain (puro) y UserProfileJpaEntity (JPA).
 * 
 * Barrera arquitectónica que evita contaminación del dominio.
 */
@Component
public class UserMapper {
    
    /**
     * JPA Entity → Domain POJO puro.
     */
    public UserProfile toDomain(UserProfileJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        
        return UserProfile.builder()
                .id(jpaEntity.getId())
                .accountId(jpaEntity.getAccountId())
                .firstName(jpaEntity.getFirstName())
                .lastName(jpaEntity.getLastName())
                .phone(jpaEntity.getPhone())
                .createdAt(jpaEntity.getCreatedAt())
                .build();
    }
    
    /**
     * Domain POJO → JPA Entity.
     */
    public UserProfileJpaEntity toJpaEntity(UserProfile domain) {
        if (domain == null) return null;
        
        return UserProfileJpaEntity.builder()
                .id(domain.getId())
                .accountId(domain.getAccountId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .phone(domain.getPhone())
                .createdAt(domain.getCreatedAt())
                .build();
    }
    
    /**
     * Domain POJO → JPA Entity (crear nuevo, sin ID).
     */
    public UserProfileJpaEntity toJpaEntityForCreation(UserProfile domain) {
        if (domain == null) return null;
        
        return UserProfileJpaEntity.builder()
                .accountId(domain.getAccountId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .phone(domain.getPhone())
                .build();
    }
}
