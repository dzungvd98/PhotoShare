package com.dev.photoshare.repository;

import com.dev.photoshare.entity.Users;
import com.dev.photoshare.entity.ViolationReport;
import com.dev.photoshare.utils.enums.ActionType;
import com.dev.photoshare.utils.enums.TargetType;
import com.dev.photoshare.utils.enums.ViolationReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ViolationReportRepository extends JpaRepository<ViolationReport,Long>, JpaSpecificationExecutor<ViolationReport> {
    boolean existsByReporterIdAndTargetTypeAndTargetId(
            Integer reporterId,
            TargetType targetType,
            Long targetId
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE ViolationReport v
    SET v.actionType = :actionType,
        v.status = :status,
        v.mod.id = :modId
    WHERE v.targetId = :targetId
""")
    int updateAllByTargetId(
            @Param("targetId") Long targetId,
            @Param("actionType") ActionType actionType,
            @Param("status")ViolationReportStatus   status,
            @Param("modId")  int modId
            );

}
