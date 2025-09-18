package com.example.attendance.controller;

import com.example.attendance.controller.form.EmployeeForm;
import com.example.attendance.model.Employee;
import com.example.attendance.model.Section;
import com.example.attendance.model.ViewListDisplay;
import com.example.attendance.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/employees")
public class AdminEmployeeController {

    private final AdminService adminService;

    public AdminEmployeeController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public ResponseEntity<EmployeeListResponse> listEmployees(HttpSession session,
                                                              @RequestParam(value = "error", required = false) String error) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EmployeeListResponse(List.of(), "ログインしてください。"));
        }
        List<ViewListDisplay> employees = adminService.listEmployees();
        return ResponseEntity.ok(new EmployeeListResponse(employees, error));
    }

    @GetMapping("/new")
    public ResponseEntity<EmployeeFormResponse> showCreateForm(HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EmployeeFormResponse(null, listSections(), "ログインしてください。", "create"));
        }
        return ResponseEntity.ok(new EmployeeFormResponse(new EmployeeForm(), listSections(), null, "create"));
    }

    @PostMapping
    public ResponseEntity<SimpleMessageResponse> createEmployee(@RequestBody EmployeeForm form,
                                                                HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessageResponse("ログインしてください。"));
        }
        String validationError = validateEmployeeForm(form, true);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleMessageResponse(validationError));
        }
        String employeeCode = adminService.registerEmployee(form.toNewEmployee());
        if (employeeCode != null) {
            return ResponseEntity.ok(new SimpleMessageResponse("従業員コード " + employeeCode + " を登録しました。"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SimpleMessageResponse("従業員の登録に失敗しました。"));
    }

    @PostMapping("/action")
    public ResponseEntity<EmployeeActionResponse> selectEmployeeAction(@RequestBody EmployeeActionRequest request,
                                                                       HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EmployeeActionResponse(null, "ログインしてください。"));
        }
        String employeeCode = request.employeeCode();
        String action = request.action();
        if (employeeCode == null || employeeCode.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new EmployeeActionResponse(null, "従業員を選択してください。"));
        }
        if ("edit".equals(action) || "delete".equals(action)) {
            return ResponseEntity.ok(new EmployeeActionResponse("/admin/employees/" + employeeCode + "/" + action, null));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new EmployeeActionResponse(null, "不明な操作です。"));
    }

    @GetMapping("/{employeeCode}/edit")
    public ResponseEntity<EmployeeFormResponse> showEditForm(@PathVariable String employeeCode,
                                                             HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EmployeeFormResponse(null, listSections(), "ログインしてください。", "edit"));
        }
        Optional<Employee> employeeOpt = adminService.findEmployee(employeeCode);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EmployeeFormResponse(null, listSections(), "従業員が見つかりません。", "edit"));
        }
        Employee employee = employeeOpt.get();
        EmployeeForm form = new EmployeeForm();
        form.setEmployeeCode(employee.getEmployeeCode());
        form.setLastName(employee.getLastName());
        form.setFirstName(employee.getFirstName());
        form.setLastKanaName(employee.getLastKanaName());
        form.setFirstKanaName(employee.getFirstKanaName());
        form.setGender(String.valueOf(employee.getGender()));
        form.setBirthDay(employee.getBirthDay());
        form.setSectionCode(employee.getSectionCode());
        form.setHireDate(employee.getHireDate());
        return ResponseEntity.ok(new EmployeeFormResponse(form, listSections(), null, "edit"));
    }

    @PostMapping("/{employeeCode}/edit")
    public ResponseEntity<SimpleMessageResponse> updateEmployee(@PathVariable String employeeCode,
                                                                @RequestBody EmployeeForm form,
                                                                HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessageResponse("ログインしてください。"));
        }
        form.setEmployeeCode(employeeCode);
        String validationError = validateEmployeeForm(form, false);
        if (validationError != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleMessageResponse(validationError));
        }
        boolean updated = adminService.updateEmployee(form.toExistingEmployee());
        if (updated) {
            return ResponseEntity.ok(new SimpleMessageResponse("従業員情報を更新しました。"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SimpleMessageResponse("従業員情報の更新に失敗しました。"));
    }

    @GetMapping("/{employeeCode}/delete")
    public ResponseEntity<EmployeeDetailResponse> confirmDelete(@PathVariable String employeeCode,
                                                                HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new EmployeeDetailResponse(null, "ログインしてください。"));
        }
        Optional<Employee> employeeOpt = adminService.findEmployee(employeeCode);
        if (employeeOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new EmployeeDetailResponse(null, "従業員が見つかりません。"));
        }
        return ResponseEntity.ok(new EmployeeDetailResponse(employeeOpt.get(), null));
    }

    @PostMapping("/{employeeCode}/delete")
    public ResponseEntity<SimpleMessageResponse> deleteEmployee(@PathVariable String employeeCode,
                                                                HttpSession session) {
        if (!isLoggedIn(session)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessageResponse("ログインしてください。"));
        }
        boolean deleted = adminService.deleteEmployee(employeeCode);
        if (deleted) {
            return ResponseEntity.ok(new SimpleMessageResponse("従業員を削除しました。"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new SimpleMessageResponse("従業員の削除に失敗しました。"));
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginUserId") != null;
    }

    private List<Section> listSections() {
        return adminService.listSections();
    }

    private String validateEmployeeForm(EmployeeForm form, boolean requirePassword) {
        if (isBlank(form.getGender()) ||
                form.getBirthDay() == null ||
                isBlank(form.getSectionCode()) ||
                form.getHireDate() == null) {
            return "全ての必須項目を入力してください。";
        }
        if (requirePassword) {
            if (isBlank(form.getPassword())) {
                return "パスワードを入力してください。";
            }
            if (!form.getPassword().equals(form.getConfirmation())) {
                return "パスワードが一致しません。";
            }
        }
        return null;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    public static record EmployeeListResponse(List<ViewListDisplay> employees, String error) {}

    public static record EmployeeFormResponse(EmployeeForm form,
                                              List<Section> sections,
                                              String error,
                                              String mode) {}

    public static record SimpleMessageResponse(String message) {}

    public static record EmployeeActionRequest(String employeeCode, String action) {}

    public static record EmployeeActionResponse(String nextPath, String error) {}

    public static record EmployeeDetailResponse(Employee employee, String error) {}
}
