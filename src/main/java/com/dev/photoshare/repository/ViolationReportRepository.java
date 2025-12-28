package com.dev.photoshare.repository;

import com.dev.photoshare.entity.ViolationReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViolationReportRepository extends JpaRepository<ViolationReport,Long> {

}
