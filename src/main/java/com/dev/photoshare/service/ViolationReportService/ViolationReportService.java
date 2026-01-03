package com.dev.photoshare.service.ViolationReportService;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.request.ViolationHandleRequest;
import com.dev.photoshare.dto.response.ViolationHandleResponse;
import com.dev.photoshare.dto.response.ViolationReportResponse;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.entity.ViolationAction;
import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.*;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViolationReportService implements IViolationReportService {
    private final ViolationReportRepository violationReportRepository;
    private final ViolationActionRepository violationActionRepository;
    private final UserRepository userRepository;

    public ViolationReportResponse sendViolationReport(int userReportId, ReportViolationRequest request) {
        Users user = userRepository.getReferenceById(userReportId);

        ViolationReport report = new ViolationReport();
        report.setReporter(user);
        report.setTargetType(request.getTargetType());
        report.setReason(request.getViolationReason());
        report.setDescription(request.getDescription());
        report.setTargetId(request.getTargetId());
        report.setStatus(ViolationReportStatus.PENDING);

        ViolationReport saved = violationReportRepository.save(report);

        return ViolationReportResponse.builder()
                .targetId(saved.getTargetId())
                .reporterId(userReportId)
                .createdAt(saved.getCreatedAt())
                .status(ViolationReportStatus.PENDING)
                .targetType(saved.getTargetType())
                .build();

    }

    @Transactional
    public ViolationHandleResponse handleViolationReport(long id, ViolationHandleRequest request) {
        ViolationReport reportFound = violationReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("violation report", "id", id));
        ViolationAction action = new ViolationAction();
        action.setReport(reportFound);
        action.setActionType(request.getViolationAction());
        action.setNote(request.getViolationMessage());
        ViolationAction saved  = violationActionRepository.save(action);
        return ViolationHandleResponse.builder()
                .violationAction(request.getViolationAction())
                .violationMessage(request.getViolationMessage())
                .build();

    }

}
