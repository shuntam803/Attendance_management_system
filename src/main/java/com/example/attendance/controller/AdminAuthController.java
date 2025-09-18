package com.example.attendance.controller;

import com.example.attendance.controller.form.AdminLoginForm;
import com.example.attendance.controller.form.AdminUserForm;
import com.example.attendance.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/login")
    public ResponseEntity<AdminLoginStatusResponse> showLogin(HttpSession session,
                                                              @RequestParam(value = "error", required = false) String error) {
        boolean loggedIn = session.getAttribute("loginUserId") != null;
        return ResponseEntity.ok(new AdminLoginStatusResponse(loggedIn, error));
    }

    @PostMapping("/login")
    public ResponseEntity<AdminLoginResponse> login(@RequestBody AdminLoginForm form,
                                                    HttpSession session) {
        boolean authenticated = adminService.authenticateAdmin(form.getUserId(), form.getPassword());
        if (authenticated) {
            session.setAttribute("loginUserId", form.getUserId());
            return ResponseEntity.ok(new AdminLoginResponse(true, null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new AdminLoginResponse(false, "認証に失敗しました。"));
    }

    @PostMapping("/logout")
    public ResponseEntity<SimpleMessageResponse> logout(HttpSession session) {
        session.removeAttribute("loginUserId");
        return ResponseEntity.ok(new SimpleMessageResponse("ログアウトしました。"));
    }

    @GetMapping("/menu")
    public ResponseEntity<MenuResponse> menu(HttpSession session) {
        boolean loggedIn = session.getAttribute("loginUserId") != null;
        if (!loggedIn) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MenuResponse(false));
        }
        return ResponseEntity.ok(new MenuResponse(true));
    }

    @GetMapping("/users/new")
    public ResponseEntity<AdminUserFormResponse> showAdminUserForm(HttpSession session) {
        if (session.getAttribute("loginUserId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AdminUserFormResponse(null, "ログインしてください。"));
        }
        return ResponseEntity.ok(new AdminUserFormResponse(new AdminUserForm(), null));
    }

    @PostMapping("/users")
    public ResponseEntity<SimpleMessageResponse> registerAdmin(@RequestBody AdminUserForm form,
                                                               HttpSession session) {
        if (session.getAttribute("loginUserId") == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SimpleMessageResponse("ログインしてください。"));
        }
        if (form.getUserId() == null || form.getUserId().isBlank() ||
            form.getPassword() == null || form.getPassword().isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleMessageResponse("入力内容に誤りがあります。"));
        }
        if (!form.getPassword().equals(form.getConfirmation())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new SimpleMessageResponse("パスワードが一致しません。"));
        }
        boolean registered = adminService.registerAdmin(form.getUserId(), form.getPassword());
        if (registered) {
            session.setAttribute("loginUserId", form.getUserId());
            return ResponseEntity.ok(new SimpleMessageResponse("管理者ユーザーを登録しました。"));
        }
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new SimpleMessageResponse("登録に失敗しました。既に同じユーザーIDが存在する可能性があります。"));
    }
    @GetMapping("/dev-login")
    public ResponseEntity<SimpleMessageResponse> devLogin(HttpSession session) {
        session.setAttribute("loginUserId", "devUser");
        return ResponseEntity.ok(new SimpleMessageResponse("開発用ログインを実施しました。"));
    }

    public static record AdminLoginStatusResponse(boolean loggedIn, String error) {}

    public static record AdminLoginResponse(boolean success, String message) {}

    public static record SimpleMessageResponse(String message) {}

    public static record MenuResponse(boolean loggedIn) {}

    public static record AdminUserFormResponse(AdminUserForm form, String error) {}
}
