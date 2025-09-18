package com.example.attendance.controller;

import com.example.attendance.controller.form.AdminLoginForm;
import com.example.attendance.controller.form.AdminUserForm;
import com.example.attendance.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public String showLogin(HttpSession session,
                            @RequestParam(value = "error", required = false) String error,
                            Model model) {
        if (session.getAttribute("loginUserId") != null) {
            return "redirect:/admin/menu";
        }
        model.addAttribute("loginForm", new AdminLoginForm());
        model.addAttribute("error", error);
        return "admin/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute("loginForm") AdminLoginForm form,
                        HttpSession session) {
        boolean authenticated = adminService.authenticateAdmin(form.getUserId(), form.getPassword());
        if (authenticated) {
            session.setAttribute("loginUserId", form.getUserId());
            return "redirect:/admin/menu";
        }
        return "redirect:/admin/login?error=fail";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("loginUserId");
        return "redirect:/";
    }

    @GetMapping("/menu")
    public String menu(HttpSession session) {
        if (session.getAttribute("loginUserId") == null) {
            return "redirect:/admin/login";
        }
        return "admin/menu";
    }

    @GetMapping("/users/new")
    public String showAdminUserForm(HttpSession session, Model model) {
        if (session.getAttribute("loginUserId") == null) {
            return "redirect:/admin/login";
        }
        model.addAttribute("userForm", new AdminUserForm());
        return "admin/register-admin";
    }

    @PostMapping("/users")
    public String registerAdmin(@ModelAttribute("userForm") AdminUserForm form,
                                 HttpSession session,
                                 Model model) {
        if (session.getAttribute("loginUserId") == null) {
            return "redirect:/admin/login";
        }
        if (form.getUserId() == null || form.getUserId().isBlank() ||
            form.getPassword() == null || form.getPassword().isBlank()) {
            model.addAttribute("userForm", form);
            model.addAttribute("error", "入力内容に誤りがあります。");
            return "admin/register-admin";
        }
        if (!form.getPassword().equals(form.getConfirmation())) {
            model.addAttribute("userForm", form);
            model.addAttribute("error", "パスワードが一致しません。");
            return "admin/register-admin";
        }
        boolean registered = adminService.registerAdmin(form.getUserId(), form.getPassword());
        if (registered) {
            session.setAttribute("loginUserId", form.getUserId());
            model.addAttribute("title", "登録完了");
            model.addAttribute("message", "管理者ユーザーを登録しました。");
            model.addAttribute("backLink", "/admin/menu");
            model.addAttribute("backText", "メニューに戻る");
            return "admin/result";
        }
        model.addAttribute("userForm", form);
        model.addAttribute("error", "登録に失敗しました。既に同じユーザーIDが存在する可能性があります。");
        return "admin/register-admin";
    }
    @GetMapping("/dev-login")
    public String devLogin(HttpSession session) {
        session.setAttribute("loginUserId", "devUser");
        return "redirect:/admin/menu";
    }


}
