package com.example.attendance.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AttendanceEmployeeRepository {


    private final JdbcTemplate jdbcTemplate;

    public AttendanceEmployeeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<String> authenticate(String employeeCode, String password) {
        List<String> codes = jdbcTemplate.query(
                "SELECT employee_code FROM m_employee WHERE employee_code = ? AND password = ?",
                (rs, rowNum) -> rs.getString("employee_code"), employeeCode, password);
        return codes.stream().findFirst();
    }

    public boolean clockIn(String employeeCode, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_work_time WHERE employee_code = ? AND work_date = ?",
                Integer.class, employeeCode, date);
        if (count != null && count > 0) {
            return false;
        }
        return jdbcTemplate.update(
                "INSERT INTO t_work_time (employee_code, work_date, start_time) VALUES (?, ?, ?)",
                employeeCode,
                date,
                time) > 0;
    }

    public boolean clockOut(String employeeCode, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        return jdbcTemplate.update(
                "UPDATE t_work_time SET finish_time = ? WHERE employee_code = ? AND work_date = ? AND start_time IS NOT NULL AND finish_time IS NULL",
                time,
                employeeCode,
                date) > 0;
    }

    public boolean startBreak(String employeeCode, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        return jdbcTemplate.update(
                "UPDATE t_work_time SET break_start_time = ? WHERE employee_code = ? AND work_date = ? AND start_time IS NOT NULL AND break_start_time IS NULL",
                time,
                employeeCode,
                date) > 0;
    }

    public boolean finishBreak(String employeeCode, LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();
        return jdbcTemplate.update(
                "UPDATE t_work_time SET break_finish_time = ? WHERE employee_code = ? AND work_date = ? AND break_start_time IS NOT NULL AND break_finish_time IS NULL",
                time,
                employeeCode,
                date) > 0;
    }
}
