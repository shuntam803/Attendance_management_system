package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

import model.entity.WorkTime;

/**
 * @author Shuncub
 * 画面表示のために出退勤時刻管理データベースと繋ぐDAOクラス。
 */
public class WorkTimeDAO {

	/** 唯一のインスタンスを生成する */
	private static WorkTimeDAO instance = new WorkTimeDAO();

	/** 特定のデータベースとの接続(セッション)。 */
	private Connection conn = null;
	
	/** privateのため新規のインスタンスをつくらせない。 */
	private WorkTimeDAO() {}

	/**
	 * @return ViewListDAOの唯一のインスタンス。
	 * 唯一のインスタンスを取得する。
	 */
	public static WorkTimeDAO getInstance() {
		return instance;
	}

	/**
	 * @throws SQLException データベース処理に問題があった場合。
	 * 特定のデータベースとの接続(セッション)を生成する。
	 */
	public void dbConnect() throws SQLException {
		ConnectionManager cm = ConnectionManager.getInstance();
		conn = cm.connect();
	}

	/**
	 * @throws SQLException データベース処理に問題があった場合。
	 * 静的SQL文を実行し、作成された結果を返すために使用されるオブジェクトを生成する。
	 */
        public void createSt() throws SQLException {
                // PreparedStatement を各メソッド内で生成するため、ここで行う処理はありません。
        }

	/** 特定のデータベースとの接続(セッション)を切断する。 */
	public void dbDiscon() {
		try {
                        if (conn != null)
                                conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param employeeCode - 従業員コード。
	 * @return - 出勤情報が既に存在していたら文字列"disable"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 出勤情報が既に存在しているかチェックする。
	 */
	public String selectStartTime(String employeeCode) throws SQLException {
		String sql = "SELECT 1 FROM t_work_time WHERE employee_code = ? AND work_date = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			ps.setString(2, LocalDate.now().toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return "disable";
				}
			}
		}
		return null;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return 退勤情報が既に存在していたら文字列"disble"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 退勤情報が既に存在しているかチェックする。
	 */
	public String selectFinishTime(String employeeCode) throws SQLException {
		String sql = "SELECT finish_time FROM t_work_time WHERE employee_code = ? AND work_date = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			ps.setString(2, LocalDate.now().toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next() && rs.getString(1) != null) {
					return "disable";
				}
			}
		}
		return null;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return - 休憩開始情報が既に存在していたら文字列"disable"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 休憩開始情報が既に存在しているかチェックする。
	 */
	public String selectStartBreak(String employeeCode) throws SQLException {
		String sql = "SELECT break_start_time FROM t_work_time WHERE employee_code = ? AND work_date = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			ps.setString(2, LocalDate.now().toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next() && rs.getString(1) != null) {
					return "disable";
				}
			}
		}
		return null;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @return 休憩終了情報が既に存在していたら文字列"disable"、存在しなかったらnull。
	 * @throws SQLException。データベース処理に問題があった場合。
	 * 休憩終了情報が既に存在しているかチェックする。
	 */
	public String selectFinishBreak(String employeeCode) throws SQLException {
		String sql = "SELECT break_finish_time FROM t_work_time WHERE employee_code = ? AND work_date = ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			ps.setString(2, LocalDate.now().toString());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next() && rs.getString(1) != null) {
					return "disable";
				}
			}
		}
		return null;
	}

	/**
	 * @param employeeCode 従業員コード。
	 * @param thisMonth 月。
	 * @return 出退勤時刻管理用モデルクラスのリスト。
	 * @throws SQLException。 データベース処理に問題があった場合。
	 * 従業員コードと月から勤務記録を抽出する。
	 */
	public List<WorkTime> selectWorkTimeThisMonthList(String employeeCode,String thisMonth)
			throws SQLException {
		List<WorkTime> workTimeThisMonthList = new LinkedList<WorkTime>();
		String sql = "SELECT * FROM t_work_time WHERE employee_code = ? AND work_date LIKE ?";
		try (PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, employeeCode);
			ps.setString(2, thisMonth + "%");
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					WorkTime workTime = new WorkTime();
					workTime.setWorkDate(LocalDate.parse(rs.getString(2),
						DateTimeFormatter.ofPattern("yyyy-MM-dd")) );
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
					if (rs.getString(3) != null) {
						LocalTime startTime = LocalTime.parse(rs.getString(3), dtf);
						workTime.setStartTime(startTime);
					}
					if (rs.getString(4) != null) {
						LocalTime finishTime = LocalTime.parse(rs.getString(4), dtf);
						workTime.setFinishTime(finishTime);
					}
					if (rs.getString(5) != null) {
						LocalTime breakStartTime = LocalTime.parse(rs.getString(5), dtf);
						workTime.setBreakStartTime(breakStartTime);
					}
					if (rs.getString(6) != null) {
						LocalTime breakFinishTime = LocalTime.parse(rs.getString(6), dtf);
						workTime.setBreakFinishTime(breakFinishTime);
					}
					if (rs.getString(5) != null && rs.getString(6) != null) {
						//自動計算セットするメソッド
						workTime.calcBreakTime();
					}
					if (rs.getString(3) != null && rs.getString(4) != null) {
						//自動計算セットするメソッド
						workTime.calcWorkingHours();
					}
					workTimeThisMonthList.add(workTime);
				}
			}
		}
		return workTimeThisMonthList;
	}


}