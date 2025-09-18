package com.example.attendance.model;

import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 従業員一覧画面表示モデルクラス。
 * @author Shuncub
 */
@Data               // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor  // 引数なしコンストラクタを自動生成
public class ViewListDisplay {

	/** 従業員コード。 */
	private String employeeCode;

	/** 氏名。 */
	private String employeeName;

	/** 氏名かな。 */
	private String employeeKanaName;

	/** 性別。 */
	private String gender;

	/** 生年月日。 */
	private Date birthDay;

	/** 部署名。 */
	private String sectionName;

	/** 入社日。 */
	private Date hireDate;

	/**
	 * 数値(0,1)を受け取って gender に対応する文字列をセットする。
	 */
	public void setGender(int gender) {
		if (gender == 0) {
			this.gender = "男性";
		} else {
			this.gender = "女性";
		}
	}
}
