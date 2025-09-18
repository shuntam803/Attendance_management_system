package com.example.attendance.controller;

import com.example.attendance.controller.form.EmployeeForm;
import com.example.attendance.model.Employee;
import com.example.attendance.model.Section;
import com.example.attendance.model.ViewListDisplay;
import com.example.attendance.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/employees")
public class AdminEmployeeController {

    private final AdminService adminService;

    public AdminEmployeeController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String listEmployees(HttpSession session, Model model,
                                @RequestParam(value = "error", required = false) String error) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        List<ViewListDisplay> employees = adminService.listEmployees();
        model.addAttribute("employees", employees);
        model.addAttribute("error", error);
        return "admin/employees/list";
    }

    @GetMapping("/new")
    public String showCreateForm(HttpSession session, Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        populateSections(model);
        if (!model.containsAttribute("employeeForm")) {
            model.addAttribute("employeeForm", new EmployeeForm());
        }
        return "admin/employees/form";
    }

    @PostMapping
    public String createEmployee(@ModelAttribute("employeeForm") EmployeeForm form,
                                 HttpSession session,
                                 Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        String validationError = validateEmployeeForm(form, true);
        if (validationError != null) {
            populateSections(model);
            model.addAttribute("employeeForm", form);
            model.addAttribute("error", validationError);
            return "admin/employees/form";
        }
        String employeeCode = adminService.registerEmployee(form.toNewEmployee());
        if (employeeCode != null) {
            model.addAttribute("title", "登録完了");
            model.addAttribute("message", "従業員コード " + employeeCode + " を登録しました。");
            model.addAttribute("backLink", "/admin/employees");
            model.addAttribute("backText", "一覧に戻る");
            return "admin/result";
        }
        populateSections(model);
        model.addAttribute("employeeForm", form);
        model.addAttribute("error", "従業員の登録に失敗しました。");
        return "admin/employees/form";
    }

    @PostMapping("/action")
    public String selectEmployeeAction(@RequestParam(value = "employeeCode", required = false) String employeeCode,
                                       @RequestParam("action") String action,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        if (employeeCode == null || employeeCode.isBlank()) {
            redirectAttributes.addAttribute("error", "従業員を選択してください。");
            return "redirect:/admin/employees";
        }
        if ("edit".equals(action)) {
            return "redirect:/admin/employees/" + employeeCode + "/edit";
        }
        if ("delete".equals(action)) {
            return "redirect:/admin/employees/" + employeeCode + "/delete";
        }
        redirectAttributes.addAttribute("error", "不明な操作です。");
        return "redirect:/admin/employees";
    }

    @GetMapping("/{employeeCode}/edit")
    public String showEditForm(@PathVariable String employeeCode,
                               HttpSession session,
                               Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        Optional<Employee> employeeOpt = adminService.findEmployee(employeeCode);
        if (employeeOpt.isEmpty()) {
            return "redirect:/admin/employees?error=notfound";
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
        model.addAttribute("employeeForm", form);
        populateSections(model);
        model.addAttribute("mode", "edit");
        return "admin/employees/form";
    }

    @PostMapping("/{employeeCode}/edit")
    public String updateEmployee(@PathVariable String employeeCode,
                                 @ModelAttribute("employeeForm") EmployeeForm form,
                                 HttpSession session,
                                 Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        form.setEmployeeCode(employeeCode);
        String validationError = validateEmployeeForm(form, false);
        if (validationError != null) {
            populateSections(model);
            model.addAttribute("employeeForm", form);
            model.addAttribute("mode", "edit");
            model.addAttribute("error", validationError);
            return "admin/employees/form";
        }
        boolean updated = adminService.updateEmployee(form.toExistingEmployee());
        if (updated) {
            model.addAttribute("title", "更新完了");
            model.addAttribute("message", "従業員情報を更新しました。");
            model.addAttribute("backLink", "/admin/employees");
            model.addAttribute("backText", "一覧に戻る");
            return "admin/result";
        }
        populateSections(model);
        model.addAttribute("employeeForm", form);
        model.addAttribute("mode", "edit");
        model.addAttribute("error", "従業員情報の更新に失敗しました。");
        return "admin/employees/form";
    }

    @GetMapping("/{employeeCode}/delete")
    public String confirmDelete(@PathVariable String employeeCode,
                                HttpSession session,
                                Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        Optional<Employee> employeeOpt = adminService.findEmployee(employeeCode);
        if (employeeOpt.isEmpty()) {
            return "redirect:/admin/employees?error=notfound";
        }
        model.addAttribute("employee", employeeOpt.get());
        return "admin/employees/delete";
    }

    @PostMapping("/{employeeCode}/delete")
    public String deleteEmployee(@PathVariable String employeeCode,
                                 HttpSession session,
                                 Model model) {
        if (!isLoggedIn(session)) {
            return "redirect:/admin/login";
        }
        boolean deleted = adminService.deleteEmployee(employeeCode);
        if (deleted) {
            model.addAttribute("title", "削除完了");
            model.addAttribute("message", "従業員を削除しました。");
            model.addAttribute("backLink", "/admin/employees");
            model.addAttribute("backText", "一覧に戻る");
            return "admin/result";
        }
        model.addAttribute("title", "削除失敗");
        model.addAttribute("message", "従業員の削除に失敗しました。");
        model.addAttribute("backLink", "/admin/employees");
        model.addAttribute("backText", "一覧に戻る");
        return "admin/result";
    }

    private boolean isLoggedIn(HttpSession session) {
        return session.getAttribute("loginUserId") != null;
    }

    private void populateSections(Model model) {
        List<Section> sections = adminService.listSections();
        model.addAttribute("sections", sections);
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
}
