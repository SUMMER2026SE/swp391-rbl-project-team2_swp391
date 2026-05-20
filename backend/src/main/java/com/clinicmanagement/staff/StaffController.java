package com.clinicmanagement.staff;

import com.clinicmanagement.common.dto.ApiResponse;
import com.clinicmanagement.common.dto.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<StaffResponse>>> getAll(
            @RequestParam(required = false) String staffType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "staffId") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(ApiResponse.success(
                staffService.getStaffList(staffType, status, keyword, pageable)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(staffService.getStaffById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StaffResponse>> create(
            @Valid @RequestBody StaffRequest request
    ) {
        StaffResponse created = staffService.createStaff(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo nhân viên thành công", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody StaffRequest request
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật nhân viên thành công", staffService.updateStaff(id, request))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa nhân viên thành công", null));
    }
}
