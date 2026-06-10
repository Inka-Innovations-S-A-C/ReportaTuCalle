package com.reportatucalle.modules.user.infrastructure.persistence.adapter;

import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.modules.user.infrastructure.persistence.entity.UserProfileJpaEntity;
import com.reportatucalle.modules.user.infrastructure.persistence.mapper.UserMapper;
import com.reportatucalle.modules.user.infrastructure.persistence.repository.UserProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Adaptador de persistencia: Implementa el puerto UserProfileRepository.
 * 
 * Responsabilidades:
 * - Traducir llamadas del puerto a operaciones JPA (via mapper)
 * - Delegar a UserProfileJpaRepository para DB
 * - Retornar entidades de dominio (UserProfileDomain), no JPA
 */
@Component
@RequiredArgsConstructor
public class UserProfilePersistenceAdapter implements UserProfileRepository {
    
    private final UserProfileJpaRepository jpaRepository;
    private final UserMapper mapper;
    
    @Override
    public UserProfile save(UserProfile userProfile) {
        UserProfileJpaEntity jpaEntity = mapper.toJpaEntityForCreation(userProfile);
        UserProfileJpaEntity saved = jpaRepository.save(jpaEntity);
        return mapper.toDomain(saved);
    }
    
    @Override
    public Optional<UserProfile> findById(Long id) {
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }
    
    @Override
    public Optional<UserProfile> findByAccountId(Long accountId) {
        return jpaRepository.findByAccountId(accountId)
                .map(mapper::toDomain);
    }
    
    @Override
    public boolean existsById(Long id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public boolean existsByAccountId(Long accountId) {
        return jpaRepository.existsByAccountId(accountId);
    }
}
