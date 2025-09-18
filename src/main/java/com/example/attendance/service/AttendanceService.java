package com.example.attendance.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.attendance.model.WorkTime;
import com.example.attendance.repository.AttendanceEmployeeRepository;
import com.example.attendance.repository.WorkTimeRepository;

@Service
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceEmployeeRepository attendanceRepository;
    private final WorkTimeRepository workTimeRepository;

    public AttendanceService(AttendanceEmployeeRepository attendanceRepository,
                             WorkTimeRepository workTimeRepository) {
        this.attendanceRepository = attendanceRepository;
        this.workTimeRepository = workTimeRepository;
    }

    public Optional<String> authenticateEmployee(String employeeCode, String password) {
        return attendanceRepository.authenticate(employeeCode, password);
    }

    @Transactional
    public boolean clockIn(String employeeCode) {
        return attendanceRepository.clockIn(employeeCode, LocalDateTime.now());
    }

    @Transactional
    public boolean clockOut(String employeeCode) {
        return attendanceRepository.clockOut(employeeCode, LocalDateTime.now());
    }

    @Transactional
    public boolean startBreak(String employeeCode) {
        return attendanceRepository.startBreak(employeeCode, LocalDateTime.now());
    }

    @Transactional
    public boolean finishBreak(String employeeCode) {
        return attendanceRepository.finishBreak(employeeCode, LocalDateTime.now());
    }

    public DailyStatus loadTodayStatus(String employeeCode) {
        LocalDate today = LocalDate.now();
        boolean hasStart = workTimeRepository.hasClockIn(employeeCode, today);
        boolean hasFinish = workTimeRepository.hasClockOut(employeeCode, today);
        boolean hasBreakStart = workTimeRepository.hasBreakStart(employeeCode, today);
        boolean hasBreakFinish = workTimeRepository.hasBreakFinish(employeeCode, today);
        return new DailyStatus(hasStart, hasFinish, hasBreakStart, hasBreakFinish);
    }

    public List<WorkTime> findMonthlyWorkTimes(String employeeCode, String month) {
        return workTimeRepository.findByMonth(employeeCode, month);
    }

    public record DailyStatus(boolean clockedIn, boolean clockedOut, boolean breakStarted, boolean breakFinished) { }
}
