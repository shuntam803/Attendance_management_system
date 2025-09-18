package com.example.attendance.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * 従業員モデルクラス。
 */
@Data // getter, setter, toString, equals, hashCode を自動生成
public class Employee {

	/** 従業員コード。 */
	private String employeeCode;

	/** 氏。 */
	private String lastName;

	/** 名。 */
	private String firstName;

	/** 氏かな。 */
	private String lastKanaName;

	/** 名かな。 */
	private String firstKanaName;

	/** 性別。*/
	private int gender;

	/** 生年月日。 */
	private LocalDate birthDay;

	/** 部署コード。 */
	private String sectionCode;

	/** 入社日。 */
	private LocalDate hireDate;

	/** パスワード。 */
	private String password;

	/** Employeeのコンストラクタ。 */
	public Employee() {}

	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastKanaName() {
		return lastKanaName;
	}
	public void setLastKanaName(String lastKanaName) {
		this.lastKanaName = lastKanaName;
	}

	public String getFirstKanaName() {
		return firstKanaName;
	}
	public void setFirstKanaName(String firstKanaName) {
		this.firstKanaName = firstKanaName;
	}

	public int getGender() {
		return gender;
	}
	public void setGender(int gender) {
		this.gender = gender;
	}

	/** 「男性」「女性」の文字列を受け取って数値に変換するユーティリティ */
	public void setGender(String gender) {
		if ("男性".equals(gender)) {
			this.gender = 0;
		} else if ("女性".equals(gender)) {
			this.gender = 1;
		}
	}

	public LocalDate getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(LocalDate birthDay) {
		this.birthDay = birthDay;
	}

	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public LocalDate getHireDate() {
		return hireDate;
	}
	public void setHireDate(LocalDate hireDate) {
		this.hireDate = hireDate;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
