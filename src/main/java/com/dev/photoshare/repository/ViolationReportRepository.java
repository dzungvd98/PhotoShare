package com.dev.photoshare.repository;

import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.utils.enums.TargetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationReportRepository extends JpaRepository<ViolationReport,Long>, JpaSpecificationExecutor<ViolationReport> {
    boolean existsByReporterIdAndTargetTypeAndTargetId(
            Integer reporterId,
            TargetType targetType,
            Long targetId
    );

}
