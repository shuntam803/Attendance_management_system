package com.example.attendance.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

    @GetMapping("/api/session/clear")
    public ResponseEntity<IndexResponse> clearSession(HttpSession session) {
        session.removeAttribute("loginUserId");
        session.removeAttribute("employeeCode");
        return ResponseEntity.ok(new IndexResponse("セッション情報をクリアしました。"));
    }

    public static record IndexResponse(String message) {}
}
