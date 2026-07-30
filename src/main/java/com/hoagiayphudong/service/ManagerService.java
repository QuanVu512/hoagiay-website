package com.hoagiayphudong.service;

import com.hoagiayphudong.dto.ManagerOptionResponse;
import com.hoagiayphudong.dto.ManagerRequest;
import com.hoagiayphudong.dto.ManagerResponse;
import com.hoagiayphudong.helper.exception.ResourceNotFoundException;
import com.hoagiayphudong.model.Department;
import com.hoagiayphudong.model.Manager;
import com.hoagiayphudong.repository.DepartmentRepository;
import com.hoagiayphudong.repository.ManagerRepository;
import com.hoagiayphudong.security.SecurityPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final DepartmentRepository departmentRepository;

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public List<ManagerResponse> findAll() {
        return managerRepository.findAllByOrderByIdAsc()
                .stream()
                .map(ManagerResponse::from)
                .toList();
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public ManagerResponse findById(Long id) {
        return ManagerResponse.from(findManager(id));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional(readOnly = true)
    public List<ManagerOptionResponse> findManagersWithoutAccount() {
        return managerRepository.findManagersWithoutAccount()
                .stream()
                .map(ManagerOptionResponse::from)
                .toList();
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public ManagerResponse create(ManagerRequest request) {
        Manager manager = new Manager();
        copyRequestToManager(request, manager);

        return ManagerResponse.from(managerRepository.save(manager));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public ManagerResponse update(Long id, ManagerRequest request) {
        Manager manager = findManager(id);
        copyRequestToManager(request, manager);

        return ManagerResponse.from(managerRepository.save(manager));
    }

    @PreAuthorize(SecurityPermission.ADMIN)
    @Transactional
    public void delete(Long id) {
        Manager manager = findManager(id);
        if (manager.getUser() != null) {
            throw new IllegalStateException("Không thể xoá hồ sơ đang có tài khoản. Hãy xoá tài khoản trước.");
        }

        managerRepository.delete(manager);
    }

    private Manager findManager(Long id) {
        return managerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hồ sơ nhân viên id " + id));
    }

    private void copyRequestToManager(ManagerRequest request, Manager manager) {
        manager.setFullName(cleanText(request.fullName()));
        manager.setPhone(cleanText(request.phone()));
        manager.setDepartment(findDepartment(request.departmentId()));
        manager.setAddressDetail(cleanText(request.addressDetail()));
        manager.setWard(cleanText(request.ward()));
        manager.setDistrict(cleanText(request.district()));
        manager.setProvince(cleanText(request.province()));
    }

    private Department findDepartment(Long departmentId) {
        if (departmentId == null) {
            return null;
        }

        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bộ phận id " + departmentId));
    }

    private String cleanText(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
