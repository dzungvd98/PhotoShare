package com.dev.photoshare.service.ViolationReportService;

import com.dev.photoshare.dto.request.ReportViolationRequest;
import com.dev.photoshare.dto.request.ViolationHandleRequest;
import com.dev.photoshare.dto.response.ViolationHandleResponse;
import com.dev.photoshare.dto.response.ViolationReportResponse;
import com.dev.photoshare.entity.Photos;
import com.dev.photoshare.entity.Users;
import com.dev.photoshare.entity.ViolationAction;
import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.exception.ResourceConflictException;
import com.dev.photoshare.exception.ResourceNotFoundException;
import com.dev.photoshare.repository.*;
import com.dev.photoshare.utils.enums.ActionType;
import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.UserStatus;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.dev.photoshare.utils.enums.ModerationAction.BAN_USER;

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

    public ViolationReportResponse sendViolationReport(int userReportId, ReportViolationRequest request) {
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
    public ViolationHandleResponse handleViolationReport(long id, ViolationHandleRequest request) {
        ViolationReport reportFound = violationReportRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("violation report", "id", id));
        if(reportFound.getStatus() != ViolationReportStatus.PENDING) {
            throw new ResourceConflictException("Báo cáo đã được xử lý");
        }
        ViolationAction action = new ViolationAction();
        action.setReport(reportFound);
        action.setActionType(request.getViolationAction());
        action.setNote(request.getViolationMessage());

        applyViolationAction(reportFound, request.getViolationAction());

        violationActionRepository.save(action);

        reportFound.setStatus(ViolationReportStatus.RESOLVED);

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




}
