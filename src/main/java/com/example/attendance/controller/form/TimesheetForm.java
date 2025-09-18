package com.example.attendance.controller.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor      // 引数なしコンストラクタを自動生成
@AllArgsConstructor     // 全フィールド引数付きコンストラクタを自動生成
public class TimesheetForm {
    private String month;
}
