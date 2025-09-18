package com.example.attendance.model;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 出退勤時刻管理モデルクラス。
 * @author Shuncub
 */
@Data               // getter, setter, toString, equals, hashCode を自動生成
@NoArgsConstructor  // 引数なしコンストラクタを自動生成
public class WorkTime {

	/** 出勤日。 */
	private LocalDate workDate;

	/** 出勤時刻。 */
	private LocalTime startTime;

	/** 退勤時刻。 */
	private LocalTime finishTime;

	/** 休憩開始時刻。 */
	private LocalTime breakStartTime;

	/** 休憩終了時刻。 */
	private LocalTime breakFinishTime;

	/** 休憩時間。 */
	private Duration breakTime;

	/** 勤務時間。 */
	private Duration workingHours;

	/** 休憩開始時間と休憩終了時間から休憩時間を自動計算する。 */
	public void calcBreakTime() {
		if (breakStartTime != null && breakFinishTime != null) {
			Duration duration = Duration.between(breakStartTime, breakFinishTime);
			setBreakTime(duration);
		}
	}

	/**
	 * 出勤時間と退勤時間から勤務時間を自動計算する。
	 * 休憩時間があるときは勤務時間から休憩時間を引く。
	 */
	public void calcWorkingHours() {
		if (startTime != null && finishTime != null) {
			Duration duration = Duration.between(startTime, finishTime);
			if (breakTime != null) {
				duration = duration.minus(breakTime);
			}
			setWorkingHours(duration);
		}
	}

	// === 表示用フォーマットメソッド ===
	public String getBreakTimeStr() {
		return formatDuration(breakTime, true); // true=分を切り上げる
	}

	public String getWorkingHoursStr() {
		return formatDuration(workingHours, false); // false=分は切り捨て
	}

	private String formatDuration(Duration d, boolean roundUpMinutes) {
		if (d == null) return "";

		long seconds = d.getSeconds();
		long minutes;

		if (roundUpMinutes) {
			// 秒があれば切り上げ
			minutes = (long) Math.ceil(seconds / 60.0);
		} else {
			// 分単位に切り捨て
			minutes = seconds / 60;
		}

		long hours = minutes / 60;
		long remainMinutes = minutes % 60;

		return String.format("%d時間%d分", hours, remainMinutes);
	}
}
