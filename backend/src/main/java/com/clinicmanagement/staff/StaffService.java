package com.clinicmanagement.staff;

import com.clinicmanagement.common.dto.PageResponse;
import com.clinicmanagement.common.exception.BusinessException;
import com.clinicmanagement.common.exception.ResourceNotFoundException;
import com.clinicmanagement.user.User;
import com.clinicmanagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageResponse<StaffResponse> getStaffList(String staffType, String status, String keyword, Pageable pageable) {
        Page<Staff> page = staffRepository.searchStaff(staffType, status, keyword, pageable);
        return PageResponse.from(page.map(StaffResponse::from));
    }

    @Transactional(readOnly = true)
    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + id));
        return StaffResponse.from(staff);
    }

    @Transactional
    public StaffResponse createStaff(StaffRequest request) {
        if (staffRepository.existsByStaffCode(request.staffCode())) {
            throw new BusinessException("Mã nhân viên đã tồn tại");
        }
        if (staffRepository.existsByUser_UserId(request.userId())) {
            throw new BusinessException("User này đã là một nhân viên");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với ID: " + request.userId()));

        Staff staff = new Staff();
        staff.setUser(user);
        staff.setStaffCode(request.staffCode());
        staff.setStaffType(request.staffType());
        staff.setPosition(request.position());
        staff.setStatus("ACTIVE");

        return StaffResponse.from(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffRequest request) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + id));

        if (!staff.getStaffCode().equals(request.staffCode()) && staffRepository.existsByStaffCode(request.staffCode())) {
            throw new BusinessException("Mã nhân viên đã tồn tại");
        }

        if (request.userId() != null && !staff.getUser().getUserId().equals(request.userId())) {
            if (staffRepository.existsByUser_UserId(request.userId())) {
                throw new BusinessException("User này đã là một nhân viên");
            }
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy User với ID: " + request.userId()));
            staff.setUser(user);
        }

        staff.setStaffCode(request.staffCode());
        staff.setStaffType(request.staffType());
        staff.setPosition(request.position());
        if (request.status() != null) {
            staff.setStatus(request.status());
        }

        return StaffResponse.from(staffRepository.save(staff));
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + id));
        staffRepository.delete(staff);
    }
}
