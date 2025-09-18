package com.example.attendance.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理者ユーザーモデルクラス。
 * @author Shuncub
 */
@Data               // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor  // 引数なしコンストラクタを自動生成
public class User {

	/** ユーザーID。 */
	private String userId;

	/** パスワード。 */
	private String password;
}
