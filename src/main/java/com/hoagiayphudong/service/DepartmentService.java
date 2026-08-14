package com.hoagiayphudong.service;

import java.util.List;

import com.hoagiayphudong.helper.exception.ResourceAlreadyExistsException;
import com.hoagiayphudong.helper.exception.ResourceNotFoundException;
import com.hoagiayphudong.dto.DepartmentRequest;
import com.hoagiayphudong.dto.DepartmentResponse;
import com.hoagiayphudong.dto.PageResponse;
import com.hoagiayphudong.helper.pagination.PageableHelper;
import com.hoagiayphudong.helper.specification.DepartmentSpecification;
import com.hoagiayphudong.model.Department;
import com.hoagiayphudong.repository.DepartmentRepository;
import com.hoagiayphudong.repository.ManagerRepository;
import com.hoagiayphudong.security.SecurityPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final ManagerRepository managerRepository;

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> findAll(Pageable pageable, String keyword, Boolean active) {
        Pageable safePageable = PageableHelper.normalize(pageable, Sort.by(Sort.Direction.ASC, "id"));
        return PageResponse.from(
                departmentRepository.findAll(DepartmentSpecification.filter(keyword, active), safePageable),
                DepartmentResponse::from
        );
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public List<DepartmentResponse> findAllOptions() {
        return departmentRepository.findAllByOrderByIdAsc()
                .stream()
                .map(DepartmentResponse::from)
                .toList();
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public DepartmentResponse findById(Long id) {
        return DepartmentResponse.from(findDepartment(id));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        String name = cleanText(request.name());
        validateUniqueName(name, null);

        Department department = new Department();
        copyRequestToDepartment(request, department);

        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findDepartment(id);
        String name = cleanText(request.name());
        validateUniqueName(name, id);
        copyRequestToDepartment(request, department);

        return DepartmentResponse.from(departmentRepository.save(department));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public void delete(Long id) {
        Department department = findDepartment(id);
        if (managerRepository.existsByDepartmentId(id)) {
            throw new IllegalStateException("Không thể xoá bộ phận đang có nhân viên.");
        }

        departmentRepository.delete(department);
    }

    private Department findDepartment(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ phận id " + id));
    }

    private void copyRequestToDepartment(DepartmentRequest request, Department department) {
        department.setName(cleanText(request.name()));
        department.setDescription(cleanText(request.description()));
        department.setActive(request.active() == null || request.active());
    }

    private void validateUniqueName(String name, Long ignoredDepartmentId) {
        boolean exists = ignoredDepartmentId == null
                ? departmentRepository.existsByNameIgnoreCase(name)
                : departmentRepository.existsByNameIgnoreCaseAndIdNot(name, ignoredDepartmentId);

        if (exists) {
            throw new ResourceAlreadyExistsException("Tên bộ phận đã tồn tại.");
        }
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
