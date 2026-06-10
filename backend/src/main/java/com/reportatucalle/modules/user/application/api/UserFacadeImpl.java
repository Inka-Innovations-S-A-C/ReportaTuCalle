package com.reportatucalle.modules.user.application.api;

import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación de UserFacade.
 * 
 * Orquesta llamadas al puerto UserProfileRepository.
 * Expone SOLO lo que otros módulos necesitan, no detalles internos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserFacadeImpl implements UserFacade {
    
    private final UserProfileRepository userProfileRepository;
    
    @Override
    public Optional<String> getFullNameForUser(Long userId) {
        return userProfileRepository.findById(userId)
                .map(user -> user.getFullName());
    }
    
    
    @Override
    public boolean userExists(Long userId) {
        return userProfileRepository.existsById(userId);
    }
    
    @Override
    public boolean userExistsByAccountId(Long accountId) {
        return userProfileRepository.existsByAccountId(accountId);
    }
    
    @Override
    public Optional<Long> getUserIdByAccountId(Long accountId) {
        return userProfileRepository.findByAccountId(accountId)
                .map(user -> user.getId());
    }
}
