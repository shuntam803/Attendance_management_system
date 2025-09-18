package com.example.attendance.controller;

import com.example.attendance.controller.form.AttendanceLoginForm;
import com.example.attendance.controller.form.TimesheetForm;
import com.example.attendance.model.Employee;
import com.example.attendance.model.WorkTime;
import com.example.attendance.service.AdminService;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.AttendanceService.DailyStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/attendance")
public class AttendanceController {

    private static final DateTimeFormatter YEAR_MONTH_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM");

    private final AttendanceService attendanceService;
    private final AdminService adminService;

    public AttendanceController(AttendanceService attendanceService,
                                AdminService adminService) {
        this.attendanceService = attendanceService;
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public ResponseEntity<LoginStatusResponse> showLogin(HttpSession session,
                                                         @RequestParam(value = "error", required = false) String error) {
        boolean loggedIn = session.getAttribute("employeeCode") != null;
        return ResponseEntity.ok(new LoginStatusResponse(loggedIn, error));
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> loginJson(@RequestBody AttendanceLoginForm form,
                                                   HttpSession session) {
        return authenticateEmployee(form, session);
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<LoginResponse> loginForm(AttendanceLoginForm form,
                                                   HttpSession session) {
        return authenticateEmployee(form, session);
    }

    private ResponseEntity<LoginResponse> authenticateEmployee(AttendanceLoginForm form,
                                                               HttpSession session) {
        Optional<String> employee = attendanceService.authenticateEmployee(form.getEmployeeCode(), form.getPassword());
        if (employee.isPresent()) {
            session.setAttribute("employeeCode", employee.get());
            return ResponseEntity.ok(new LoginResponse(true, null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse(false, "認証に失敗しました。"));
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleMessageResponse> logout(HttpSession session) {
        session.removeAttribute("employeeCode");
        return ResponseEntity.ok(new SimpleMessageResponse("ログアウトしました。"));
    }

    @GetMapping("/menu")
    public ResponseEntity<MenuResponse> menu(HttpSession session) {
        if (session.getAttribute("employeeCode") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MenuResponse(false));
        }
        return ResponseEntity.ok(new MenuResponse(true));
    }

    @GetMapping("/timecard")
    public ResponseEntity<TimecardResponse> showTimecard(HttpSession session,
                                                         @RequestParam(value = "message", required = false) String message,
                                                         @RequestParam(value = "error", required = false) String error) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TimecardResponse(null, null, "ログインしてください。"));
        }
        DailyStatus status = attendanceService.loadTodayStatus(employeeCode);
        return ResponseEntity.ok(new TimecardResponse(status, message, error));
    }

    @PostMapping("/timecard")
    public ResponseEntity<SimpleMessageResponse> handleTimecard(@RequestBody TimecardActionRequest request,
                                                                HttpSession session) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessageResponse("ログインしてください。"));
        }
        String action = request.action();
        boolean success = switch (action) {
            case "clockIn" -> attendanceService.clockIn(employeeCode);
            case "clockOut" -> attendanceService.clockOut(employeeCode);
            case "startBreak" -> attendanceService.startBreak(employeeCode);
            case "finishBreak" -> attendanceService.finishBreak(employeeCode);
            default -> false;
        };
        if (success) {
            String message = switch (action) {
                case "clockIn" -> "出勤処理が完了しました。";
                case "clockOut" -> "退勤処理が完了しました。";
                case "startBreak" -> "休憩開始を登録しました。";
                case "finishBreak" -> "休憩終了を登録しました。";
                default -> "処理が完了しました。";
            };
            return ResponseEntity.ok(new SimpleMessageResponse(message));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new SimpleMessageResponse("処理に失敗しました。"));
    }

    @GetMapping("/timesheet")
    public ResponseEntity<TimesheetOptionsResponse> showTimesheetForm(HttpSession session,
                                                                      @RequestParam(value = "month", required = false) String selectedMonth) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new TimesheetOptionsResponse(List.of(), null, "ログインしてください。"));
        }
        return ResponseEntity.ok(new TimesheetOptionsResponse(generateMonthOptions(), selectedMonth, null));
    }

    @PostMapping("/timesheet")
    public ResponseEntity<?> showTimesheet(@RequestBody TimesheetForm form,
                                           HttpSession session) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessageResponse("ログインしてください。"));
        }
        if (form.getMonth() == null || form.getMonth().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new TimesheetOptionsResponse(generateMonthOptions(), null, "年月を選択してください。"));
        }
        List<WorkTime> workTimes = attendanceService.findMonthlyWorkTimes(employeeCode, form.getMonth());
        Optional<Employee> employeeOpt = adminService.findEmployee(employeeCode);
        Map<Integer, WorkTime> workTimeMap = workTimes.stream()
                .collect(Collectors.toMap(w -> w.getWorkDate().getDayOfMonth(), Function.identity(), (a, b) -> a));
        TimesheetResponse response = new TimesheetResponse(
                employeeOpt.map(e -> e.getLastName() + " " + e.getFirstName()).orElse(employeeCode),
                form.getMonth(),
                workTimes,
                computeDays(form.getMonth()),
                workTimeMap
        );
        return ResponseEntity.ok(response);
    }

    private List<String> generateMonthOptions() {
        List<String> months = new ArrayList<>();
        YearMonth current = YearMonth.now();
        for (int i = 0; i < 12; i++) {
            months.add(current.minusMonths(i).format(YEAR_MONTH_FORMAT));
        }
        return months;
    }

    private List<Integer> computeDays(String yearMonth) {
        YearMonth ym = YearMonth.parse(yearMonth, YEAR_MONTH_FORMAT);
        List<Integer> days = new ArrayList<>();
        for (int day = 1; day <= ym.lengthOfMonth(); day++) {
            days.add(day);
        }
        return days;
    }


    public static record LoginStatusResponse(boolean loggedIn, String error) {}

    public static record LoginResponse(boolean success, String message) {}

    public static record SimpleMessageResponse(String message) {}

    public static record MenuResponse(boolean loggedIn) {}

    public static record TimecardResponse(DailyStatus status, String message, String error) {}

    public static record TimecardActionRequest(String action) {}

    public static record TimesheetOptionsResponse(List<String> months, String selectedMonth, String error) {}

    public static record TimesheetResponse(String employeeName,
                                           String selectedMonth,
                                           List<WorkTime> workTimes,
                                           List<Integer> monthDays,
                                           Map<Integer, WorkTime> workTimeMap) {}
}
