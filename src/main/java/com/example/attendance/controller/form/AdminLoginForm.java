package com.example.attendance.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理者ログイン用フォーム。
 */
@Data                   // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor      // 引数なしコンストラクタを自動生成
@AllArgsConstructor     // 全フィールドを引数に持つコンストラクタを自動生成
public class AdminLoginForm {
    private String userId;
    private String password;
}
