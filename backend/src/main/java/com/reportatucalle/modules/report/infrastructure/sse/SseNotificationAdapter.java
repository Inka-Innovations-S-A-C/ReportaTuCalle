package com.reportatucalle.modules.report.infrastructure.sse;

import com.reportatucalle.modules.report.application.dto.ReportResponse;
import com.reportatucalle.modules.report.application.mapper.ReportMapper;
import com.reportatucalle.modules.report.domain.entity.Report;
import com.reportatucalle.modules.report.domain.entity.ReportEndorsement;
import com.reportatucalle.modules.report.domain.portsout.ReportNotificationPort;
import com.reportatucalle.modules.report.domain.repository.ReportEndorsementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class SseNotificationAdapter implements ReportNotificationPort {

    private final List<SseEmitter> adminEmitters = new CopyOnWriteArrayList<>();
    private final Map<Long, List<SseEmitter>> citizenEmitters = new ConcurrentHashMap<>();
    
    private final ReportMapper mapper;
    private final ReportEndorsementRepository endorsementRepository;

    public SseEmitter createAdminEmitter() {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1 hour timeout
        adminEmitters.add(emitter);
        
        emitter.onCompletion(() -> adminEmitters.remove(emitter));
        emitter.onTimeout(() -> adminEmitters.remove(emitter));
        emitter.onError((e) -> adminEmitters.remove(emitter));
        
        return emitter;
    }

    public SseEmitter createCitizenEmitter(Long citizenId) {
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 1 hour timeout
        
        citizenEmitters.computeIfAbsent(citizenId, k -> new CopyOnWriteArrayList<>()).add(emitter);
        
        Runnable removeCallback = () -> {
            List<SseEmitter> list = citizenEmitters.get(citizenId);
            if (list != null) {
                list.remove(emitter);
                if (list.isEmpty()) {
                    citizenEmitters.remove(citizenId);
                }
            }
        };
        
        emitter.onCompletion(removeCallback);
        emitter.onTimeout(removeCallback);
        emitter.onError((e) -> removeCallback.run());
        
        return emitter;
    }

    @Override
    public void notifyReportCreated(Report report) {
        ReportResponse response = mapper.toResponse(report);
        broadcastToAdmins("REPORT_CREATED", response);
        broadcastToAllCitizens("REPORT_CREATED", response);
    }

    @Override
    public void notifyReportStatusUpdated(Report report) {
        ReportResponse response = mapper.toResponse(report);
        broadcastToAdmins("REPORT_UPDATED", response);
        broadcastToCitizen(report.getCitizenId(), "REPORT_UPDATED", response);
        
        List<ReportEndorsement> endorsers = endorsementRepository.findByReportId(report.getId());
        for (ReportEndorsement endorsement : endorsers) {
            broadcastToCitizen(endorsement.getCitizenId(), "REPORT_UPDATED", response);
        }
    }

    @Override
    public void notifyReportAssigned(Report report) {
        ReportResponse response = mapper.toResponse(report);
        broadcastToAdmins("REPORT_ASSIGNED", response);
        broadcastToCitizen(report.getCitizenId(), "REPORT_ASSIGNED", response);
        
        List<ReportEndorsement> endorsers = endorsementRepository.findByReportId(report.getId());
        for (ReportEndorsement endorsement : endorsers) {
            broadcastToCitizen(endorsement.getCitizenId(), "REPORT_ASSIGNED", response);
        }
    }

    private void broadcastToAdmins(String eventName, ReportResponse payload) {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        adminEmitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(payload));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        adminEmitters.removeAll(deadEmitters);
    }
    
    private void broadcastToCitizen(Long citizenId, String eventName, ReportResponse payload) {
        List<SseEmitter> list = citizenEmitters.get(citizenId);
        if (list == null || list.isEmpty()) return;
        
        List<SseEmitter> deadEmitters = new ArrayList<>();
        list.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(payload));
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }
        });
        list.removeAll(deadEmitters);
    }

    private void broadcastToAllCitizens(String eventName, ReportResponse payload) {
        List<Long> deadCitizenIds = new ArrayList<>();
        citizenEmitters.forEach((citizenId, list) -> {
            List<SseEmitter> deadEmitters = new ArrayList<>();
            list.forEach(emitter -> {
                try {
                    emitter.send(SseEmitter.event()
                            .name(eventName)
                            .data(payload));
                } catch (IOException e) {
                    deadEmitters.add(emitter);
                }
            });
            list.removeAll(deadEmitters);
            if (list.isEmpty()) {
                deadCitizenIds.add(citizenId);
            }
        });
        deadCitizenIds.forEach(citizenEmitters::remove);
    }
}
