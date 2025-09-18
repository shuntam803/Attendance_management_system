package com.example.attendance.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(HttpSession session) {
        session.removeAttribute("loginUserId");
        session.removeAttribute("employeeCode");
        return "index";
    }
}
