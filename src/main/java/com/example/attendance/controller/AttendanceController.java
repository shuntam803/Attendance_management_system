package com.example.attendance.controller;

import com.example.attendance.controller.form.AttendanceLoginForm;
import com.example.attendance.controller.form.TimesheetForm;
import com.example.attendance.model.Employee;
import com.example.attendance.model.WorkTime;
import com.example.attendance.service.AdminService;
import com.example.attendance.service.AttendanceService;
import com.example.attendance.service.AttendanceService.DailyStatus;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Controller
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
    public String showLogin(HttpSession session,
                            @RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (session.getAttribute("employeeCode") != null) {
            return "redirect:/attendance/menu";
        }
        model.addAttribute("loginForm", new AttendanceLoginForm());
        model.addAttribute("error", error);
        return "attendance/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") AttendanceLoginForm form,
                        HttpSession session) {
        Optional<String> employee = attendanceService.authenticateEmployee(form.getEmployeeCode(), form.getPassword());
        if (employee.isPresent()) {
            session.setAttribute("employeeCode", employee.get());
            return "redirect:/attendance/menu";
        }
        return "redirect:/attendance/login?error=fail";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("employeeCode");
        return "redirect:/attendance/login";
    }

    @GetMapping("/menu")
    public String menu(HttpSession session) {
        if (session.getAttribute("employeeCode") == null) {
            return "redirect:/attendance/login";
        }
        return "attendance/menu";
    }

    @GetMapping("/timecard")
    public String showTimecard(HttpSession session, Model model,
                               @RequestParam(value = "message", required = false) String message,
                               @RequestParam(value = "error", required = false) String error) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return "redirect:/attendance/login";
        }
        DailyStatus status = attendanceService.loadTodayStatus(employeeCode);
        model.addAttribute("status", status);
        model.addAttribute("message", message);
        model.addAttribute("error", error);
        return "attendance/timecard";
    }

    @PostMapping("/timecard")
    public String handleTimecard(@RequestParam("action") String action,
                                 HttpSession session) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return "redirect:/attendance/login";
        }
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
            return "redirect:/attendance/timecard?message=" + encode(message);
        }
        return "redirect:/attendance/timecard?error=" + encode("処理に失敗しました。");
    }

    @GetMapping("/timesheet")
    public String showTimesheetForm(HttpSession session, Model model,
                                    @RequestParam(value = "month", required = false) String selectedMonth) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return "redirect:/attendance/login";
        }
        model.addAttribute("months", generateMonthOptions());
        TimesheetForm form = new TimesheetForm();
        form.setMonth(selectedMonth);
        model.addAttribute("timesheetForm", form);
        return "attendance/timesheet-select";
    }

    @PostMapping("/timesheet")
    public String showTimesheet(@ModelAttribute("timesheetForm") TimesheetForm form,
                                 HttpSession session,
                                 Model model) {
        String employeeCode = (String) session.getAttribute("employeeCode");
        if (employeeCode == null) {
            return "redirect:/attendance/login";
        }
        if (form.getMonth() == null || form.getMonth().isBlank()) {
            model.addAttribute("error", "年月を選択してください。");
            model.addAttribute("months", generateMonthOptions());
            return "attendance/timesheet-select";
        }
        List<WorkTime> workTimes = attendanceService.findMonthlyWorkTimes(employeeCode, form.getMonth());

        // === デバッグ出力 ===
        System.out.println("---- WorkTimes ----");
        for (WorkTime wt : workTimes) {
            System.out.println("Date: " + wt.getWorkDate()
                    + ", Start: " + wt.getStartTime()
                    + ", Finish: " + wt.getFinishTime()
                    + ", BreakStart: " + wt.getBreakStartTime()
                    + ", BreakFinish: " + wt.getBreakFinishTime());
        }


        Optional<Employee> employeeOpt = adminService.findEmployee(employeeCode);
        model.addAttribute("employeeName", employeeOpt.map(e -> e.getLastName() + " " + e.getFirstName()).orElse(employeeCode));
        model.addAttribute("selectedMonth", form.getMonth());
        model.addAttribute("workTimes", workTimes);
        model.addAttribute("monthDays", computeDays(form.getMonth()));
        Map<Integer, WorkTime> workTimeMap = workTimes.stream()
                .collect(Collectors.toMap(w -> w.getWorkDate().getDayOfMonth(), Function.identity(), (a, b) -> a));
        model.addAttribute("workTimeMap", workTimeMap);


        // === デバッグ出力 ===
        System.out.println("---- WorkTimeMap ----");
        workTimeMap.forEach((day, wt) -> {
            System.out.println(day + "日 => Start: " + wt.getStartTime() + ", Finish: " + wt.getFinishTime());
        });
        return "attendance/timesheet-view";
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

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
