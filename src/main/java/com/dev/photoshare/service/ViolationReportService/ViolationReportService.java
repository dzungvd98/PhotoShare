package com.dev.photoshare.service.ViolationReportService;

import com.dev.photoshare.dto.projection.ViolationReportView;
import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.request.ViolationHandleRequest;
import com.dev.photoshare.dto.request.ViolationSearchRequest;
import com.dev.photoshare.dto.response.PageResponse;
import com.dev.photoshare.dto.response.ViolationHandleResponse;
import com.dev.photoshare.dto.response.ViolationReportResponse;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.entity.ViolationAction;
import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.exception.BusinessException;
import com.dev.photoshare.exception.ResourceConflictException;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.*;
import com.dev.photoshare.repository.specification.ViolationReportSpecification;
import com.dev.photoshare.utils.enums.ActionType;
import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ViolationReportService implements IViolationReportService {
    private final ViolationReportRepository violationReportRepository;
    private final ViolationActionRepository violationActionRepository;
    private final UserRepository userRepository;
    private final PhotoRepository photoRepository;
    private final CommentRepository commentRepository;

    private final static int NUM_DAY_REPORT = 3;

    @Override
    public PageResponse<ViolationReportView> getViolationReports(Pageable pageable, ViolationSearchRequest req) {
        Specification<ViolationReport> specification =
                ViolationReportSpecification.filterViolationReport(req);

        Page<ViolationReport> page =
                violationReportRepository.findAll(specification, pageable);

        Page<ViolationReportView> result =
                page.map(this::mapToView);

        return PageResponse.from(result);
    }

    public ViolationReportResponse sendViolationReport(int userReportId, ReportViolationRequest request) {

        if (violationReportRepository.existsByReporterIdAndTargetTypeAndTargetId(
                userReportId, request.getTargetType(), request.getTargetId())) {
            throw new BusinessException("Bạn chỉ có thể gửi báo cáo 1 lần cho 1 bài viết");
        }
        Users user = userRepository.getReferenceById(userReportId);

        ViolationReport report = new ViolationReport();
        report.setReporter(user);
        report.setTargetType(request.getTargetType());
        report.setReason(request.getViolationReason());
        report.setDescription(request.getDescription());
        report.setTargetId(request.getTargetId());
        report.setStatus(ViolationReportStatus.PENDING);

        Users reportedPerson = switch (request.getTargetType()) {
            case COMMENT -> commentRepository.findAuthorByCommentId(request.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", request.getTargetId()));
            case PHOTO -> photoRepository.findAuthorByPhotoId(request.getTargetId())
                    .orElseThrow(() -> new ResourceNotFoundException("Photo", "id", request.getTargetId()));
        };

        report.setReportedPerson(reportedPerson);

        ViolationReport saved = violationReportRepository.save(report);

        return ViolationReportResponse.builder()
                .targetId(saved.getTargetId())
                .reporterId(userReportId)
                .reportedPersonId(reportedPerson.getId())
                .createdAt(saved.getCreatedAt())
                .status(ViolationReportStatus.PENDING)
                .targetType(saved.getTargetType())
                .build();

    }

    @Transactional
    public ViolationHandleResponse handleViolationReport(long id, ViolationHandleRequest request, int userId) {
        ViolationReport reportFound = violationReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("violation report", "id", id));
        if(reportFound.getStatus() != ViolationReportStatus.PENDING) {
            throw new ResourceConflictException("Báo cáo đã được xử lý");
        }

        reportFound.setActionType(request.getViolationAction());
        reportFound.setNote(request.getViolationMessage());
        applyViolationAction(reportFound, request.getViolationAction());
        reportFound.setStatus(ViolationReportStatus.RESOLVED);
        reportFound.setMod(new Users(userId));
        violationReportRepository.updateAllByTargetId(reportFound.getTargetId(), request.getViolationAction(),  ViolationReportStatus.RESOLVED, userId);
        violationReportRepository.save(reportFound);


        return ViolationHandleResponse.builder()
                .violationAction(request.getViolationAction())
                .violationMessage(request.getViolationMessage())
                .build();

    }

    private void lockedUser(Users user) {
        user.lockedUser(NUM_DAY_REPORT);
    }

    private void bannedUser(Users user) {
        user.bannedUser();
    }

    private void deleteViolation(ViolationReport report) {
        switch (report.getTargetType()) {
            case PHOTO -> photoRepository.softDeletePhotoById(report.getTargetId());
            case COMMENT -> commentRepository.softDeleteRootAndReplies(report.getTargetId());
            default -> throw new IllegalStateException(
                    "Unsupported target type: " + report.getTargetType()
            );
        }
    }

    private void applyViolationAction(ViolationReport report,
                                      ActionType actionType) {

        Users reportedUser = report.getReportedPerson();

        switch (actionType) {

            case CONTENT_REMOVED -> deleteViolation(report);

            case USER_SUSPENDED -> lockedUser(reportedUser);

            case USER_BANNED -> bannedUser(reportedUser);

            case NO_VIOLATION -> {
                // không làm gì, chỉ đánh dấu report đã xử lý
            }

            default -> throw new IllegalStateException(
                    "Unsupported violation action: " + actionType
            );
        }
    }

    private ViolationReportView mapToView(ViolationReport r) {

        return new ViolationReportView() {

            @Override
            public Long getReportId() {
                return r.getId() != null ? r.getId() : null;
            }

            @Override
            public Integer getReporterId() {
                return r.getReporter() != null ? r.getReporter().getId() : null;
            }

            @Override
            public String getReporterDisplayName() {
                return r.getReporter() != null && r.getReporter().getProfile() != null
                        ? r.getReporter().getProfile().getDisplayName()
                        : null;
            }

            @Override
            public Integer getReportedPersonId() {
                return r.getReportedPerson() != null ? r.getReportedPerson().getId() : null;
            }

            @Override
            public String getReportedPersonDisplayName() {
                return r.getReportedPerson() != null && r.getReportedPerson().getProfile() != null
                        ? r.getReportedPerson().getProfile().getDisplayName()
                        : null;
            }

            @Override
            public Integer getModId() {
                return r.getMod() != null ? r.getMod().getId() : null;
            }

            @Override
            public String getModDisplayName() {
                return r.getMod() != null && r.getMod().getProfile() != null
                        ? r.getMod().getProfile().getDisplayName()
                        : null;
            }

            @Override
            public ViolationReportStatus getStatus() {
                return r.getStatus();
            }

            @Override
            public Long getTargetId() {
                return r.getTargetId();
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return r.getCreatedAt();
            }

            @Override
            public LocalDateTime getUpdatedAt() {
                return r.getUpdatedAt();
            }

            @Override
            public TargetType getTargetType() {
                return r.getTargetType();
            }
        };
    }
}
