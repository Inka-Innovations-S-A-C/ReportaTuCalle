package com.reportatucalle.modules.auth.application.api;

import com.reportatucalle.modules.auth.domain.repository.AuthAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación de AuthFacade.
 * 
 * Orquesta llamadas al puerto AuthAccountRepository.
 * Expone SOLO lo que otros módulos necesitan, no detalles internos.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthFacadeImpl implements AuthFacade {
    
    private final AuthAccountRepository authAccountRepository;
    
    @Override
    public Optional<String> getEmailForAccount(Long accountId) {
        return authAccountRepository.findById(accountId)
                .map(account -> account.getEmail());
    }
    
    @Override
    public boolean accountExists(Long accountId) {
        return authAccountRepository.existsById(accountId);
    }
    
    @Override
    public Optional<String> getRoleForAccount(Long accountId) {
        return authAccountRepository.findById(accountId)
                .map(account -> account.getRole().name());
    }
}
