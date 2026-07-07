package com.reportatucalle.modules.report.infrastructure.controller;

import com.reportatucalle.modules.auth.infrastructure.persistence.entity.AuthAccountJpaEntity;
import com.reportatucalle.modules.report.infrastructure.sse.SseNotificationAdapter;
import com.reportatucalle.modules.user.domain.entity.UserProfile;
import com.reportatucalle.modules.user.domain.repository.UserProfileRepository;
import com.reportatucalle.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/v1/reports/stream")
@RequiredArgsConstructor
public class ReportCitizenSseController {

    private final SseNotificationAdapter sseNotificationAdapter;
    private final UserProfileRepository userProfileRepository;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasAnyRole('CITIZEN', 'SUPERVISOR', 'ADMIN')")
    public SseEmitter streamCitizenReports() {
        AuthAccountJpaEntity currentAccount = (AuthAccountJpaEntity) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        UserProfile citizenProfile = userProfileRepository.findByAccountId(currentAccount.getId())
                .orElseThrow(() -> new BusinessException("Perfil no encontrado", "PROFILE_NOT_FOUND"));

        return sseNotificationAdapter.createCitizenEmitter(citizenProfile.getId());
    }
}
