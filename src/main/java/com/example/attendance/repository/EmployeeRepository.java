package com.example.attendance.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.attendance.model.Employee;
import com.example.attendance.model.Section;

@Repository
public class EmployeeRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Employee> findByCode(String employeeCode) {
        List<Employee> results = jdbcTemplate.query(
                "SELECT employee_code, last_name, first_name, last_kana_name, first_kana_name, gender, birth_day, section_code, hire_date, password " +
                        "FROM m_employee WHERE employee_code = ?",
                employeeRowMapper(),
                employeeCode);
        return results.stream().findFirst();
    }

    public String insert(Employee employee) {
        String employeeCode = generateEmployeeCode();
        int updated = jdbcTemplate.update(
                "INSERT INTO m_employee (employee_code, last_name, first_name, last_kana_name, first_kana_name, gender, birth_day, section_code, hire_date, password) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                employeeCode,
                employee.getLastName(),
                employee.getFirstName(),
                employee.getLastKanaName(),
                employee.getFirstKanaName(),
                employee.getGender(),
                employee.getBirthDay(),      // LocalDate → java.sql.Date に自動変換される
                employee.getSectionCode(),
                employee.getHireDate(),      // LocalDate → java.sql.Date に自動変換される
                employee.getPassword());
        return updated > 0 ? employeeCode : null;
    }

    public boolean update(Employee employee) {
        return jdbcTemplate.update(
                "UPDATE m_employee SET last_name = ?, first_name = ?, last_kana_name = ?, first_kana_name = ?, gender = ?, birth_day = ?, section_code = ?, hire_date = ? WHERE employee_code = ?",
                employee.getLastName(),
                employee.getFirstName(),
                employee.getLastKanaName(),
                employee.getFirstKanaName(),
                employee.getGender(),
                employee.getBirthDay(),      // LocalDate
                employee.getSectionCode(),
                employee.getHireDate(),      // LocalDate
                employee.getEmployeeCode()) > 0;
    }

    public boolean delete(String employeeCode) {
        return jdbcTemplate.update("DELETE FROM m_employee WHERE employee_code = ?", employeeCode) > 0;
    }

    public List<Section> findAllSections() {
        return jdbcTemplate.query(
                "SELECT section_code, section_name FROM m_section ORDER BY section_code",
                (rs, rowNum) -> {
                    Section section = new Section();
                    section.setSectionCode(rs.getString("section_code"));
                    section.setSectionName(rs.getString("section_name"));
                    return section;
                });
    }

    private String generateEmployeeCode() {
        String latest = jdbcTemplate.queryForObject("SELECT MAX(employee_code) FROM m_employee", String.class);
        int nextNumber = 1;
        if (latest != null && latest.length() > 1) {
            nextNumber = Integer.parseInt(latest.substring(1)) + 1;
        }
        return "E" + String.format("%04d", nextNumber);
    }

    private RowMapper<Employee> employeeRowMapper() {
        return (rs, rowNum) -> mapEmployee(rs);
    }

    private Employee mapEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setEmployeeCode(rs.getString("employee_code"));
        employee.setLastName(rs.getString("last_name"));
        employee.setFirstName(rs.getString("first_name"));
        employee.setLastKanaName(rs.getString("last_kana_name"));
        employee.setFirstKanaName(rs.getString("first_kana_name"));
        employee.setGender(rs.getInt("gender"));

        // LocalDate に変換
        employee.setBirthDay(rs.getObject("birth_day", LocalDate.class));
        employee.setSectionCode(rs.getString("section_code"));
        employee.setHireDate(rs.getObject("hire_date", LocalDate.class));
        employee.setPassword(rs.getString("password"));
        return employee;
    }
}
