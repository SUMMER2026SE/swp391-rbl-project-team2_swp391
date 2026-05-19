package com.clinicmanagement.doctor;

import com.clinicmanagement.common.dto.PageResponse;
import com.clinicmanagement.common.exception.BusinessException;
import com.clinicmanagement.common.exception.ResourceNotFoundException;
import com.clinicmanagement.department.Department;
import com.clinicmanagement.department.DepartmentRepository;
import com.clinicmanagement.user.User;
import com.clinicmanagement.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional(readOnly = true)
    public PageResponse<DoctorResponse> getAll(Long departmentId, String keyword, String status, Pageable pageable) {
        Page<Doctor> page = doctorRepository.searchDoctors(departmentId, keyword, status, pageable);
        return PageResponse.from(page.map(DoctorResponse::from));
    }

    @Transactional(readOnly = true)
    public DoctorResponse getById(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + id));
        return DoctorResponse.from(doctor);
    }

    @Transactional
    public DoctorResponse create(DoctorRequest request) {
        if (doctorRepository.existsByDoctorCode(request.doctorCode())) {
            throw new BusinessException("Mã bác sĩ đã tồn tại");
        }
        if (doctorRepository.existsByUser_UserId(request.userId())) {
            throw new BusinessException("Người dùng này đã là bác sĩ");
        }

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + request.userId()));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + request.departmentId()));

        Doctor doctor = Doctor.builder()
                .user(user)
                .department(department)
                .doctorCode(request.doctorCode())
                .degree(request.degree())
                .specialization(request.specialization())
                .yearsOfExperience(request.yearsOfExperience() != null ? request.yearsOfExperience() : 0)
                .biography(request.biography())
                .consultationFee(request.consultationFee())
                .status(request.status() != null ? request.status() : "ACTIVE")
                .build();

        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    @Transactional
    public DoctorResponse update(Long id, DoctorRequest request) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + id));

        if (!doctor.getDoctorCode().equals(request.doctorCode()) && doctorRepository.existsByDoctorCode(request.doctorCode())) {
            throw new BusinessException("Mã bác sĩ đã tồn tại");
        }

        if (!doctor.getUser().getUserId().equals(request.userId())) {
            if (doctorRepository.existsByUser_UserId(request.userId())) {
                throw new BusinessException("Người dùng này đã là bác sĩ");
            }
            User user = userRepository.findById(request.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + request.userId()));
            doctor.setUser(user);
        }

        if (!doctor.getDepartment().getDepartmentId().equals(request.departmentId())) {
            Department department = departmentRepository.findById(request.departmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy chuyên khoa với ID: " + request.departmentId()));
            doctor.setDepartment(department);
        }

        doctor.setDoctorCode(request.doctorCode());
        doctor.setDegree(request.degree());
        doctor.setSpecialization(request.specialization());
        if (request.yearsOfExperience() != null) {
            doctor.setYearsOfExperience(request.yearsOfExperience());
        }
        doctor.setBiography(request.biography());
        if (request.consultationFee() != null) {
            doctor.setConsultationFee(request.consultationFee());
        }
        if (request.status() != null) {
            doctor.setStatus(request.status());
        }

        return DoctorResponse.from(doctorRepository.save(doctor));
    }

    @Transactional
    public void delete(Long id) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bác sĩ với ID: " + id));
        // Additional checks like "doctor has appointments" could be added here
        doctorRepository.delete(doctor);
    }
}
