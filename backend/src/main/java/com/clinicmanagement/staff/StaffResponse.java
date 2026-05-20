package com.clinicmanagement.staff;

public record StaffResponse(
        Long staffId,
        Long userId,
        String fullName,
        String email,
        String staffCode,
        String staffType,
        String position,
        String status
) {
    public static StaffResponse from(Staff s) {
        return new StaffResponse(
                s.getStaffId(),
                s.getUser().getUserId(),
                s.getUser().getFullName(),
                s.getUser().getEmail(),
                s.getStaffCode(),
                s.getStaffType(),
                s.getPosition(),
                s.getStatus()
        );
    }
}
