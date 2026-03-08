package com.dev.photoshare.dto.projection;

import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;

public interface ViolationReportView {

    Long getTargetId();

    TargetType getTargetType();

    ViolationReportStatus getStatus();

    LocalDateTime getCreatedAt();

    LocalDateTime getUpdatedAt();

    @Value("#{target.reporter.id}")
    Integer getReporterId();

    @Value("#{target.reporter.profile.displayName}")
    String getReporterDisplayName();

    @Value("#{target.reportedPerson.id}")
    Integer getReportedPersonId();

    @Value("#{target.reportedPerson.profile.displayName}")
    String getReportedPersonDisplayName();

    Integer getModId();
    String getModDisplayName();
}
