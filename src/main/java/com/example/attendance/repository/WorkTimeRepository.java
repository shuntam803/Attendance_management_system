package com.example.attendance.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.attendance.model.WorkTime;

@Repository
public class WorkTimeRepository {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JdbcTemplate jdbcTemplate;

    public WorkTimeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasClockIn(String employeeCode, LocalDate date) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_work_time WHERE employee_code = ? AND work_date = ?",
                Integer.class,
                employeeCode,
                date
        );
        return count != null && count > 0;
    }

    public boolean hasClockOut(String employeeCode, LocalDate date) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_work_time WHERE employee_code = ? AND work_date = ? AND finish_time IS NOT NULL",
                Integer.class,
                employeeCode,
                date
        );
        return count != null && count > 0;
    }

    public boolean hasBreakStart(String employeeCode, LocalDate date) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_work_time WHERE employee_code = ? AND work_date = ? AND break_start_time IS NOT NULL",
                Integer.class,
                employeeCode,
                date
        );
        return count != null && count > 0;
    }

    public boolean hasBreakFinish(String employeeCode, LocalDate date) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM t_work_time WHERE employee_code = ? AND work_date = ? AND break_finish_time IS NOT NULL",
                Integer.class,
                employeeCode,
                date
        );
        return count != null && count > 0;
    }

    public List<WorkTime> findByMonth(String employeeCode, String monthPrefix) {
        String sql = "SELECT work_date, start_time, finish_time, break_start_time, break_finish_time "
                + "FROM t_work_time "
                + "WHERE employee_code = ? "
                + "AND DATE_TRUNC('month', work_date) = DATE_TRUNC('month', TO_DATE(?, 'yyyy-MM')) "
                + "ORDER BY work_date";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapWorkTime(rs), employeeCode, monthPrefix);
    }

    private WorkTime mapWorkTime(ResultSet rs) throws SQLException {
        WorkTime workTime = new WorkTime();

        workTime.setWorkDate(rs.getObject("work_date", LocalDate.class));
        workTime.setStartTime(rs.getObject("start_time", LocalTime.class));
        workTime.setFinishTime(rs.getObject("finish_time", LocalTime.class));
        workTime.setBreakStartTime(rs.getObject("break_start_time", LocalTime.class));
        workTime.setBreakFinishTime(rs.getObject("break_finish_time", LocalTime.class));

        if (workTime.getBreakStartTime() != null && workTime.getBreakFinishTime() != null) {
            workTime.calcBreakTime();
        }
        if (workTime.getStartTime() != null && workTime.getFinishTime() != null) {
            workTime.calcWorkingHours();
        }

        return workTime;
    }

}
