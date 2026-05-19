package com.clinicmanagement.doctor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;

public record DoctorRequest(
        @NotNull(message = "User ID không được để trống")
        Long userId,

        @NotNull(message = "Department ID không được để trống")
        Long departmentId,

        @NotBlank(message = "Mã bác sĩ không được để trống")
        String doctorCode,

        String degree,

        String specialization,

        @Min(value = 0, message = "Số năm kinh nghiệm phải lớn hơn hoặc bằng 0")
        Integer yearsOfExperience,

        String biography,

        @Min(value = 0, message = "Phí tư vấn phải lớn hơn hoặc bằng 0")
        BigDecimal consultationFee,

        @Pattern(regexp = "ACTIVE|INACTIVE|ON_LEAVE", message = "Trạng thái không hợp lệ")
        String status
) {
}