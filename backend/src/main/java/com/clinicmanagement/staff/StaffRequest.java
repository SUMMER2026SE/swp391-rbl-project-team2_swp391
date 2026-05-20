package com.clinicmanagement.staff;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record StaffRequest(
        @NotNull(message = "ID người dùng không được để trống")
        Long userId,

        @NotBlank(message = "Mã nhân viên không được để trống")
        String staffCode,

        @NotBlank(message = "Loại nhân viên không được để trống")
        @Pattern(regexp = "RECEPTIONIST|PHARMACIST|LAB_TECHNICIAN|ADMIN",
                message = "Loại nhân viên phải là RECEPTIONIST, PHARMACIST, LAB_TECHNICIAN hoặc ADMIN")
        String staffType,

        String position,

        @Pattern(regexp = "ACTIVE|INACTIVE", message = "Trạng thái phải là ACTIVE hoặc INACTIVE")
        String status
) {
}
