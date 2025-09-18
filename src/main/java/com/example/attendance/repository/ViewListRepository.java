package com.example.attendance.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.attendance.model.ViewListDisplay;

@Repository
public class ViewListRepository {

    private final JdbcTemplate jdbcTemplate;

    public ViewListRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ViewListDisplay> findAll() {
        String sql = "SELECT e.employee_code, "
                + "concat(e.last_name, e.first_name) AS employee_name, "
                + "concat(e.last_kana_name, e.first_kana_name) AS employee_kana_name, "
                + "e.gender, e.birth_day, s.section_name, e.hire_date "
                + "FROM m_employee e LEFT OUTER JOIN m_section s ON e.section_code = s.section_code";
        return jdbcTemplate.query(sql, this::mapRow);
    }

    private ViewListDisplay mapRow(ResultSet rs, int rowNum) throws SQLException {
        ViewListDisplay display = new ViewListDisplay();
        display.setEmployeeCode(rs.getString("employee_code"));
        display.setEmployeeName(rs.getString("employee_name"));
        display.setEmployeeKanaName(rs.getString("employee_kana_name"));
        display.setGender(rs.getInt("gender"));
        display.setBirthDay(rs.getDate("birth_day"));
        display.setSectionName(rs.getString("section_name"));
        display.setHireDate(rs.getDate("hire_date"));
        return display;
    }
}
