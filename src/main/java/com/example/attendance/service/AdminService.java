package com.example.attendance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.attendance.model.Employee;
import com.example.attendance.model.Section;
import com.example.attendance.model.ViewListDisplay;
import com.example.attendance.repository.EmployeeRepository;
import com.example.attendance.repository.UserRepository;
import com.example.attendance.repository.ViewListRepository;

@Service
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final ViewListRepository viewListRepository;

    public AdminService(UserRepository userRepository,
                        EmployeeRepository employeeRepository,
                        ViewListRepository viewListRepository) {
        this.userRepository = userRepository;
        this.employeeRepository = employeeRepository;
        this.viewListRepository = viewListRepository;
    }

    public boolean authenticateAdmin(String userId, String password) {
        return userRepository.authenticate(userId, password);
    }

    @Transactional
    public boolean registerAdmin(String userId, String password) {
        return userRepository.insert(userId, password);
    }

    public Optional<Employee> findEmployee(String employeeCode) {
        return employeeRepository.findByCode(employeeCode);
    }

    @Transactional
    public String registerEmployee(Employee employee) {
        return employeeRepository.insert(employee);
    }

    @Transactional
    public boolean updateEmployee(Employee employee) {
        return employeeRepository.update(employee);
    }

    @Transactional
    public boolean deleteEmployee(String employeeCode) {
        return employeeRepository.delete(employeeCode);
    }

    public List<Section> listSections() {
        return employeeRepository.findAllSections();
    }

    public List<ViewListDisplay> listEmployees() {
        return viewListRepository.findAll();
    }
}
