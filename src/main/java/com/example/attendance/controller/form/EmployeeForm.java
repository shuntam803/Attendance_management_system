package com.example.attendance.controller.form;

import com.example.attendance.model.Employee;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

/**
 * 従業員フォームクラス。
 */
@Data                   // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor      // 引数なしコンストラクタを自動生成
@AllArgsConstructor     // 全フィールド引数付きコンストラクタを自動生成
public class EmployeeForm {

    private String employeeCode;
    private String lastName;
    private String firstName;
    private String lastKanaName;
    private String firstKanaName;
    private String gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDay;

    private String sectionCode;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate hireDate;

    private String password;
    private String confirmation;

    /** 新規従業員エンティティに変換 */
    public Employee toNewEmployee() {
        Employee employee = new Employee();
        employee.setLastName(lastName);
        employee.setFirstName(firstName);
        employee.setLastKanaName(lastKanaName);
        employee.setFirstKanaName(firstKanaName);
        employee.setGender(Integer.parseInt(gender));
        employee.setBirthDay(birthDay);
        employee.setSectionCode(sectionCode);
        employee.setHireDate(hireDate);
        employee.setPassword(password);
        return employee;
    }

    /** 既存従業員エンティティに変換 */
    public Employee toExistingEmployee() {
        Employee employee = toNewEmployee();
        employee.setEmployeeCode(employeeCode);
        return employee;
    }

    /** gender を int に変換（数値変換できなければ 0 にフォールバック） */
    public int parseGender() {
        try {
            return Integer.parseInt(gender);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
