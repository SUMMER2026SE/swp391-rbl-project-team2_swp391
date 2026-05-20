package com.clinicmanagement.staff;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StaffRepository extends JpaRepository<Staff, Long> {

    @Query("SELECT s FROM Staff s JOIN s.user u WHERE " +
            "(:staffType IS NULL OR s.staffType = :staffType) AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(LOWER(u.fullName) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')) " +
            "OR LOWER(s.staffCode) LIKE LOWER(CONCAT('%', CAST(:keyword AS string), '%')))")
    Page<Staff> searchStaff(@Param("staffType") String staffType,
                            @Param("status") String status,
                            @Param("keyword") String keyword,
                            Pageable pageable);

    boolean existsByStaffCode(String staffCode);

    boolean existsByUser_UserId(Long userId);
}
