package com.example.attendance.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 部署モデルクラス。
 * @author Shuncub
 */
@Data                // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor   // 引数なしコンストラクタを自動生成
public class Section {

	/** 部署コード。 */
	private String sectionCode;

	/** 部署名。 */
	private String sectionName;

}
