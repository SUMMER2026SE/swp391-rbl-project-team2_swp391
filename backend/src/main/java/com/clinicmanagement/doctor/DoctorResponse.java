package com.clinicmanagement.doctor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DoctorResponse(
        Long doctorId,
        String doctorCode,
        String fullName,
        Long departmentId,
        String departmentName,
        String degree,
        String specialization,
        Integer yearsOfExperience,
        BigDecimal consultationFee,
        String status,
        LocalDateTime createdAt
) {
    public static DoctorResponse from(Doctor doctor) {
        return new DoctorResponse(
                doctor.getDoctorId(),
                doctor.getDoctorCode(),
                doctor.getUser() != null ? doctor.getUser().getFullName() : null,
                doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentId() : null,
                doctor.getDepartment() != null ? doctor.getDepartment().getDepartmentName() : null,
                doctor.getDegree(),
                doctor.getSpecialization(),
                doctor.getYearsOfExperience(),
                doctor.getConsultationFee(),
                doctor.getStatus(),
                doctor.getCreatedAt()
        );
    }
}